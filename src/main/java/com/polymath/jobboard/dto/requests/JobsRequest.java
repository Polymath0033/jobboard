package com.polymath.jobboard.dto.requests;

import com.polymath.jobboard.models.enums.JobStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record JobsRequest(@NotNull @NotBlank(message = "A job must have title") String title, @NotNull @NotBlank(message = "A job must have a description") String description, String location, String category,
                           JobStatus status, Double salary, LocalDateTime postedAt, LocalDateTime expiresAt) {
}
