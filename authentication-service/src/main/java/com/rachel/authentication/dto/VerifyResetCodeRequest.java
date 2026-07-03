package com.rachel.authentication.dto;

import lombok.Data;

@Data
public class VerifyResetCodeRequest {

    private String email;

    private String code;

}