package com.polymath.jobboard.security;

import com.polymath.jobboard.services.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private  final JwtFilter filter;
    private  final Oauth2SuccessHandler oauth2SuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(UserDetailsService userDetailsService,JwtFilter filter,@Lazy Oauth2SuccessHandler oauth2SuccessHandler,CustomOAuth2UserService customOAuth2UserService) {
        this.filter = filter;
        this.userDetailsService = userDetailsService;
        this.oauth2SuccessHandler = oauth2SuccessHandler;
        this.customOAuth2UserService = customOAuth2UserService;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req->
                        req.requestMatchers(
                                "/api/auth/**","/api/v1/jobs","/api/v1/jobs/{id}").permitAll()
//                                JOB_SEEKERS
                                .requestMatchers("/api/v1/jobs-seeker/**","/api/v1/jobs/{id}/apply","/api/v1/jobs/{id}/save").hasRole("JOB_SEEKER")
//                                 EMPLOYER
                                .requestMatchers("/api/v1/employer/**","/api/v1/jobs/{id}/applications").hasRole("EMPLOYER")
//                                ADMIN
                                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN").anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .oauth2Login(oauth2->oauth2
                        .userInfoEndpoint(info->info
                                .userService(customOAuth2UserService))
                        .successHandler(oauth2SuccessHandler))
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
