package com.academiaconnect.auth.authservice.infrastructure.persistence;

import com.academiaconnect.auth.authservice.domain.model.Role;
import com.academiaconnect.auth.authservice.domain.model.User;
import com.academiaconnect.auth.authservice.domain.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    Optional<User> findById(Long id);

    @Override
    void deleteById(Long id);

    @Override
    Page<User> findAll(Pageable pageable);

    @Override
    Page<User> findByUsernameContaining(String username, Pageable pageable);

    @Override
    Page<User> findByRolesContaining(Role role, Pageable pageable);

    @Override
    Page<User> findByUsernameContainingAndRolesContaining(String username, Role role, Pageable pageable);

    @Override
    boolean existsById(Long id);
}