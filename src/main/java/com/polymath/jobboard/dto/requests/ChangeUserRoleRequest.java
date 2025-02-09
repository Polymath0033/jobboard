package com.polymath.jobboard.dto.requests;

import com.polymath.jobboard.models.enums.UserRole;

public record ChangeUserRoleRequest(String email, UserRole role)  {
}
