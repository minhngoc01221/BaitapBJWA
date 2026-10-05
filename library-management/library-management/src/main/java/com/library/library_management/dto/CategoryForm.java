package com.library.library_management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryForm {
    private Long id;

    @NotBlank(message = "Tên thể loại không được rỗng")
    private String name;

    private String description;
    private Boolean active = true;
}
