package com.polymath.jobboard.controllers;
import com.polymath.jobboard.dto.requests.*;
import com.polymath.jobboard.dto.response.AuthResponse;
import com.polymath.jobboard.dto.response.RefreshTokenResponse;
import com.polymath.jobboard.dto.response.ResetTokenResponse;
import com.polymath.jobboard.services.JwtService;
import com.polymath.jobboard.services.ResetTokenService;
import com.polymath.jobboard.services.TokenService;
import com.polymath.jobboard.services.UserService;
import com.polymath.jobboard.utils.responseHandler.ResponseHandler;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final TokenService tokenService;
    private final ResetTokenService resetTokenService;

    public AuthController(UserService userService, TokenService tokenService, JwtService jwtService, ResetTokenService resetTokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.resetTokenService = resetTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUserRequest user, HttpServletResponse response) {
       AuthResponse authResponse = userService.registerUser(user,response);
       return ResponseHandler.handleResponse(authResponse, HttpStatus.CREATED,"register");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest user, HttpServletResponse response) {
       AuthResponse authResponse= userService.loginUser(user,response);
       return ResponseHandler.handleResponse(authResponse, HttpStatus.OK,"login");
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request, HttpServletResponse response) {
        userService.changePassword(request.oldPassword(),request.newPassword(),request.email());
        return ResponseHandler.handleResponse(null, HttpStatus.OK,"Successfully changed password");
    }

    @PutMapping("/change-role")
    public ResponseEntity<?> changeRole(@Valid @RequestBody ChangeUserRoleRequest request, HttpServletResponse response) {
        userService.changeUserRole(request.email(),request.role());
        return ResponseHandler.handleResponse(null, HttpStatus.OK,"Successfully changed role");
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        //String email = jwtService.extractEmail(token);
        RefreshTokenResponse tokenData = tokenService.getRefreshToken(token);
        return ResponseHandler.handleResponse(tokenData, HttpStatus.OK,"token data");
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<?> generateRefreshToken(@RequestBody RefreshTokenDto request) {
        RefreshTokenResponse response = tokenService.generateRefreshToken(request.email());
        return ResponseHandler.handleResponse(response, HttpStatus.OK,"refresh token");
    }

    @PostMapping("/reset-token")
    public ResponseEntity<?> generateResetToken(@RequestBody RefreshTokenDto request) {
        ResetTokenResponse response = resetTokenService.generateInitialPasswordReset(request.email());
        return ResponseHandler.handleResponse(response, HttpStatus.OK,"reset token");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetTokenDto request){
        resetTokenService.resetPassword(request.resetToken(),request.newPassword());
        return ResponseHandler.handleResponse(null, HttpStatus.OK,"password reset successfully reset password");
    }




}
