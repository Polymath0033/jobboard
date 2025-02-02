package com.polymath.jobboard.security;

import com.polymath.jobboard.services.JwtService;
import com.polymath.jobboard.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Oauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final UserService userService;

    public Oauth2SuccessHandler(JwtService service,UserService userService) {
        this.jwtService = service;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        System.out.println(oAuth2AuthenticationToken);
        String email = oAuth2AuthenticationToken.getPrincipal().getAttribute("email");
        String token = jwtService.generateAccessToken(email);
        response.getWriter().write("{\"token\":\""+token+"\"}");
        response.setContentType("application/json");

    }
}
