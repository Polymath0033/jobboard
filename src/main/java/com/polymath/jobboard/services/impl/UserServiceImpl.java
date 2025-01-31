package com.polymath.jobboard.services.impl;

import com.polymath.jobboard.dto.requests.LoginRequest;
import com.polymath.jobboard.dto.requests.RegisterUserRequest;
import com.polymath.jobboard.models.Tokens;
import com.polymath.jobboard.models.Users;
import com.polymath.jobboard.repositories.TokenRepository;
import com.polymath.jobboard.repositories.UsersRepositories;
import com.polymath.jobboard.services.JwtService;
import com.polymath.jobboard.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class UserServiceImpl implements UserService {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private final UsersRepositories usersRepositories;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    @Autowired
    AuthenticationManager authenticationManager;
    public UserServiceImpl(UsersRepositories usersRepositories,TokenRepository tokenRepository,JwtService jwtService) {
        this.usersRepositories = usersRepositories;
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
    }

    public void registerUser(RegisterUserRequest user,HttpServletResponse response) {
        Users users = new Users();
        users.setEmail(user.email());
        users.setPassword(user.password());
        users.setRole(user.role());
        users.setCreatedAt(user.createdAt());
        usersRepositories.save(users);
        users.setPassword(encoder.encode(user.password()));
        String accessToken = jwtService.generateAccessToken(user.email());
        String refreshToken = jwtService.generateRefreshToken(user.email());
        Tokens tokens = new Tokens();
        tokens.setUser(users);
        tokens.setRefreshToken(refreshToken);
        tokens.setIssuedAt(LocalDateTime.now());
        tokens.setExpiresAt(LocalDateTime.now().plusMonths(6));
        tokenRepository.save(tokens);
        savedTokenOnCookies(refreshToken,response);

    }
    public void loginUser(LoginRequest user,HttpServletResponse response) {
       Users existingUser = usersRepositories.findByEmail(user.email());
       try{
           Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.email(), user.password()));
           if(authentication.isAuthenticated()) {
               String accessToken = jwtService.generateAccessToken(user.email());
               String refreshToken = jwtService.generateRefreshToken(user.email());
               Tokens tokens = new Tokens();

           }

       } catch (Exception e) {
           throw new RuntimeException(e);
       }
    }
    private void savedTokenOnCookies(String refreshToken, HttpServletResponse response) {
        Cookie cookie =  new Cookie("refreshToken",refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/api/auth/refresh");
        cookie.setMaxAge(6*30*24*60*60);
        response.addCookie(cookie);

    }

}
