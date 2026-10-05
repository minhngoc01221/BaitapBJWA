package com.library.library_management.service;

import com.library.library_management.entity.Borrowing;
import com.library.library_management.repository.BorrowingDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final BorrowingService borrowingService;
    private final BorrowingDetailRepository borrowingDetailRepository;

    public List<Borrowing> overdueBorrowings() {
        return borrowingService.findOverdue();
    }

    public List<Object[]> topBorrowedBooks() {
        return borrowingDetailRepository.findTopBorrowedBooks();
    }

    public List<Object[]> borrowingsByMember() {
        return borrowingDetailRepository.countBorrowingsByMember();
    }
}
