package com.polymath.jobboard.repositories;
import com.polymath.jobboard.models.Tokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Tokens, Long> {

    Optional<Tokens> findByRefreshToken(String refreshToken);
    Optional<Tokens> findByUserEmail(String email);
    void deleteByRefreshToken(String refreshToken);
    void deleteByUserId(Long tokenId);

    boolean findByUserId(Long userId);
    boolean existsByUserEmail(String email);

    // Optional<Tokens> findByUserIdA

}
