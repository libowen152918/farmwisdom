package com.farmwisdom.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.farmwisdom.dto.LoginRequest;
import com.farmwisdom.dto.UserRegisterRequest;
import com.farmwisdom.dto.UserResponse;
import com.farmwisdom.dto.UserProfileUpdateRequest;
import com.farmwisdom.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserResponse register(UserRegisterRequest request);

    UserResponse login(LoginRequest request);

    UserResponse getCurrentUser();

    UserResponse updateProfile(Long userId, UserProfileUpdateRequest request);

    void resetPassword(String email);

    void changePassword(Long userId, String oldPassword, String newPassword);

    UserResponse updateAvatar(Long userId, MultipartFile file);

    void deleteUser(Long userId);

    Page<UserResponse> getUsers(int page, int size);

    UserResponse getUserById(Long userId);

    User getUserEntityById(Long userId);

    User getUserByUsername(String username);

    User getUserByEmail(String email);

    void enableUser(Long userId);

    void disableUser(Long userId);

    void lockUser(Long userId);

    void unlockUser(Long userId);

    void updateUserRole(Long userId, String role);
}