package com.academiaconnect.auth.authservice.application.service;

import com.academiaconnect.auth.authservice.application.dto.LoginRequest;
import com.academiaconnect.auth.authservice.application.dto.RegisterRequest;
import com.academiaconnect.auth.authservice.application.dto.TokenResponse;
import com.academiaconnect.auth.authservice.domain.model.Role;
import com.academiaconnect.auth.authservice.domain.model.User;
import com.academiaconnect.auth.authservice.domain.repository.UserRepository;
import com.academiaconnect.auth.authservice.infrastructure.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public User registerUser(RegisterRequest registerRequest) {
        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .roles(Collections.singleton(Role.USER))
                .emailVerified(false)
                .build();

        User savedUser = userRepository.save(user);
        // In a real application, you would call an EmailService to send a verification email.
        System.out.println("Verification email sent to " + savedUser.getEmail());
        return savedUser;
    }

    @Override
    public TokenResponse login(LoginRequest loginRequest) {
        var authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getUsernameOrEmail(), loginRequest.getPassword());
        var auth = authenticationManager.authenticate(authToken);

        String accessToken = jwtTokenProvider.generateAccessToken(auth);
        String refreshToken = jwtTokenProvider.generateRefreshToken(auth);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        if (jwtTokenProvider.validateRefreshToken(refreshToken)) {
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            var authToken = new UsernamePasswordAuthenticationToken(
                    username, null, jwtTokenProvider.getAuthorities(refreshToken));
            String newAccessToken = jwtTokenProvider.generateAccessToken(authToken);
            return TokenResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .build();
        } else {
            throw new RuntimeException("Invalid refresh token");
        }
    }

    @Override
    public User getCurrentUser() {
        // Retrieve username from security context (here stubbed)
        String username = jwtTokenProvider.getCurrentUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public void resetPasswordRequest(String email) {
        // Generate a reset token and send reset link (omitted token generation for brevity)
        System.out.println("Password reset link sent to " + email);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        // Validate token, find the user and update the password (omitted for brevity)
        System.out.println("Password reset successful for token: " + token);
    }
}
