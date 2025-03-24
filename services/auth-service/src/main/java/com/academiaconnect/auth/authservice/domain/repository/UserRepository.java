package com.academiaconnect.auth.authservice.domain.repository;

import com.academiaconnect.auth.authservice.domain.model.Role;
import com.academiaconnect.auth.authservice.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    User save(User user);
    Optional<User> findById(Long id);
    void deleteById(Long id);
    Page<User> findAll(Pageable pageable);
    Page<User> findByUsernameContaining(String username, Pageable pageable);
    Page<User> findByRolesContaining(Role role, Pageable pageable);
    Page<User> findByUsernameContainingAndRolesContaining(String username, Role role, Pageable pageable);
    boolean existsById(Long id);
}