package com.polymath.jobboard.dto.response;

import com.polymath.jobboard.models.enums.UserRole;

public record UserEmailAndRole(String email, UserRole role){}
