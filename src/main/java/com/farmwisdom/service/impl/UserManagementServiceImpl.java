package com.farmwisdom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.farmwisdom.dto.UserResponse;
import com.farmwisdom.entity.User;
import com.farmwisdom.exception.ResourceNotFoundException;
import com.farmwisdom.mapper.UserMapper;
import com.farmwisdom.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final UserMapper userMapper;

    @Override
    public Page<UserResponse> getUserList(Integer page, Integer size, String username, String email) {
        log.debug("Getting user list with page: {}, size: {}, username: {}, email: {}", 
                page, size, username, email);
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(username)) {
            wrapper.like(User::getUsername, username);
        }
        if (StringUtils.hasText(email)) {
            wrapper.like(User::getEmail, email);
        }
        wrapper.orderByDesc(User::getCreateTime);

        Page<User> userPage = userMapper.selectPage(new Page<>(page, size), wrapper);
        Page<UserResponse> responsePage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        responsePage.setRecords(userPage.getRecords().stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .nickname(user.getNickname())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .avatar(user.getAvatar())
                        .role(user.getRole())
                        .bio(user.getBio())
                        .expertise(user.getExpertise())
                        .reputation(user.getReputation())
                        .postCount(user.getPostCount())
                        .commentCount(user.getCommentCount())
                        .lastLoginTime(user.getLastLoginTime())
                        .createTime(user.getCreateTime())
                        .isEnabled(user.getIsEnabled())
                        .isLocked(user.getIsLocked())
                        .build())
                .collect(java.util.stream.Collectors.toList()));
        return responsePage;
    }

    @Override
    public UserResponse getUserById(Long id) {
        log.debug("Getting user with id: {}", id);
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .bio(user.getBio())
                .expertise(user.getExpertise())
                .reputation(user.getReputation())
                .postCount(user.getPostCount())
                .commentCount(user.getCommentCount())
                .lastLoginTime(user.getLastLoginTime())
                .createTime(user.getCreateTime())
                .isEnabled(user.getIsEnabled())
                .isLocked(user.getIsLocked())
                .build();
    }

    @Override
    @Transactional
    public void updateUserStatus(Long id, Boolean enabled) {
        log.debug("Updating user status with id: {}, enabled: {}", id, enabled);
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        user.setIsEnabled(enabled);
        if (userMapper.updateById(user) == 0) {
            throw new ResourceNotFoundException("User", "id", id);
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.debug("Deleting user with id: {}", id);
        if (userMapper.deleteById(id) == 0) {
            throw new ResourceNotFoundException("User", "id", id);
        }
    }
} 