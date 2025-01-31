package com.polymath.jobboard.services.impl;

import com.polymath.jobboard.models.Tokens;
import com.polymath.jobboard.models.Users;
import com.polymath.jobboard.repositories.TokenRepository;
import com.polymath.jobboard.services.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
    public void saveToken(String refreshToken, LocalDateTime issuedAt, LocalDateTime expiresAt, Users users) {
        Tokens tokens = new Tokens();
        tokens.setUser(users);
        tokens.setRefreshToken(refreshToken);
        tokens.setIssuedAt(LocalDateTime.now());
        tokens.setExpiresAt(LocalDateTime.now().plusMonths(6));
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
    public void revokeAllTokensForUser(Users users) {
        tokenRepository.deleteByUser(users);
    }

    private void savedTokenOnCookies(String refreshToken, HttpServletResponse response) {
        Cookie cookie =  new Cookie("refreshToken",refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/api/auth/refresh");
        cookie.setMaxAge(6*30*24*60*60);
        response.addCookie(cookie);

    }

}
