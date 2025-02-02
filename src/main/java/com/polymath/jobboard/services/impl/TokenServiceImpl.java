package com.polymath.jobboard.services.impl;

import com.polymath.jobboard.models.Tokens;
import com.polymath.jobboard.models.Users;
import com.polymath.jobboard.repositories.TokenRepository;
import com.polymath.jobboard.services.TokenService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;

    public TokenServiceImpl(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }


   // @Override
    public void saveToken(String refreshToken,  Users users) {
        Tokens tokens = new Tokens();
        tokens.setUser(users);
        tokens.setRefreshToken(refreshToken);
        tokens.setIssuedAt(LocalDateTime.now());
        tokens.setExpiresAt(LocalDateTime.now().plusMonths(6));
        tokens.setRevoked(false);
        tokenRepository.save(tokens);
    }


    public boolean isRefreshTokenValid(String refreshToken) {
        Optional<Tokens> tokens = tokenRepository.findByRefreshToken(refreshToken);
        return tokens.isPresent() && !tokens.get().isRevoked()&&tokens.get().getExpiresAt().isAfter(LocalDateTime.now());
    }

    public void revokeRefreshToken(String refreshToken) {
        Optional<Tokens> tokens = tokenRepository.findByRefreshToken(refreshToken);
        if (tokens.isPresent()) {
            tokens.get().setRevoked(true);
            tokenRepository.save(tokens.get());
        }

    }
    public void revokeAndDeleteTokenForUser(Long userId) {
        tokenRepository.deleteByUserId(userId);
        System.out.println(tokenRepository.findAll());
        //System.out.println("user id"+tokenRepository.findByUserId(userId));
    }

}
