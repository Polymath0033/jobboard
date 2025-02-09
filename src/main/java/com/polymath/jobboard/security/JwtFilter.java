package com.polymath.jobboard.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polymath.jobboard.exceptions.CustomNotAuthorized;
import com.polymath.jobboard.services.JwtService;
import com.polymath.jobboard.services.MyUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MyUserDetailsService myUserDetailsService;
    public JwtFilter(JwtService jwtService, MyUserDetailsService myUserDetailsService) {
        this.jwtService = jwtService;
        this.myUserDetailsService = myUserDetailsService;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,@NotNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            String authHeader = request.getHeader("Authorization");
            String token = null;
            String email = null;
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
              token = authHeader.substring(7);
                email = jwtService.extractEmail(token);
            }
            if(email != null&& SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = myUserDetailsService.loadUserByUsername(email);
                if(jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }else {
                    throw new BadCredentialsException("Your access token has expired, please log in again");
                  //  System.out.println("3. Invalid token");
                }

            }
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("status", HttpStatus.UNAUTHORIZED.value());
            responseBody.put("message", "Authentication expired, please log in again");
            responseBody.put("data", null);

            new ObjectMapper().writeValue(response.getOutputStream(), responseBody);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        filterChain.doFilter(request, response);

    }
}
