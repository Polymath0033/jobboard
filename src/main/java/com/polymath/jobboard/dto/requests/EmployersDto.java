package com.polymath.jobboard.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmployersDto(@NotNull Long userId, @NotBlank String companyName,String companyDescription,String companyLogo,String websiteUrl) {
}
