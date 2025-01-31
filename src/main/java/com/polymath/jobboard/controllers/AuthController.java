package com.polymath.jobboard.controllers;

import com.polymath.jobboard.dto.RegisterUser;
import com.polymath.jobboard.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
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
    public String register(RegisterUser user, HttpServletResponse response) {
        //userService.registerUser(user);
        return "success";
    }

}
