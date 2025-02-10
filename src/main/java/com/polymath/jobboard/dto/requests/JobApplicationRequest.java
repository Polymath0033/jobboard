package com.polymath.jobboard.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record JobApplicationRequest(@NotNull @NotBlank(message = "there must be a resume") MultipartFile resumeUrl, String coverLetter) {
}
