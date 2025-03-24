package com.academiaconnect.auth.authservice.application.service;

import com.academiaconnect.auth.authservice.application.dto.auth.LoginRequest;
import com.academiaconnect.auth.authservice.application.dto.auth.RegisterRequest;
import com.academiaconnect.auth.authservice.application.dto.auth.TokenResponse;
import com.academiaconnect.auth.authservice.application.dto.user.UserResponse;
import com.academiaconnect.auth.authservice.application.exception.InvalidTokenException;
import com.academiaconnect.auth.authservice.application.exception.ResourceAlreadyExistsException;
import com.academiaconnect.auth.authservice.application.exception.UnverifiedAccountException;
import com.academiaconnect.auth.authservice.domain.model.Role;
import com.academiaconnect.auth.authservice.domain.model.User;
import com.academiaconnect.auth.authservice.domain.model.VerificationToken;
import com.academiaconnect.auth.authservice.domain.repository.UserRepository;
import com.academiaconnect.auth.authservice.domain.repository.VerificationTokenRepository;
import com.academiaconnect.auth.authservice.infrastructure.email.EmailService;
import com.academiaconnect.auth.authservice.infrastructure.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final VerificationTokenRepository tokenRepository;
    private final EmailService emailService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    @Transactional
    public User registerUser(RegisterRequest registerRequest) {
        // Check if username or email already exists
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new ResourceAlreadyExistsException("Username is already taken");
        }

        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("Email is already in use");
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .roles(Collections.singleton(Role.USER))
                .emailVerified(false)
                .build();

        User savedUser = userRepository.save(user);

        // Generate verification token
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(savedUser)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .tokenType(VerificationToken.TokenType.EMAIL_VERIFICATION)
                .build();

        tokenRepository.save(verificationToken);

        // Send verification email
        emailService.sendVerificationEmail(savedUser.getEmail(), token);

        return savedUser;
    }

    @Override
    public TokenResponse login(LoginRequest loginRequest) {
        try {
            // First check if the account exists and is verified
            User user = userRepository.findByUsername(loginRequest.getUsernameOrEmail())
                    .orElseGet(() -> userRepository.findByEmail(loginRequest.getUsernameOrEmail())
                            .orElse(null));

            if (user != null && !user.isEmailVerified()) {
                // If user exists but email is not verified, send a new verification email
                resendVerificationEmail(user);
                throw new UnverifiedAccountException("Email not verified. A new verification email has been sent.");
            }

            // Proceed with authentication
            var authToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsernameOrEmail(), loginRequest.getPassword());
            Authentication auth = authenticationManager.authenticate(authToken);

            String accessToken = jwtTokenProvider.generateAccessToken(auth);
            String refreshToken = jwtTokenProvider.generateRefreshToken(auth);

            return TokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .build();
        } catch (AuthenticationException e) {
            throw new InvalidTokenException("Invalid username/email or password");
        }
    }

    private void resendVerificationEmail(User user) {
        // Delete any existing verification tokens
        tokenRepository.findByUserAndTokenType(user, VerificationToken.TokenType.EMAIL_VERIFICATION)
                .ifPresent(tokenRepository::delete);

        // Generate a new token
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .tokenType(VerificationToken.TokenType.EMAIL_VERIFICATION)
                .build();

        tokenRepository.save(verificationToken);

        // Send new verification email
        emailService.sendVerificationEmail(user.getEmail(), token);
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
            throw new InvalidTokenException("Invalid or expired refresh token");
        }
    }

    @Override
    @Transactional
    public String verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid verification token"));

        if (verificationToken.isExpired()) {
            tokenRepository.delete(verificationToken);
            throw new InvalidTokenException("Token has expired");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        tokenRepository.delete(verificationToken);

        // Return the frontend URL for redirection
        return frontendUrl + "/login?verified=true";
    }

    @Override
    public void resetPasswordRequest(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidTokenException("No user found with email: " + email));

        // Remove any existing tokens
        tokenRepository.findByUserAndTokenType(user, VerificationToken.TokenType.PASSWORD_RESET)
                .ifPresent(tokenRepository::delete);

        // Generate reset token
        String token = UUID.randomUUID().toString();
        VerificationToken resetToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(30))
                .tokenType(VerificationToken.TokenType.PASSWORD_RESET)
                .build();

        tokenRepository.save(resetToken);

        // Send password reset email
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        VerificationToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid reset token"));

        if (resetToken.isExpired() ||
                resetToken.getTokenType() != VerificationToken.TokenType.PASSWORD_RESET) {
            tokenRepository.delete(resetToken);
            throw new InvalidTokenException("Token is invalid or has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(resetToken);
    }

    @Override
    public UserResponse getCurrentUser() {
        // Retrieve username from security context
        String username = jwtTokenProvider.getCurrentUsername();
        if (username == null) {
            throw new InvalidTokenException("Authentication required");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidTokenException("User not found"));

        return mapUserToResponse(user);
    }

    private UserResponse mapUserToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .emailVerified(user.isEmailVerified())
                .build();
    }
}