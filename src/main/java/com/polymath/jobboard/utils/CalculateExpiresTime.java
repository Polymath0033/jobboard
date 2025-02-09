package com.polymath.jobboard.utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class CalculateExpiresTime {
    public static long calculateExpiresIn(LocalDateTime expiresAt) {
        return Math.max(0, expiresAt.toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
    }
}
