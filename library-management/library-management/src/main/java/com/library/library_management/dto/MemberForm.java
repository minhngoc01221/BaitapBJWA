package com.library.library_management.dto;

import com.library.library_management.entity.MemberStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberForm {
    private Long id;

    @NotBlank(message = "Họ tên không được rỗng")
    private String fullName;

    @NotBlank(message = "Email không được rỗng")
    @Email(message = "Email không hợp lệ")
    private String email;

    private String phone;
    private MemberStatus status = MemberStatus.ACTIVE;
}
