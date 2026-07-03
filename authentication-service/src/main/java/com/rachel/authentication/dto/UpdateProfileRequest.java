package com.rachel.authentication.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {

    private String username;

    private String email;

    private String phone;

    private String profileImage;

}