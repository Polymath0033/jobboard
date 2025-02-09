package com.polymath.jobboard.services.impl;

import com.polymath.jobboard.dto.response.ResetTokenResponse;
import com.polymath.jobboard.exceptions.CustomBadRequest;
import com.polymath.jobboard.exceptions.UserDoesNotExists;
import com.polymath.jobboard.models.ResetToken;
import com.polymath.jobboard.models.Users;
import com.polymath.jobboard.repositories.ResetTokenRepositories;
import com.polymath.jobboard.repositories.UsersRepositories;
import com.polymath.jobboard.services.ResetTokenService;
import com.polymath.jobboard.utils.CalculateExpiresTime;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
@Service
public class ResetTokenServiceImpl implements ResetTokenService {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private final UsersRepositories usersRepositories;
    private final ResetTokenRepositories resetTokenRepositories;

    public ResetTokenServiceImpl(UsersRepositories usersRepositories, ResetTokenRepositories resetTokenRepositories) {
        this.usersRepositories = usersRepositories;
        this.resetTokenRepositories = resetTokenRepositories;
    }

    @Override
    public ResetTokenResponse generateInitialPasswordReset(String email) {
        Users user = usersRepositories.findByEmail(email).orElseThrow(()->new UserDoesNotExists("User not found"));
        String token = UUID.randomUUID().toString();
        Optional<ResetToken> existingResetToken = resetTokenRepositories.findByUserEmail(email);
        if (existingResetToken.isPresent()) {
            ResetToken resetToken = existingResetToken.get();
            return new ResetTokenResponse(resetToken.getToken(), CalculateExpiresTime.calculateExpiresIn(resetToken.getExpiresAt()),resetToken.getUser().getEmail());
        }
        ResetToken resetToken = new ResetToken();
        resetToken.setToken(UUID.fromString(token));
        resetToken.setUser(user);
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        resetTokenRepositories.save(resetToken);
       // ResetToken storedToken = resetTokenRepositories.
        return new ResetTokenResponse(resetToken.getToken(),CalculateExpiresTime.calculateExpiresIn(resetToken.getExpiresAt()),user.getEmail());
    }

    @Override
    @Transactional
    public void resetPassword(String resetPasswordToken, String newPassword) {
        ResetToken resetToken=resetTokenRepositories.findByToken(UUID.fromString(resetPasswordToken)).orElseThrow(()->new CustomBadRequest("Invalid or expired reset token"));
        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomBadRequest("Reset token expired");
        }
        resetToken.getUser().setPassword(encoder.encode(newPassword));
        resetTokenRepositories.delete(resetToken);
    }
}
