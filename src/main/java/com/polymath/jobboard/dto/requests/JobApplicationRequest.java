package com.polymath.jobboard.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record JobApplicationRequest(MultipartFile resumeUrl, String coverLetter) {
}
