package com.polymath.jobboard.services;

import com.polymath.jobboard.dto.response.RefreshTokenResponse;
import com.polymath.jobboard.models.Tokens;
import com.polymath.jobboard.models.Users;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public interface TokenService {
    void saveToken(String refreshToken, Users users );
    boolean isRefreshTokenValid(String refreshToken);
    void revokeRefreshToken(String refreshToken);
    void revokeAndDeleteTokenForUser(Long userId);
    RefreshTokenResponse getRefreshToken(String accessToken);
    RefreshTokenResponse generateRefreshToken(String email);
}
