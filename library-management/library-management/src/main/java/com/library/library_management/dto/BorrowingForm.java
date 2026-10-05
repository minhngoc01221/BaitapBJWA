package com.library.library_management.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BorrowingForm {
    @NotNull(message = "Vui lòng chọn thành viên")
    private Long memberId;

    @Valid
    private List<BorrowingItemForm> items = new ArrayList<>();
}
