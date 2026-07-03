package com.rachel.authentication.login;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
