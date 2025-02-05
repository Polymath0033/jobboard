package com.polymath.jobboard.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmployersDto(@NotNull @NotBlank String email,@NotNull @NotBlank String companyName,String companyDescription,String companyLogo,String websiteUrl) {
}
