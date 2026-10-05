package com.library.library_management.service;

import com.library.library_management.dto.BookForm;
import com.library.library_management.entity.Book;
import com.library.library_management.entity.BookStatus;
import com.library.library_management.entity.Category;
import com.library.library_management.exception.BusinessException;
import com.library.library_management.exception.ResourceNotFoundException;
import com.library.library_management.repository.BookRepository;
import com.library.library_management.repository.BorrowingDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final CategoryService categoryService;
    private final BorrowingDetailRepository borrowingDetailRepository;

    public Page<Book> search(String keyword, Pageable pageable) {
        return bookRepository.search(keyword, pageable);
    }

    public List<Book> findBorrowableBooks() {
        return bookRepository.findByStatusOrderByTitleAsc(BookStatus.ACTIVE);
    }

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách"));
    }

    @Transactional
    public Book save(BookForm form) {
        validateQuantity(form.getTotalQuantity(), form.getAvailableQuantity());
        Long id = form.getId() == null ? 0L : form.getId();
        if (bookRepository.existsByIsbnIgnoreCaseAndIdNot(form.getIsbn().trim(), id)) {
            throw new BusinessException("ISBN đã tồn tại");
        }

        Category category = categoryService.findById(form.getCategoryId());
        Book book = form.getId() == null ? new Book() : findById(form.getId());
        book.setIsbn(form.getIsbn().trim());
        book.setTitle(form.getTitle().trim());
        book.setAuthor(form.getAuthor().trim());
        book.setCategory(category);
        book.setTotalQuantity(form.getTotalQuantity());
        book.setAvailableQuantity(form.getAvailableQuantity());
        book.setStatus(form.getStatus() == null ? BookStatus.ACTIVE : form.getStatus());
        return bookRepository.save(book);
    }

    @Transactional
    public void softDelete(Long id) {
        if (borrowingDetailRepository.existsByBook_IdAndReturnedQuantityLessThan(id, Integer.MAX_VALUE)) {
            Book book = findById(id);
            if (book.getAvailableQuantity() < book.getTotalQuantity()) {
                throw new BusinessException("Không thể xóa sách đang có lượt mượn chưa trả");
            }
        }
        Book book = findById(id);
        book.setStatus(BookStatus.DELETED);
    }

    public BookForm toForm(Book book) {
        BookForm form = new BookForm();
        form.setId(book.getId());
        form.setIsbn(book.getIsbn());
        form.setTitle(book.getTitle());
        form.setAuthor(book.getAuthor());
        form.setCategoryId(book.getCategory().getId());
        form.setTotalQuantity(book.getTotalQuantity());
        form.setAvailableQuantity(book.getAvailableQuantity());
        form.setStatus(book.getStatus());
        return form;
    }

    void validateQuantity(Integer totalQuantity, Integer availableQuantity) {
        if (totalQuantity == null || availableQuantity == null) {
            return;
        }
        if (availableQuantity > totalQuantity) {
            throw new BusinessException("Số lượng còn lại không được lớn hơn tổng số lượng");
        }
    }
}
