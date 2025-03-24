package com.academiaconnect.auth.authservice.application.service;

import com.academiaconnect.auth.authservice.application.dto.user.CreateUserRequest;
import com.academiaconnect.auth.authservice.application.dto.user.UpdateUserRequest;
import com.academiaconnect.auth.authservice.application.dto.user.UserPage;
import com.academiaconnect.auth.authservice.application.dto.user.UserResponse;
import com.academiaconnect.auth.authservice.application.exception.ResourceAlreadyExistsException;
import com.academiaconnect.auth.authservice.application.exception.ResourceNotFoundException;
import com.academiaconnect.auth.authservice.domain.model.Role;
import com.academiaconnect.auth.authservice.domain.model.User;
import com.academiaconnect.auth.authservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        // Check if username or email already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResourceAlreadyExistsException("Username is already taken");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("Email is already in use");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(request.getRoles())
                .emailVerified(true) // Admin-created users are verified by default
                .build();

        User savedUser = userRepository.save(user);
        return mapUserToResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapUserToResponse(user);
    }

    @Override
    public UserPage getUsers(String username, Role role, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<User> userPage;

        // Determine which query to use based on the provided parameters
        if (StringUtils.hasText(username) && role != null) {
            userPage = userRepository.findByUsernameContainingAndRolesContaining(username, role, pageable);
        } else if (StringUtils.hasText(username)) {
            userPage = userRepository.findByUsernameContaining(username, pageable);
        } else if (role != null) {
            userPage = userRepository.findByRolesContaining(role, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        return mapToUserPage(userPage, page, size);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Check if username is being changed and is already taken
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            userRepository.findByUsername(request.getUsername())
                    .ifPresent(u -> {
                        throw new ResourceAlreadyExistsException("Username is already taken");
                    });
            user.setUsername(request.getUsername());
        }

        // Check if email is being changed and is already taken
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            userRepository.findByEmail(request.getEmail())
                    .ifPresent(u -> {
                        throw new ResourceAlreadyExistsException("Email is already in use");
                    });
            user.setEmail(request.getEmail());
        }

        // Update roles if provided
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            user.setRoles(request.getRoles());
        }

        User updatedUser = userRepository.save(user);
        return mapUserToResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
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

    private UserPage mapToUserPage(Page<User> userPage, int page, int size) {
        List<UserResponse> userResponses = userPage.getContent().stream()
                .map(this::mapUserToResponse)
                .collect(Collectors.toList());

        return UserPage.builder()
                .users(userResponses)
                .page(page)
                .size(size)
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .build();
    }
}