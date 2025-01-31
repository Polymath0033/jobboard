package com.polymath.jobboard.dto.requests;

import com.polymath.jobboard.models.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDateTime;

public record RegisterUserRequest(@NotBlank(message = "Email cannot be blank") @Email(message = "Enter a valid email") String email,
                                  @NotBlank(message = "Password cannot be blank")
                                  @Size(min = 5,message = "Password should be at least more than 5 characters") String password, UserRole role,
                                  @CurrentTimestamp LocalDateTime createdAt) {
}
