package com.polymath.jobboard.dto.response;

public record AuthResponse(String accessToken, UserInfo user) {
}
