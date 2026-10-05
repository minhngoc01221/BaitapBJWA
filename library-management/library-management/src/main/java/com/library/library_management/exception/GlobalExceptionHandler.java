package com.library.library_management.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({BusinessException.class, ResourceNotFoundException.class})
    public String handleBusinessError(RuntimeException exception, Model model) {
        model.addAttribute("message", exception.getMessage());
        return "error";
    }
}
