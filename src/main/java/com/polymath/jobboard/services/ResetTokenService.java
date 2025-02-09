package com.polymath.jobboard.services;

import com.polymath.jobboard.dto.response.ResetTokenResponse;
import org.springframework.stereotype.Service;

@Service
public interface ResetTokenService {
    ResetTokenResponse generateInitialPasswordReset(String email);
    void resetPassword(String resetPasswordToken, String newPassword);
}
