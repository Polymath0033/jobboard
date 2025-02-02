package com.polymath.jobboard.services;

import com.polymath.jobboard.dto.requests.LoginRequest;
import com.polymath.jobboard.dto.requests.RegisterUserRequest;
import com.polymath.jobboard.dto.response.AuthResponse;
import com.polymath.jobboard.dto.response.UserInfo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
     AuthResponse registerUser(RegisterUserRequest user, HttpServletResponse response);
     AuthResponse loginUser(LoginRequest user, HttpServletResponse response);
     UserInfo getUserInfo(String email);
}
