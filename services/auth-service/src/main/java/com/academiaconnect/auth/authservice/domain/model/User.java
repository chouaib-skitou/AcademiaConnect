package com.academiaconnect.auth.authservice.domain.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class User {
    private Long id;
    private String username;
    private String email;
    private String password; // hashed password
    private boolean verified;
    private LocalDateTime createdAt;
    private Set<Role> roles;
}
