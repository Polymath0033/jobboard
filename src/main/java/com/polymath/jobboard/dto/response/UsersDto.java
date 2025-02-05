package com.polymath.jobboard.dto.response;

import com.polymath.jobboard.models.enums.UserRole;

import java.time.LocalDateTime;

public record UsersDto(Long id, String email, UserRole role, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
