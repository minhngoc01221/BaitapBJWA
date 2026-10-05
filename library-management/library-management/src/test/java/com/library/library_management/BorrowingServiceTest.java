package com.library.library_management;

import com.library.library_management.dto.BorrowingForm;
import com.library.library_management.dto.BorrowingItemForm;
import com.library.library_management.entity.*;
import com.library.library_management.exception.BusinessException;
import com.library.library_management.repository.BookRepository;
import com.library.library_management.repository.BorrowingRepository;
import com.library.library_management.repository.MemberRepository;
import com.library.library_management.service.BorrowingService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BorrowingServiceTest {

    private final BorrowingRepository borrowingRepository = mock(BorrowingRepository.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final BookRepository bookRepository = mock(BookRepository.class);
    private final BorrowingService borrowingService = new BorrowingService(borrowingRepository, memberRepository, bookRepository);

    @Test
    void createBorrowingSubtractsAvailableQuantity() {
        Member member = Member.builder().id(1L).status(MemberStatus.ACTIVE).build();
        Book book = Book.builder().id(2L).title("Clean Code").status(BookStatus.ACTIVE).totalQuantity(5).availableQuantity(3).build();
        BorrowingForm form = new BorrowingForm();
        form.setMemberId(1L);
        BorrowingItemForm item = new BorrowingItemForm();
        item.setBookId(2L);
        item.setQuantity(2);
        form.getItems().add(item);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book));
        when(borrowingRepository.save(any(Borrowing.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Borrowing borrowing = borrowingService.create(form);

        assertThat(book.getAvailableQuantity()).isEqualTo(1);
        assertThat(borrowing.getDueDate()).isEqualTo(borrowing.getBorrowDate().plusDays(14));
        assertThat(borrowing.getDetails()).hasSize(1);
        verify(borrowingRepository).save(any(Borrowing.class));
    }

    @Test
    void blockedMemberCannotBorrow() {
        Member member = Member.builder().id(1L).status(MemberStatus.BLOCKED).build();
        BorrowingForm form = new BorrowingForm();
        form.setMemberId(1L);
        form.getItems().add(new BorrowingItemForm());

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> borrowingService.create(form))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("bị khóa");
        verifyNoInteractions(bookRepository, borrowingRepository);
    }
}
