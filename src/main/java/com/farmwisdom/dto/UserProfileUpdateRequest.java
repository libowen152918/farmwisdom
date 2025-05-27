package com.farmwisdom.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserProfileUpdateRequest {
    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Size(max = 200, message = "个人简介不能超过200个字符")
    private String bio;

    private String expertise;
} 