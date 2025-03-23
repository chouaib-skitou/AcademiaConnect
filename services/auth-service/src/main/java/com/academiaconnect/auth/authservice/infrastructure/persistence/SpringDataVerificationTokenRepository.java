package com.academiaconnect.auth.authservice.infrastructure.persistence;

import com.academiaconnect.auth.authservice.domain.model.VerificationToken;
import com.academiaconnect.auth.authservice.domain.model.User;
import com.academiaconnect.auth.authservice.domain.repository.VerificationTokenRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataVerificationTokenRepository extends VerificationTokenRepository, JpaRepository<VerificationToken, Long> {
    @Override
    Optional<VerificationToken> findByToken(String token);

    @Override
    Optional<VerificationToken> findByUserAndTokenType(User user, VerificationToken.TokenType tokenType);

    @Override
    VerificationToken save(VerificationToken token);

    @Override
    void delete(VerificationToken token);
}