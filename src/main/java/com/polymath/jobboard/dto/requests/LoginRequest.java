package com.polymath.jobboard.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank(message = "Email cannot be blank") @Email(message = "Enter a valid email") String email,
                           @NotBlank(message = "Password cannot be blank") String password) {
}
