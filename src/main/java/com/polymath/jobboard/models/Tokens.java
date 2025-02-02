package com.polymath.jobboard.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class Tokens {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(unique = true, nullable = false)
    private String refreshToken;
    @Column(nullable = false)
    private LocalDateTime issuedAt;
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    @Column(nullable = false)
    private boolean revoked;
    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private Users user;
}
