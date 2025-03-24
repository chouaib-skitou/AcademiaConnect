package com.academiaconnect.auth.authservice.application.dto.user;

import com.academiaconnect.auth.authservice.domain.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Set<Role> roles;
    private boolean emailVerified;
}