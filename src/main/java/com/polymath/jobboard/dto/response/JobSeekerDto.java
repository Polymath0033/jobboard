package com.polymath.jobboard.dto.response;

import com.polymath.jobboard.models.enums.UserRole;

public record JobSeekerDto(Long id, String email, UserRole role, String firstName, String lastName, String resumeUrl, String skills, String experiences) {}
