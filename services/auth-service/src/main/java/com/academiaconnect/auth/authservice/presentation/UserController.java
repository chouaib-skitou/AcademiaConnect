package com.academiaconnect.auth.authservice.presentation;

import com.academiaconnect.auth.authservice.application.dto.LoginRequest;
import com.academiaconnect.auth.authservice.application.dto.RegisterRequest;
import com.academiaconnect.auth.authservice.application.dto.TokenResponse;
import com.academiaconnect.auth.authservice.application.service.UserService;
import com.academiaconnect.auth.authservice.domain.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
        userService.registerUser(registerRequest);
        return ResponseEntity.ok("User registered successfully. Verification email sent.");
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        TokenResponse tokenResponse = userService.login(loginRequest);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        TokenResponse tokenResponse = userService.refreshToken(refreshToken);
        return ResponseEntity.ok(tokenResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<User> currentUser() {
        User user = userService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    @PostMapping("/reset-password-request")
    public ResponseEntity<String> resetPasswordRequest(@RequestParam("email") String email) {
        userService.resetPasswordRequest(email);
        return ResponseEntity.ok("Password reset link sent to email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token,
                                                @RequestParam("newPassword") String newPassword) {
        userService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password reset successfully.");
    }
}
