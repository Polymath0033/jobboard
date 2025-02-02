package com.polymath.jobboard.repositories;
import com.polymath.jobboard.models.Tokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Tokens, Long> {

    Optional<Tokens> findByRefreshToken(String refreshToken);
    void deleteByRefreshToken(String refreshToken);
    void deleteByUserId(Long tokenId);

    boolean findByUserId(Long userId);

    // Optional<Tokens> findByUserIdA

}
