package com.library.library_management.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BorrowingItemForm {
    @NotNull(message = "Vui lòng chọn sách")
    private Long bookId;

    @NotNull(message = "Số lượng là bắt buộc")
    @Min(value = 1, message = "Số lượng mượn tối thiểu là 1")
    private Integer quantity = 1;
}
