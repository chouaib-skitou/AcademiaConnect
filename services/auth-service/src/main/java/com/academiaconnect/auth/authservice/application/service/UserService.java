package com.academiaconnect.auth.authservice.application.service;

import com.academiaconnect.auth.authservice.application.dto.user.CreateUserRequest;
import com.academiaconnect.auth.authservice.application.dto.user.UpdateUserRequest;
import com.academiaconnect.auth.authservice.application.dto.user.UserPage;
import com.academiaconnect.auth.authservice.application.dto.user.UserResponse;
import com.academiaconnect.auth.authservice.domain.model.Role;

public interface UserService {
    // CRUD operations
    UserResponse createUser(CreateUserRequest request);
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UpdateUserRequest request);
    void deleteUser(Long id);

    // Unified method for getting users with optional filtering
    UserPage getUsers(String username, Role role, int page, int size, String sortBy, String direction);
}