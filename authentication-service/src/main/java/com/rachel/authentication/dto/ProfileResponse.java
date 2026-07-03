package com.rachel.authentication.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProfileResponse {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private String profileImage;
}
