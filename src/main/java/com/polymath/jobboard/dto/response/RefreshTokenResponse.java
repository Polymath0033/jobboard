package com.polymath.jobboard.dto.response;

import java.time.LocalDateTime;

public record RefreshTokenResponse(String refreshToken, String accessToken, Long refreshTokenExpiresAt,Long accessTokenExpiresAt,boolean revoked) {
}
