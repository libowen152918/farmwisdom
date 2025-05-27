package com.farmwisdom.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.farmwisdom.dto.UserResponse;

public interface UserManagementService {
    /**
     * 获取用户列表
     * @param page 页码
     * @param size 每页大小
     * @param username 用户名（可选）
     * @param email 邮箱（可选）
     * @return 用户列表分页数据
     */
    Page<UserResponse> getUserList(Integer page, Integer size, String username, String email);

    /**
     * 根据ID获取用户
     * @param id 用户ID
     * @return 用户信息
     */
    UserResponse getUserById(Long id);

    /**
     * 更新用户状态
     * @param id 用户ID
     * @param enabled 是否启用
     */
    void updateUserStatus(Long id, Boolean enabled);

    /**
     * 删除用户
     * @param id 用户ID
     */
    void deleteUser(Long id);
} 