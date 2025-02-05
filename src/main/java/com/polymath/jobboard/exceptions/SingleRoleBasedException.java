package com.polymath.jobboard.exceptions;

import com.polymath.jobboard.models.enums.UserRole;
import lombok.Getter;

@Getter
public class SingleRoleBasedException extends RuntimeException {
    private final UserRole role;
    public SingleRoleBasedException(String message, UserRole role) {
        super(message);
        this.role = role;
    }
}
