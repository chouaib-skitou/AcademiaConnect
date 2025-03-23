package com.academiaconnect.auth.authservice.domain.repository;

import com.academiaconnect.auth.authservice.domain.model.VerificationToken;
import com.academiaconnect.auth.authservice.domain.model.User;
import java.util.Optional;

public interface VerificationTokenRepository {
    VerificationToken save(VerificationToken token);
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByUserAndTokenType(User user, VerificationToken.TokenType tokenType);
    void delete(VerificationToken token);
}