package com.farmwisdom.service;

import com.farmwisdom.dto.LoginRequest;
import com.farmwisdom.dto.RegisterRequest;

public interface AuthService {
    String login(LoginRequest loginRequest);
    void register(RegisterRequest registerRequest);
}
