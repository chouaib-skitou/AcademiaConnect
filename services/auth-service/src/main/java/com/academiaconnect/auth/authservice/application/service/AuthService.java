package com.academiaconnect.auth.authservice.application.service;

import com.academiaconnect.auth.authservice.application.dto.auth.LoginRequest;
import com.academiaconnect.auth.authservice.application.dto.auth.RegisterRequest;
import com.academiaconnect.auth.authservice.application.dto.auth.TokenResponse;
import com.academiaconnect.auth.authservice.application.dto.user.UserResponse;
import com.academiaconnect.auth.authservice.domain.model.User;

public interface AuthService {
    User registerUser(RegisterRequest registerRequest);
    TokenResponse login(LoginRequest loginRequest);
    TokenResponse refreshToken(String refreshToken);
    String verifyEmail(String token);
    void resetPasswordRequest(String email);
    void resetPassword(String token, String newPassword);
    UserResponse getCurrentUser();
}