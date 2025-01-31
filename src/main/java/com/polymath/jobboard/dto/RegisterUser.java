package com.polymath.jobboard.dto;

import com.polymath.jobboard.models.enums.UserRole;

public record RegisterUser(String email, String password, UserRole role) {

}
