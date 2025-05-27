package com.farmwisdom.enums;

public enum UserRole {
    ROLE_USER("普通用户"),
    ROLE_EXPERT("专家用户"),
    ROLE_ADMIN("管理员");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
