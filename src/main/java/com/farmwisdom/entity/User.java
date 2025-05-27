package com.farmwisdom.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.farmwisdom.enums.UserRole;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("users")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("nickname")
    private String nickname;

    @TableField("email")
    private String email;

    @TableField("phone")
    private String phone;

    @TableField("avatar")
    private String avatar;

    @TableField("role")
    private String role;

    @TableField("bio")
    private String bio;

    @TableField("expertise")
    private String expertise;

    @TableField("reputation")
    private Integer reputation;

    @TableField("post_count")
    private Integer postCount;

    @TableField("comment_count")
    private Integer commentCount;

    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    @TableField("is_enabled")
    private Boolean isEnabled;

    @TableField("is_locked")
    private Boolean isLocked;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    public boolean isAdmin() {
        return UserRole.ROLE_ADMIN.name().equals(this.role);
    }

    public boolean isExpert() {
        return UserRole.ROLE_EXPERT.name().equals(this.role);
    }
}