package com.farmwisdom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.farmwisdom.dto.LoginRequest;
import com.farmwisdom.dto.UserRegisterRequest;
import com.farmwisdom.dto.UserResponse;
import com.farmwisdom.dto.UserProfileUpdateRequest;
import com.farmwisdom.entity.User;
import com.farmwisdom.enums.UserRole;
import com.farmwisdom.exception.ResourceNotFoundException;
import com.farmwisdom.mapper.UserMapper;
import com.farmwisdom.security.JwtTokenProvider;
import com.farmwisdom.security.SecurityUtils;
import com.farmwisdom.service.UserService;
import com.farmwisdom.service.FileStorageService;
import com.farmwisdom.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final FileStorageService fileStorageService;
    private final EmailService emailService;

    @Autowired
    public UserServiceImpl(UserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtTokenProvider jwtTokenProvider,
                           FileStorageService fileStorageService,
                           EmailService emailService) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.fileStorageService = fileStorageService;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public UserResponse register(UserRegisterRequest request) {
        // 验证密码确认
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("两次输入的密码不一致");
        }

        // 检查用户名是否已存在
        if (getUserByUsername(request.getUsername()) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (getUserByEmail(request.getEmail()) != null) {
            throw new IllegalArgumentException("邮箱已被注册");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setBio(request.getBio());
        user.setExpertise(request.getExpertise());
        user.setRole(UserRole.ROLE_USER.name());
        user.setReputation(0);
        user.setPostCount(0);
        user.setCommentCount(0);
        user.setIsEnabled(true);
        user.setIsLocked(false);
        user.setLastLoginTime(LocalDateTime.now());

        userMapper.insert(user);

        // 发送欢迎邮件
        emailService.sendWelcomeEmail(user);

        return convertToUserResponse(user);
    }

    @Override
    public UserResponse login(LoginRequest request) {
        log.info("Attempting to login user: {}", request.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            String token = jwtTokenProvider.generateToken(authentication);
            User user = getUserByUsername(request.getUsername());
            UserResponse response = convertToUserResponse(user);
            response.setToken(token);

            log.info("User logged in successfully: {}", request.getUsername());
            return response;
        } catch (BadCredentialsException e) {
            log.error("Login failed for user {}: Invalid credentials", request.getUsername());
            throw new BadCredentialsException("用户名或密码错误");
        } catch (Exception e) {
            log.error("Login failed for user {}: {}", request.getUsername(), e.getMessage());
            throw new RuntimeException("登录失败，请稍后重试");
        }
    }

    @Override
    public UserResponse getCurrentUser() {
        User user = findUserById(SecurityUtils.getCurrentUserId());
        return convertToUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long userId, UserProfileUpdateRequest request) {
        User user = findUserById(userId);

        // 检查邮箱是否被其他用户使用
        User existingUser = getUserByEmail(request.getEmail());
        if (existingUser != null && !existingUser.getId().equals(userId)) {
            throw new IllegalArgumentException("邮箱已被其他用户注册");
        }

        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setBio(request.getBio());
        user.setExpertise(request.getExpertise());

        userMapper.updateById(user);
        return convertToUserResponse(user);
    }

    @Override
    @Transactional
    public void resetPassword(String email) {
        User user = getUserByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("User", "email", email);
        }

        // 生成重置令牌
        String token = jwtTokenProvider.generatePasswordResetToken(user);

        // 发送重置密码邮件
        emailService.sendPasswordResetEmail(user, token);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = findUserById(userId);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("原密码不正确");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);

        // 发送密码更改通知邮件
        emailService.sendPasswordChangedEmail(user);
    }

    @Override
    @Transactional
    public UserResponse updateAvatar(Long userId, MultipartFile file) {
        User user = findUserById(userId);
        String avatarUrl = fileStorageService.storeFile(file, "avatars");

        // 如果用户已有头像，删除旧头像
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            try {
                fileStorageService.deleteFile(user.getAvatar());
            } catch (Exception e) {
                log.warn("Failed to delete old avatar: {}", user.getAvatar());
            }
        }

        user.setAvatar(avatarUrl);
        userMapper.updateById(user);
        return convertToUserResponse(user);
    }

    @Override
    public Page<UserResponse> getUsers(int page, int size) {
        Page<User> userPage = userMapper.selectPage(new Page<>(page, size), null);

        Page<UserResponse> responsePage = new Page<>();
        responsePage.setCurrent(userPage.getCurrent());
        responsePage.setSize(userPage.getSize());
        responsePage.setTotal(userPage.getTotal());
        responsePage.setRecords(userPage.getRecords().stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList()));

        return responsePage;
    }

    @Override
    public UserResponse getUserById(Long userId) {
        return convertToUserResponse(findUserById(userId));
    }

    @Override
    public User getUserEntityById(Long userId) {
        return findUserById(userId);
    }

    private User findUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public User getUserByEmail(String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        return userMapper.selectOne(wrapper);
    }

    @Override
    @Transactional
    public void enableUser(Long userId) {
        User user = findUserById(userId);
        user.setIsEnabled(true);
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void disableUser(Long userId) {
        User user = findUserById(userId);
        user.setIsEnabled(false);
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void lockUser(Long userId) {
        User user = findUserById(userId);
        user.setIsLocked(true);
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void unlockUser(Long userId) {
        User user = findUserById(userId);
        user.setIsLocked(false);
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void updateUserRole(Long userId, String role) {
        User user = findUserById(userId);

        // 验证角色是否有效
        try {
            UserRole.valueOf(role);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("无效的用户角色");
        }

        user.setRole(role);
        userMapper.updateById(user);

        // 如果是专家角色，发送验证邮件
        if (UserRole.ROLE_EXPERT.name().equals(role)) {
            emailService.sendExpertVerificationEmail(user);
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = findUserById(userId);

        // 删除用户头像
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            try {
                fileStorageService.deleteFile(user.getAvatar());
            } catch (Exception e) {
                log.warn("Failed to delete user avatar: {}", user.getAvatar());
            }
        }

        // 删除用户记录
        userMapper.deleteById(userId);

        log.info("User deleted successfully: {}", user.getUsername());
    }

    private UserResponse convertToUserResponse(User user) {
        if (user == null) {
            return null;
        }

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setAvatar(user.getAvatar());
        response.setRole(user.getRole());
        response.setBio(user.getBio());
        response.setExpertise(user.getExpertise());
        response.setReputation(user.getReputation());
        response.setPostCount(user.getPostCount());
        response.setCommentCount(user.getCommentCount());
        response.setLastLoginTime(user.getLastLoginTime());
        response.setCreateTime(user.getCreateTime());
        return response;
    }
}