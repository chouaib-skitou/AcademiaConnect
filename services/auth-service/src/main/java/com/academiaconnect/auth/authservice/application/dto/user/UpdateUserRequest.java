package com.academiaconnect.auth.authservice.application.dto.user;

import com.academiaconnect.auth.authservice.domain.model.Role;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String username;

    @Email(message = "Invalid email format")
    private String email;

    private Set<Role> roles;
}