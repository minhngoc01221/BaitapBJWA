package com.library.library_management.controller;

import com.library.library_management.dto.MemberForm;
import com.library.library_management.entity.MemberStatus;
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
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        model.addAttribute("members", memberService.search(keyword, PageRequest.of(page, size, Sort.by("createdAt").descending())));
        model.addAttribute("keyword", keyword);
        return "members/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        prepareForm(model, new MemberForm());
        return "members/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        prepareForm(model, memberService.toForm(memberService.findById(id)));
        return "members/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("member") MemberForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            prepareForm(model, form);
            return "members/form";
        }
        memberService.save(form);
        return "redirect:/members";
    }

    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable Long id, @RequestParam MemberStatus status) {
        memberService.changeStatus(id, status);
        return "redirect:/members";
    }

    private void prepareForm(Model model, MemberForm form) {
        model.addAttribute("member", form);
        model.addAttribute("statuses", MemberStatus.values());
    }
}
