package com.polymath.jobboard.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class ResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_id")
    private Users user;
    private UUID token;
    private LocalDateTime expiresAt;
}
