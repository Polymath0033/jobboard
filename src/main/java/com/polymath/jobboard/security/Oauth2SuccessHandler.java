package com.polymath.jobboard.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polymath.jobboard.dto.response.AuthResponse;
import com.polymath.jobboard.dto.response.UserInfo;
import com.polymath.jobboard.exceptions.UserDoesNotExists;
import com.polymath.jobboard.services.JwtService;
import com.polymath.jobboard.services.UserDataService;
import com.polymath.jobboard.services.UserService;
import com.polymath.jobboard.utils.responseHandler.ResponseHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Oauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final UserService userService;
    private final ObjectMapper objectMapper;


    public Oauth2SuccessHandler(JwtService service, UserService userService, ObjectMapper objectMapper) {
        this.jwtService = service;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
            System.out.println(oAuth2AuthenticationToken);
            String email = oAuth2AuthenticationToken.getPrincipal().getAttribute("email");
            if(email == null) {
                throw new UserDoesNotExists("Email not found in oauth response ");
            }
            String accessToken = jwtService.generateAccessToken(email);
//            response.getWriter().write("{\"token\":\""+token+"\"}");
//            response.setContentType("application/json");
            UserInfo userInfo = userService.getUserInfo(email);
            AuthResponse authResponse = new AuthResponse(accessToken, userInfo);
            sendSuccessResponse(response,authResponse);
        } catch (IOException e) {
            sendFailureResponse(response);
        }
    }
    private void sendSuccessResponse(HttpServletResponse response, Object data) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
        ResponseEntity<Object> responseEntity = ResponseHandler.handleResponse(data, HttpStatus.OK, "Oauth2 successfull");
        response.getWriter().write(objectMapper.writeValueAsString(responseEntity.getBody()));
    }
    private void sendFailureResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setContentType("application/json");
        ResponseEntity<Object> responseEntity = ResponseHandler.handleResponse(null, HttpStatus.INTERNAL_SERVER_ERROR, "Authentication fAiled");
        response.getWriter().write(objectMapper.writeValueAsString(responseEntity.getBody()));
    }
}
