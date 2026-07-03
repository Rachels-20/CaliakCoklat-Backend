package com.rachel.authentication.dto;

import lombok.Data;

@Data
public class ForgotPasswordEvent {

    private String email;
    private String otp;
}