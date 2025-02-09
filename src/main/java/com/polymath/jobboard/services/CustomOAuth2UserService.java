package com.polymath.jobboard.services;

import com.polymath.jobboard.models.JobSeekers;
import com.polymath.jobboard.models.UserPrincipal;
import com.polymath.jobboard.models.Users;
import com.polymath.jobboard.models.enums.UserRole;
import com.polymath.jobboard.repositories.JobSeekersRepository;
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
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service

public class CustomOAuth2UserService extends DefaultOAuth2UserService {


    private final UsersRepositories usersRepositories;
    private final TokenService tokenService;
    private final JwtService jwtService;
    private final JobSeekersRepository jobSeekersRepository;

    public CustomOAuth2UserService(UsersRepositories usersRepositories, TokenService tokenService, JwtService jwtService, UserDataService userDataService, JobSeekersRepository jobSeekersRepository) {
        this.usersRepositories = usersRepositories;
        this.tokenService = tokenService;
        this.jwtService = jwtService;
        this.jobSeekersRepository = jobSeekersRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User);
        String email = oAuth2User.getAttribute("email");
        String firstName = extractFirstName(oAuth2User);
        String lastName = extractLastName(oAuth2User);
       // String password = oAuth2User.getAttribute("password");

        Users user = usersRepositories.findByEmail(email)
                .orElseGet(() -> {
                    // Create a new user if not found
                    Users newUser = new Users();
                    newUser.setEmail(email);
                    newUser.setRole(UserRole.JOB_SEEKER);
                    String randomPassword = UUID.randomUUID().toString();
                    newUser.setPassword(encoder.encode(randomPassword));
                    Users savedUser =usersRepositories.save(newUser);
                    Optional<JobSeekers> jobSeekers=jobSeekersRepository.findByJobSeekerEmail(email);
                    if(jobSeekers.isEmpty()){
                        createInitialJobSeeker(newUser,firstName,lastName);
                    }
                    return savedUser;
                });

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

    private String extractFirstName(OAuth2User auth2User){
        String givenName = auth2User.getAttribute("given_name");
        if(givenName != null){
            return givenName;
        }
        String name = auth2User.getAttribute("name");
        if(name != null&&name.contains(" ")){
            return name.split(" ")[0];
        }
        return null;
    }
    private String extractLastName(OAuth2User auth2User){
       String familyName = auth2User.getAttribute("family_name");
       if(familyName != null){
           return familyName;
       }
        String name = auth2User.getAttribute("name");
       if(name != null&&name.contains(" ")){
           String[] parts=name.split(" ");
           return parts[parts.length-1];
       }
       return null;
    }
    public static void Cookies(String refreshToken, HttpServletResponse response) {
        Cookie cookie =  new Cookie("refreshToken",refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/api/auth/refresh");
        cookie.setMaxAge(6*30*24*60*60);
        response.addCookie(cookie);
    }
    private void createInitialJobSeeker(Users user,String firstName,String lastName){
        if((firstName!=null&&!firstName.isEmpty())&&(lastName!=null&&!lastName.isEmpty())){
            JobSeekers jobSeekers = new JobSeekers();
            jobSeekers.setFirstName(firstName);
            jobSeekers.setLastName(lastName);
            jobSeekers.setUser(user);
            jobSeekersRepository.save(jobSeekers);
        }
    }
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
}
