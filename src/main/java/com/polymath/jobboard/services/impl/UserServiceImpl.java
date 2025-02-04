package com.polymath.jobboard.services.impl;

import com.polymath.jobboard.dto.requests.LoginRequest;
import com.polymath.jobboard.dto.requests.RegisterUserRequest;
import com.polymath.jobboard.dto.response.AuthResponse;
import com.polymath.jobboard.dto.response.UserInfo;
import com.polymath.jobboard.exceptions.InvalidEntity;
import com.polymath.jobboard.exceptions.UserAlreadyExists;
import com.polymath.jobboard.exceptions.UserDoesNotExists;
import com.polymath.jobboard.models.Users;
import com.polymath.jobboard.models.enums.UserRole;
import com.polymath.jobboard.repositories.UsersRepositories;
import com.polymath.jobboard.services.CustomOAuth2UserService;
import com.polymath.jobboard.services.JwtService;
import com.polymath.jobboard.services.TokenService;
import com.polymath.jobboard.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private final UsersRepositories usersRepositories;
    private final JwtService jwtService;
    private final TokenService tokenService;
    @Autowired
    AuthenticationManager authenticationManager;
    public UserServiceImpl(UsersRepositories usersRepositories,  JwtService jwtService, TokenService tokenService) {
        this.usersRepositories = usersRepositories;
        this.tokenService= tokenService;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse registerUser(RegisterUserRequest user, HttpServletResponse response) {
        Optional<Users> existingUser = usersRepositories.findByEmail(user.email());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExists("User already exist "+existingUser.get().getEmail());
        }

        UserRole role;
        if(user.role()==null){
            role=UserRole.JOB_SEEKER;
        }else {
            try {
                role = UserRole.valueOf(user.role().toString().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidEntity("Invalid role"+user.role());
            }
        }
        Users users = new Users();
        users.setEmail(user.email());
        users.setPassword(encoder.encode(user.password()));
        users.setRole(role);
        users.setCreatedAt(LocalDateTime.now());
        usersRepositories.save(users);
        String accessToken;
        accessToken = jwtService.generateAccessToken(user.email());
        handleTokenGeneration(users,response);
        System.out.println(accessToken);
       // AuthResponse authResponse = new AuthResponse();
        return new  AuthResponse(accessToken,new UserInfo(users.getId(),users.getEmail(),users.getRole()));

    }
    @Transactional
    public AuthResponse loginUser(LoginRequest user,HttpServletResponse response) {
        Users existingUser = usersRepositories.findByEmail(user.email()).orElseThrow(()->new UserDoesNotExists("User does not exist"));
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.email(), user.password()));
            if (authentication.isAuthenticated()) {
                String accessToken = jwtService.generateAccessToken(existingUser.getEmail());
               handleTokenGeneration(existingUser,response);
               return new AuthResponse(accessToken,new UserInfo(existingUser.getId(),existingUser.getEmail(),existingUser.getRole()));
            }else {
                return new AuthResponse(null,null);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void handleTokenGeneration(Users user, HttpServletResponse response){
        tokenService.revokeAndDeleteTokenForUser(user.getId());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());
        tokenService.saveToken(refreshToken,user);
        savedTokenOnCookies(refreshToken,response);
    }

    private void savedTokenOnCookies(String refreshToken, HttpServletResponse response) {
        CustomOAuth2UserService.Cookies(refreshToken, response);
    }
    public UserInfo getUserInfo(String email){
        Users users = usersRepositories.findByEmail(email).orElseThrow(()->new UserDoesNotExists("User does not exist"));
        return new UserInfo(users.getId(),users.getEmail(),users.getRole());
    }

}
