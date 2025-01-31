package com.polymath.jobboard.repositories;

import com.polymath.jobboard.models.Tokens;
import com.polymath.jobboard.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Tokens, Long> {
    void saveTokens(Tokens tokens);
    Optional<Tokens> findByRefreshToken(String refreshToken);
    void deleteByRefreshToken(String refreshToken);
    void deleteByUser(Users users);

}
