package com.academiaconnect.auth.authservice.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime expiryDate;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    public enum TokenType {
        EMAIL_VERIFICATION,
        PASSWORD_RESET
    }
}