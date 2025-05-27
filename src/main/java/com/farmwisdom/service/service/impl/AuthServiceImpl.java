package com.farmwisdom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.farmwisdom.dto.LoginRequest;
import com.farmwisdom.dto.RegisterRequest;
import com.farmwisdom.entity.User;
import com.farmwisdom.enums.UserRole;
import com.farmwisdom.exception.BusinessException;
import com.farmwisdom.mapper.UserMapper;
import com.farmwisdom.security.JwtTokenProvider;
import com.farmwisdom.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public String login(LoginRequest loginRequest) {
        log.info("Attempting login for user: {}", loginRequest.getUsername());
        try {
            log.debug("Creating authentication token for user: {}", loginRequest.getUsername());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            log.debug("Authentication successful, setting security context");
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Generating JWT token");
            String token = tokenProvider.generateToken(authentication);
            log.info("Login successful for user: {}", loginRequest.getUsername());
            return token;
        } catch (Exception e) {
            log.error("Login failed for user: {} with error: {}", loginRequest.getUsername(), e.getMessage(), e);
            throw new BusinessException("用户名或密码错误");
        }
    }

    @Override
    @Transactional
    public void register(RegisterRequest registerRequest) {
        log.info("Attempting to register user: {}", registerRequest.getUsername());
        try {
            // 检查用户名是否已存在
            User existingUser = userMapper.selectOne(
                    new LambdaQueryWrapper<User>()
                            .eq(User::getUsername, registerRequest.getUsername())
            );
            if (existingUser != null) {
                log.warn("Username already exists: {}", registerRequest.getUsername());
                throw new BusinessException("USERNAME_EXISTS", "用户名已存在");
            }

            // 检查邮箱是否已存在
            existingUser = userMapper.selectOne(
                    new LambdaQueryWrapper<User>()
                            .eq(User::getEmail, registerRequest.getEmail())
            );
            if (existingUser != null) {
                log.warn("Email already in use: {}", registerRequest.getEmail());
                throw new BusinessException("EMAIL_EXISTS", "邮箱已被使用");
            }

            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setEmail(registerRequest.getEmail());
            user.setRole(UserRole.ROLE_USER);
            user.setReputation(0);
            user.setIsEnabled(true);
            user.setIsLocked(false);
            user.setPostCount(0);
            user.setCommentCount(0);
            user.setNickname(registerRequest.getUsername());

            // 如果请求中包含这些可选字段，则设置它们
            if (registerRequest.getAvatar() != null) {
                user.setAvatar(registerRequest.getAvatar());
            }
            if (registerRequest.getBio() != null) {
                user.setBio(registerRequest.getBio());
            }

            userMapper.insert(user);
            log.info("Successfully registered user: {}", registerRequest.getUsername());
        } catch (BusinessException e) {
            log.error("Registration failed for user: {} with business error: {}", registerRequest.getUsername(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Registration failed for user: {} with unexpected error", registerRequest.getUsername(), e);
            throw new BusinessException("注册失败，请稍后重试");
        }
    }
}