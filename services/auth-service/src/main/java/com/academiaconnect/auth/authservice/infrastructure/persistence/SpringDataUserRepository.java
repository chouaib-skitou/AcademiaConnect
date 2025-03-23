package com.academiaconnect.auth.authservice.infrastructure.persistence;

import com.academiaconnect.auth.authservice.domain.model.User;
import com.academiaconnect.auth.authservice.domain.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataUserRepository extends UserRepository, JpaRepository<User, Long> {
    @Override
    Optional<User> findByUsername(String username);

    @Override
    Optional<User> findByEmail(String email);

    @Override
    User save(User user);
}
