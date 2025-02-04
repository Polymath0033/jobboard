package com.polymath.jobboard.dto.response;

import com.polymath.jobboard.models.enums.UserRole;

public record EmployerDto(Long id, String email, UserRole role, String companyName, String companyDescription, String logoUrl, String websiteUrl) {
}
