package com.polymath.jobboard.repositories;

import com.polymath.jobboard.models.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResetTokenRepositories extends JpaRepository<ResetToken, Long> {
    boolean existsByUserEmail(String userEmail);
    Optional<ResetToken> findByUserEmail(String userEmail);
    Optional<ResetToken> findByToken(UUID token);
}
