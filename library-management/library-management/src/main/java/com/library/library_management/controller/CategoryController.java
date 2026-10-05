package com.library.library_management.controller;

import com.library.library_management.dto.CategoryForm;
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
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        model.addAttribute("categories", categoryService.search(keyword, PageRequest.of(page, size, Sort.by("id").descending())));
        model.addAttribute("keyword", keyword);
        return "categories/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("category", new CategoryForm());
        return "categories/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.toForm(categoryService.findById(id)));
        return "categories/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("category") CategoryForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "categories/form";
        }
        categoryService.save(form);
        return "redirect:/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        categoryService.softDelete(id);
        return "redirect:/categories";
    }
}
