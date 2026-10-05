package com.library.library_management.controller;

import com.library.library_management.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/overdue")
    public String overdue(Model model) {
        model.addAttribute("borrowings", reportService.overdueBorrowings());
        return "reports/overdue";
    }

    @GetMapping
    public String summary(Model model) {
        model.addAttribute("topBooks", reportService.topBorrowedBooks());
        model.addAttribute("memberCounts", reportService.borrowingsByMember());
        return "reports/summary";
    }
}
