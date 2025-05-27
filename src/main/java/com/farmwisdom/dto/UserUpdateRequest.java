package com.farmwisdom.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String username;
    private String email;
    private String role;
    private String avatar;
    private String bio;
    private Integer reputation;
} 