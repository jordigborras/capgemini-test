package com.capgemini.test.code.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {

    @NotBlank
    @Size(max = 6)
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String dni;

    @NotBlank
    private String phone;

    @NotBlank
    @Pattern(regexp = "admin|superadmin", message = "Role must be admin or superadmin")
    private String role;
}