package com.library.library_management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "borrowing_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowingDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrowing_id", nullable = false)
    private Borrowing borrowing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "returned_quantity", nullable = false)
    private Integer returnedQuantity;

    @Column(name = "fine_amount", nullable = false)
    private BigDecimal fineAmount;

    public int remainingQuantity() {
        return quantity - returnedQuantity;
    }
}
