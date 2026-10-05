package com.library.library_management.repository;

import com.library.library_management.entity.Borrowing;
import com.library.library_management.entity.BorrowingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {
    Page<Borrowing> findByStatus(BorrowingStatus status, Pageable pageable);

    @Query("""
            select distinct b from Borrowing b
            join fetch b.member
            left join fetch b.details d
            left join fetch d.book
            where b.id = :id
            """)
    java.util.Optional<Borrowing> findDetailById(Long id);

    List<Borrowing> findByStatusNotAndDueDateBeforeOrderByDueDateAsc(BorrowingStatus status, LocalDate today);
}
