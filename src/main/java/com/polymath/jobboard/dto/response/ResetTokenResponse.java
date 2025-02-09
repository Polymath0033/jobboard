package com.polymath.jobboard.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ResetTokenResponse(UUID resetToken, long expiresAt,String email) {
}
