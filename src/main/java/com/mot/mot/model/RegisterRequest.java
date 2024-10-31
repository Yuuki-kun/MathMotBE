package com.mot.mot.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Tên không được để trống.")
    @Length(min = 3, max = 255, message = "Tên phải có ít nhất 3 ký tự.")
    private String fullName;

    @Email(message = "Email không hợp lệ.")
    @NotBlank(message = "Email không được để trống.")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*]).{6,}$",
            message = "Mật khẩu phải có ít nhất 6 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt (!@#$%^&*).")

    private String password;

    @NotBlank(message = "Role không được để trống.")
    private String role;
}