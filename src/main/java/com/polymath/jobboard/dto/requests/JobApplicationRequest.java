package com.polymath.jobboard.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JobApplicationRequest(@NotNull @NotBlank(message = "there must be a resume") String resumeUrl,String coverLetter) {
}
