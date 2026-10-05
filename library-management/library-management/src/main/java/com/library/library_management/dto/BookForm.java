package com.library.library_management.dto;

import com.library.library_management.entity.BookStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookForm {
    private Long id;

    @NotBlank(message = "ISBN không được rỗng")
    private String isbn;

    @NotBlank(message = "Tên sách không được rỗng")
    private String title;

    @NotBlank(message = "Tác giả không được rỗng")
    private String author;

    @NotNull(message = "Vui lòng chọn thể loại")
    private Long categoryId;

    @NotNull(message = "Tổng số lượng là bắt buộc")
    @Min(value = 0, message = "Tổng số lượng không được âm")
    private Integer totalQuantity;

    @NotNull(message = "Số lượng còn lại là bắt buộc")
    @Min(value = 0, message = "Số lượng còn lại không được âm")
    private Integer availableQuantity;

    private BookStatus status = BookStatus.ACTIVE;
}
