package com.library.library_management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "borrowings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Borrowing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "borrow_date", nullable = false)
    private LocalDate borrowDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "returned_date")
    private LocalDate returnedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BorrowingStatus status;

    @Column(name = "total_fine", nullable = false)
    private BigDecimal totalFine;

    @Builder.Default
    @OneToMany(mappedBy = "borrowing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BorrowingDetail> details = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (borrowDate == null) {
            borrowDate = LocalDate.now();
        }
        if (dueDate == null) {
            dueDate = borrowDate.plusDays(14);
        }
        if (status == null) {
            status = BorrowingStatus.BORROWED;
        }
        if (totalFine == null) {
            totalFine = BigDecimal.ZERO;
        }
    }

    public void addDetail(BorrowingDetail detail) {
        details.add(detail);
        detail.setBorrowing(this);
    }
}
