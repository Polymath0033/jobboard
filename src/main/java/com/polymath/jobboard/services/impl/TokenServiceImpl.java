package com.polymath.jobboard.services.impl;

import com.polymath.jobboard.dto.response.RefreshTokenResponse;
import com.polymath.jobboard.exceptions.CustomBadRequest;
import com.polymath.jobboard.exceptions.CustomNotFound;
import com.polymath.jobboard.models.Tokens;
import com.polymath.jobboard.models.Users;
import com.polymath.jobboard.repositories.TokenRepository;
import com.polymath.jobboard.repositories.UsersRepositories;
import com.polymath.jobboard.services.JwtService;
import com.polymath.jobboard.services.TokenService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final UsersRepositories usersRepositories;

    public TokenServiceImpl(TokenRepository tokenRepository, JwtService jwtService, UsersRepositories usersRepositories) {
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;

        this.usersRepositories = usersRepositories;
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
    }

    @Override
    public RefreshTokenResponse getRefreshToken(String accessToken) {
        String email = jwtService.extractEmail(accessToken);
        Tokens tokens = tokenRepository.findByUserEmail(email).orElseThrow(()->new CustomNotFound("Refresh token not found"));
        if(!isRefreshTokenValid(tokens.getRefreshToken())) {
            throw new CustomBadRequest("Refresh token is invalid, please try log in again");
        }
        LocalDateTime refreshTokenExpiresAt = jwtService.expirationDate(tokens.getRefreshToken());
        LocalDateTime accessTokenExpiresAt = jwtService.expirationDate(accessToken);
        return new RefreshTokenResponse(tokens.getRefreshToken(), accessToken, calculateExpiresIn(refreshTokenExpiresAt), calculateExpiresIn(accessTokenExpiresAt),tokens.isRevoked());
    }

    @Override
    public RefreshTokenResponse generateRefreshToken(String email) {
        if(email == null) {
            throw new CustomBadRequest("Email cannot be null");
        }
        String accessToken = jwtService.generateAccessToken(email);
        String refreshToken = jwtService.generateRefreshToken(email);
        LocalDateTime accessTokenExpiresAt = jwtService.expirationDate(accessToken);
        LocalDateTime refreshTokenExpiresAt = jwtService.expirationDate(refreshToken);
        Users users = usersRepositories.findByEmail(email).orElseThrow(()->new CustomNotFound("User with email " + email + " not found"));
        if(tokenRepository.existsByUserEmail(email)){
            Tokens existingToken = tokenRepository.findByUserEmail(email).orElseThrow();
            existingToken.setRefreshToken(refreshToken);
            existingToken.setExpiresAt(refreshTokenExpiresAt);
            existingToken.setRevoked(false);
            tokenRepository.save(existingToken);
        }else {
            saveToken(refreshToken, users);
        }
        return new RefreshTokenResponse(refreshToken, accessToken, calculateExpiresIn(refreshTokenExpiresAt), calculateExpiresIn(accessTokenExpiresAt),false);
    }

    public static long calculateExpiresIn(LocalDateTime expiresAt) {
        return Math.max(0, expiresAt.toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
    }
}
