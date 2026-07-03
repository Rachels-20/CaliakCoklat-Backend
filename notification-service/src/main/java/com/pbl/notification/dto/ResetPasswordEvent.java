package com.pbl.notification.dto;

import lombok.Data;

@Data
public class ResetPasswordEvent {

    private String email;

    private String code;
}