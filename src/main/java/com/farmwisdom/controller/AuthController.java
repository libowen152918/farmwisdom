package com.farmwisdom.controller;

import com.farmwisdom.dto.LoginRequest;
import com.farmwisdom.dto.RegisterRequest;
import com.farmwisdom.dto.UserResponse;
import com.farmwisdom.service.AuthService;
import com.farmwisdom.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Attempting login for user: {}", loginRequest.getUsername());
        try {
            String token = authService.login(loginRequest);
            UserResponse userResponse = userService.getCurrentUser();
            userResponse.setToken(token);
            log.info("Login successful for user: {}", loginRequest.getUsername());
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            log.error("Login failed for user: {}", loginRequest.getUsername(), e);
            throw e;
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Attempting to register user: {}", registerRequest.getUsername());
        authService.register(registerRequest);
        log.info("Successfully registered user: {}", registerRequest.getUsername());
        return ResponseEntity.ok().build();
    }
}