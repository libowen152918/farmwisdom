package com.farmwisdom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private String role;
    private String bio;
    private String expertise;
    private Integer reputation;
    private Integer postCount;
    private Integer commentCount;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
    private Boolean isEnabled;
    private Boolean isLocked;

    private String token; // JWT token，仅在登录时返回
}