package com.academiaconnect.auth.authservice.presentation;

import com.academiaconnect.auth.authservice.application.dto.*;
import com.academiaconnect.auth.authservice.application.exception.ValidationException;
import com.academiaconnect.auth.authservice.application.service.UserService;
import com.academiaconnect.auth.authservice.domain.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and user management endpoints")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Register a new user",
            description = "Registers a new user and sends a verification email")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User registered successfully",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(
                    responseCode = "409",
                    description = "Username or email already exists",
                    content = @Content),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Parameter(description = "User registration details", required = true)
            @Valid @RequestBody RegisterRequest registerRequest) {
        userService.registerUser(registerRequest);
        return ResponseEntity.ok("User registered successfully. Verification email sent.");
    }

    @Operation(
            summary = "User login",
            description = "Authenticates a user and returns JWT tokens")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Account not verified",
                    content = @Content),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @Parameter(description = "Login credentials", required = true)
            @Valid @RequestBody LoginRequest loginRequest) {
        TokenResponse tokenResponse = userService.login(loginRequest);
        return ResponseEntity.ok(tokenResponse);
    }

    @Operation(
            summary = "Refresh JWT token",
            description = "Generate a new access token using a valid refresh token")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Token refreshed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or expired refresh token",
                    content = @Content)
    })
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(
            @Parameter(description = "Refresh token", required = true)
            @RequestParam("refreshToken") String refreshToken) {
        TokenResponse tokenResponse = userService.refreshToken(refreshToken);
        return ResponseEntity.ok(tokenResponse);
    }

    @Operation(
            summary = "Get current user",
            description = "Returns the currently authenticated user's details",
            security = { @SecurityRequirement(name = "bearer-jwt") })
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User details retrieved successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "401",
                    description = "Not authenticated",
                    content = @Content)
    })
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> currentUser() {
        User user = userService.getCurrentUser();

        // Create a map to avoid serialization issues
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("id", user.getId());
        userDetails.put("username", user.getUsername());
        userDetails.put("email", user.getEmail());
        userDetails.put("roles", user.getRoles());
        userDetails.put("emailVerified", user.isEmailVerified());

        return ResponseEntity.ok(userDetails);
    }

    @Operation(
            summary = "Request password reset",
            description = "Sends a password reset email to the user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password reset email sent",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid email format",
                    content = @Content),
            @ApiResponse(
                    responseCode = "404",
                    description = "Email not found",
                    content = @Content)
    })
    @PostMapping("/reset-password-request")
    public ResponseEntity<String> resetPasswordRequest(
            @Parameter(description = "Email reset request", required = true)
            @Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPasswordRequest(request.getEmail());
        return ResponseEntity.ok("Password reset link sent to email.");
    }

    @Operation(
            summary = "Reset password",
            description = "Resets user password using a valid token")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password reset successful",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid token or passwords don't match",
                    content = @Content),
            @ApiResponse(
                    responseCode = "404",
                    description = "Token not found",
                    content = @Content)
    })
    @PostMapping("/reset-password/{token}")
    public ResponseEntity<String> resetPasswordWithPathToken(
            @Parameter(description = "Reset token from email", required = true)
            @PathVariable("token") String token,
            @Parameter(description = "New password details", required = true)
            @Valid @RequestBody PasswordUpdateDto passwordUpdateDto) {

        // Check if passwords match
        if (!passwordUpdateDto.getNewPassword().equals(passwordUpdateDto.getConfirmNewPassword())) {
            throw new ValidationException("Passwords do not match");
        }

        userService.resetPassword(token, passwordUpdateDto.getNewPassword());
        return ResponseEntity.ok("Password reset successfully.");
    }

    @Operation(
            summary = "Verify email",
            description = "Verifies user's email address using the token sent via email and redirects to frontend",
            security = { })
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "302",
                    description = "Email verified successfully, redirected to frontend",
                    content = @Content),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or expired token",
                    content = @Content)
    })
    @GetMapping("/verify-email")
    public RedirectView verifyEmail(
            @Parameter(description = "Email verification token", required = true)
            @RequestParam("token") String token) {
        String redirectUrl = userService.verifyEmail(token);
        return new RedirectView(redirectUrl);
    }
}