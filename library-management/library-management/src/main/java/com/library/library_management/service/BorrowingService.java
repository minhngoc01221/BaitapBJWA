package com.library.library_management.service;

import com.library.library_management.dto.BorrowingForm;
import com.library.library_management.dto.BorrowingItemForm;
import com.library.library_management.entity.*;
import com.library.library_management.exception.BusinessException;
import com.library.library_management.exception.ResourceNotFoundException;
import com.library.library_management.repository.BookRepository;
import com.library.library_management.repository.BorrowingRepository;
import com.library.library_management.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowingService {
    public static final BigDecimal FINE_PER_DAY = BigDecimal.valueOf(5000);

    private final BorrowingRepository borrowingRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;

    public Page<Borrowing> findAll(Pageable pageable) {
        return borrowingRepository.findAll(pageable);
    }

    public Borrowing findDetail(Long id) {
        return borrowingRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu mượn"));
    }

    public List<Borrowing> findOverdue() {
        return borrowingRepository.findByStatusNotAndDueDateBeforeOrderByDueDateAsc(BorrowingStatus.RETURNED, LocalDate.now());
    }

    @Transactional
    public Borrowing create(BorrowingForm form) {
        if (form.getItems() == null || form.getItems().isEmpty()) {
            throw new BusinessException("Phiếu mượn cần ít nhất một cuốn sách");
        }

        Member member = memberRepository.findById(form.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thành viên"));
        if (member.getStatus() == MemberStatus.BLOCKED) {
            throw new BusinessException("Thành viên đang bị khóa không được mượn sách");
        }

        LocalDate borrowDate = LocalDate.now();
        Borrowing borrowing = Borrowing.builder()
                .member(member)
                .borrowDate(borrowDate)
                .dueDate(borrowDate.plusDays(14))
                .status(BorrowingStatus.BORROWED)
                .totalFine(BigDecimal.ZERO)
                .build();

        for (BorrowingItemForm item : form.getItems()) {
            if (item.getBookId() == null || item.getQuantity() == null || item.getQuantity() <= 0) {
                continue;
            }
            Book book = bookRepository.findById(item.getBookId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách"));
            validateBorrowable(book, item.getQuantity());
            book.setAvailableQuantity(book.getAvailableQuantity() - item.getQuantity());
            borrowing.addDetail(BorrowingDetail.builder()
                    .book(book)
                    .quantity(item.getQuantity())
                    .returnedQuantity(0)
                    .fineAmount(BigDecimal.ZERO)
                    .build());
        }

        if (borrowing.getDetails().isEmpty()) {
            throw new BusinessException("Phiếu mượn cần ít nhất một dòng sách hợp lệ");
        }
        return borrowingRepository.save(borrowing);
    }

    @Transactional
    public void returnAll(Long borrowingId) {
        Borrowing borrowing = findDetail(borrowingId);
        for (BorrowingDetail detail : borrowing.getDetails()) {
            returnDetail(detail, detail.remainingQuantity(), borrowing.getDueDate());
        }
        finishReturn(borrowing);
    }

    @Transactional
    public void returnDetail(Long borrowingId, Long detailId, Integer quantity) {
        Borrowing borrowing = findDetail(borrowingId);
        BorrowingDetail detail = borrowing.getDetails().stream()
                .filter(item -> item.getId().equals(detailId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dòng mượn"));
        returnDetail(detail, quantity, borrowing.getDueDate());
        finishReturn(borrowing);
    }

    private void validateBorrowable(Book book, int quantity) {
        if (book.getStatus() != BookStatus.ACTIVE) {
            throw new BusinessException("Không thể mượn sách không ở trạng thái ACTIVE");
        }
        if (book.getAvailableQuantity() < quantity) {
            throw new BusinessException("Số lượng sách còn lại không đủ: " + book.getTitle());
        }
    }

    private void returnDetail(BorrowingDetail detail, int quantity, LocalDate dueDate) {
        if (quantity <= 0) {
            return;
        }
        if (quantity > detail.remainingQuantity()) {
            throw new BusinessException("Số lượng trả vượt quá số lượng đang mượn");
        }
        detail.setReturnedQuantity(detail.getReturnedQuantity() + quantity);
        detail.getBook().setAvailableQuantity(detail.getBook().getAvailableQuantity() + quantity);

        long lateDays = Math.max(0, ChronoUnit.DAYS.between(dueDate, LocalDate.now()));
        BigDecimal fine = FINE_PER_DAY.multiply(BigDecimal.valueOf(lateDays)).multiply(BigDecimal.valueOf(quantity));
        detail.setFineAmount(detail.getFineAmount().add(fine));
    }

    private void finishReturn(Borrowing borrowing) {
        BigDecimal totalFine = borrowing.getDetails().stream()
                .map(BorrowingDetail::getFineAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        borrowing.setTotalFine(totalFine);

        boolean allReturned = borrowing.getDetails().stream().allMatch(detail -> detail.remainingQuantity() == 0);
        if (allReturned) {
            borrowing.setStatus(BorrowingStatus.RETURNED);
            borrowing.setReturnedDate(LocalDate.now());
        } else if (borrowing.getDueDate().isBefore(LocalDate.now())) {
            borrowing.setStatus(BorrowingStatus.OVERDUE);
        } else {
            borrowing.setStatus(BorrowingStatus.PARTIALLY_RETURNED);
        }
    }
}
