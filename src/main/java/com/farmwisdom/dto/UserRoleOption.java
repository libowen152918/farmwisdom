package com.farmwisdom.dto;

import com.farmwisdom.enums.UserRole;
import lombok.Data;

@Data
public class UserRoleOption {
    private String value;
    private String label;

    public static UserRoleOption from(UserRole role) {
        UserRoleOption option = new UserRoleOption();
        option.setValue(role.name());
        option.setLabel(role.getDescription());
        return option;
    }
} 