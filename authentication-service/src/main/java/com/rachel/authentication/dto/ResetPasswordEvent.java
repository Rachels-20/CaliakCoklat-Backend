package com.rachel.authentication.dto;

import lombok.Data;

@Data
public class ResetPasswordEvent {

    private String email;

    private String code;

}