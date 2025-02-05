package com.polymath.jobboard.utils;

import com.polymath.jobboard.exceptions.MultiRoleException;
import com.polymath.jobboard.exceptions.SingleRoleBasedException;
import com.polymath.jobboard.models.enums.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RoleUtils {
    public void validateMultipleRoles(UserRole... roles) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Set<String> userAuthorities = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        boolean hasAllRoles = Arrays.stream(roles).allMatch(role -> userAuthorities.contains("ROLE_"+role));
        if (!hasAllRoles) {
            Set<UserRole> rolesSet = Arrays.stream(roles).collect(Collectors.toSet());
            throw new MultiRoleException("Insufficient privileges",rolesSet,true);
        }
    }
    public void validateSingleRole(UserRole role) {
        System.out.println(role);
        if(!hasRole(role)) {
            throw new SingleRoleBasedException("Access denied",role);
        }
    }
    public void validateAnyRoles(UserRole... roles) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Set<String> userAuthorities = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        boolean hasAnyRole = Arrays.stream(roles).anyMatch(role -> userAuthorities.contains("ROLE_"+role.name()));
        if (!hasAnyRole) {
            Set<UserRole> roleSet = Arrays.stream(roles).collect(Collectors.toSet());
          throw new MultiRoleException("Insufficient privileges:This action requires one of these roles",roleSet,false);
        }
    }
    public boolean hasRole(UserRole role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_"+role.toString()));
    }
    public UserRole getCurrentRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(authority->authority.startsWith("ROLE_"))
                    .map(authority->UserRole.valueOf(authority.substring(5)))
                    .findFirst().orElse(null);
        }
        return null;
    }
}
