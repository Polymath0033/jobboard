package com.polymath.jobboard.dto.requests;

public record ChangePasswordRequest(String oldPassword, String newPassword,String email) {
}
