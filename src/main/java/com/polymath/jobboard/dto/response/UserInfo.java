package com.polymath.jobboard.dto.response;

import com.polymath.jobboard.models.enums.UserRole;

public record UserInfo(Long id, String email, UserRole role){}
