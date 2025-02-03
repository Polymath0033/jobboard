package com.polymath.jobboard.dto.requests;

import jakarta.validation.constraints.NotNull;

public record JobSeekersDto(@NotNull String email,String firstName,String lastName, String resumeUrl,String skills,String experiences) {
}
