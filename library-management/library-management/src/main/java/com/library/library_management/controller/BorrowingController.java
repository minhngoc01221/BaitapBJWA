package com.library.library_management.controller;

import com.library.library_management.dto.BorrowingForm;
import com.library.library_management.dto.BorrowingItemForm;
import com.library.library_management.service.BookService;
import com.library.library_management.service.BorrowingService;
import com.library.library_management.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/borrowings")
@RequiredArgsConstructor
public class BorrowingController {
    private final BorrowingService borrowingService;
    private final MemberService memberService;
    private final BookService bookService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        model.addAttribute("borrowings", borrowingService.findAll(PageRequest.of(page, size, Sort.by("id").descending())));
        return "borrowings/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        BorrowingForm form = new BorrowingForm();
        form.getItems().add(new BorrowingItemForm());
        form.getItems().add(new BorrowingItemForm());
        form.getItems().add(new BorrowingItemForm());
        prepareForm(model, form);
        return "borrowings/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("borrowing") BorrowingForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            prepareForm(model, form);
            return "borrowings/form";
        }
        borrowingService.create(form);
        return "redirect:/borrowings";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("borrowing", borrowingService.findDetail(id));
        return "borrowings/detail";
    }

    @PostMapping("/{id}/return")
    public String returnAll(@PathVariable Long id) {
        borrowingService.returnAll(id);
        return "redirect:/borrowings/" + id;
    }

    @PostMapping("/{id}/return-detail")
    public String returnDetail(@PathVariable Long id, @RequestParam Long detailId, @RequestParam Integer quantity) {
        borrowingService.returnDetail(id, detailId, quantity);
        return "redirect:/borrowings/" + id;
    }

    private void prepareForm(Model model, BorrowingForm form) {
        model.addAttribute("borrowing", form);
        model.addAttribute("members", memberService.findActiveMembers());
        model.addAttribute("books", bookService.findBorrowableBooks());
    }
}
