package com.library.library_management.controller;

import com.library.library_management.dto.BookForm;
import com.library.library_management.entity.BookStatus;
import com.library.library_management.service.BookService;
import com.library.library_management.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final CategoryService categoryService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        model.addAttribute("books", bookService.search(keyword, PageRequest.of(page, size, Sort.by("id").descending())));
        model.addAttribute("keyword", keyword);
        return "books/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        prepareForm(model, new BookForm());
        return "books/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        prepareForm(model, bookService.toForm(bookService.findById(id)));
        return "books/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("book") BookForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            prepareForm(model, form);
            return "books/form";
        }
        bookService.save(form);
        return "redirect:/books";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        bookService.softDelete(id);
        return "redirect:/books";
    }

    private void prepareForm(Model model, BookForm form) {
        model.addAttribute("book", form);
        model.addAttribute("categories", categoryService.findActiveCategories());
        model.addAttribute("statuses", BookStatus.values());
    }
}
