package com.polymath.jobboard.exceptions;

import com.polymath.jobboard.models.enums.UserRole;
import lombok.Getter;

import java.util.Set;

@Getter
public class MultiRoleException extends RuntimeException {
    private final Set<UserRole> requiredRoles;
    private final boolean requireAll;

    public MultiRoleException(String message,Set<UserRole> requiredRoles, boolean requireAll) {
        super(message);
        this.requiredRoles = requiredRoles;
        this.requireAll = requireAll;
    }

}
