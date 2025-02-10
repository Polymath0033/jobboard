package com.polymath.jobboard.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record JobSeekersDto(@NotNull @NotBlank String email, @NotNull @NotBlank(message = "First name cannot be blank") String firstName, @NotNull @NotBlank(message = "Last name cannot be blank") String lastName,
                            MultipartFile resumeFile, String skills, String experiences) {
}
