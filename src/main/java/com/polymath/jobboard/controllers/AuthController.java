package com.polymath.jobboard.controllers;
import com.polymath.jobboard.dto.requests.LoginRequest;
import com.polymath.jobboard.dto.requests.RegisterUserRequest;
import com.polymath.jobboard.dto.response.AuthResponse;
import com.polymath.jobboard.services.UserService;
import com.polymath.jobboard.utils.responseHandler.ResponseHandler;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterUserRequest user, HttpServletResponse response) {
       AuthResponse authResponse = userService.registerUser(user,response);
       return ResponseHandler.handleResponse(authResponse, HttpStatus.CREATED,"register");

    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest user, HttpServletResponse response) {
       AuthResponse authResponse= userService.loginUser(user,response);
       return ResponseHandler.handleResponse(authResponse, HttpStatus.OK,"login");
    }
}
