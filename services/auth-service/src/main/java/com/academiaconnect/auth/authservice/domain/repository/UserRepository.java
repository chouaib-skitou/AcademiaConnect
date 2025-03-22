package com.academiaconnect.auth.authservice.domain.repository;

import com.academiaconnect.auth.authservice.domain.model.User;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
}
