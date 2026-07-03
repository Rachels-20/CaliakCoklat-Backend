package com.rachel.authentication.register;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    @Pattern(regexp = "^(08|628)[0-9]{8,13}$", message = "Format nomor harus 08xxxxxxxx atau 628xxxxxxxx")
    private String phone;
}