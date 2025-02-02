package com.polymath.jobboard.services;

import com.polymath.jobboard.models.UserPrincipal;
import com.polymath.jobboard.models.Users;
import com.polymath.jobboard.models.enums.UserRole;
import com.polymath.jobboard.repositories.UsersRepositories;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service

public class CustomOAuth2UserService extends DefaultOAuth2UserService {


    private final UsersRepositories usersRepositories;
    private final TokenService tokenService;
    private final JwtService jwtService;
    public CustomOAuth2UserService( UsersRepositories usersRepositories,TokenService tokenService,JwtService jwtService) {
        this.usersRepositories = usersRepositories;
        this.tokenService = tokenService;
        this.jwtService = jwtService;
        log.info("CustomOauth2UserService created");
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("loadUser method called");
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("OAuth2User loaded: {}", oAuth2User.getAttributes());  // Add this log
        String email = oAuth2User.getAttribute("email");
       // String password = oAuth2User.getAttribute("password");

        Users user = usersRepositories.findByEmail(email)
                .orElseGet(() -> {
                    // Create a new user if not found
                    Users newUser = new Users();
                    newUser.setEmail(email);
                    newUser.setRole(UserRole.JOB_SEEKER);
                    String randomPassword = UUID.randomUUID().toString();
                    newUser.setPassword(encoder.encode(randomPassword));
                    return usersRepositories.save(newUser);
                });
        System.out.println("from custom oauth2UserService");
        System.out.println(user);

        //tokenService.saveToken(user,response);
        HttpServletResponse response = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getResponse();
        handleTokenGeneration(user,response);
        return new UserPrincipal(user,oAuth2User.getAttributes());
    }
    @Transactional
    protected void handleTokenGeneration(Users user, HttpServletResponse response){
        tokenService.revokeAndDeleteTokenForUser(user.getId());

        String refreshToken = jwtService.generateRefreshToken(user.getEmail());
        tokenService.saveToken(refreshToken,user);
        savedTokenOnCookies(refreshToken,response);
    }
    private void savedTokenOnCookies(String refreshToken, HttpServletResponse response) {
        Cookies(refreshToken, response);
    }

    public static void Cookies(String refreshToken, HttpServletResponse response) {
        Cookie cookie =  new Cookie("refreshToken",refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/api/auth/refresh");
        cookie.setMaxAge(6*30*24*60*60);
        response.addCookie(cookie);
    }

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
}
