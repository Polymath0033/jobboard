package com.polymath.jobboard.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record EmployersDto(@NotNull @NotBlank String email, @NotNull @NotBlank String companyName, String companyDescription,
                           MultipartFile companyLogo, String websiteUrl) {
}
