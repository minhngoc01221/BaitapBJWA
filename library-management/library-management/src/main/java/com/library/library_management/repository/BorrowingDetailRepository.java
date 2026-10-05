package com.library.library_management.repository;

import com.library.library_management.entity.BorrowingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BorrowingDetailRepository extends JpaRepository<BorrowingDetail, Long> {
    boolean existsByBook_IdAndReturnedQuantityLessThan(Long bookId, Integer quantity);

    @Query(value = """
            select b.title, sum(d.quantity)
            from borrowing_details d
            join books b on b.id = d.book_id
            group by b.id, b.title
            order by sum(d.quantity) desc
            limit 5
            """, nativeQuery = true)
    List<Object[]> findTopBorrowedBooks();

    @Query("""
            select b.member.fullName, count(b.id)
            from Borrowing b
            group by b.member.id, b.member.fullName
            order by count(b.id) desc
            """)
    List<Object[]> countBorrowingsByMember();
}
