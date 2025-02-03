package com.polymath.jobboard.services.impl;

import com.polymath.jobboard.dto.requests.JobSeekersDto;
import com.polymath.jobboard.exceptions.CustomBadRequest;
import com.polymath.jobboard.exceptions.UserDoesNotExists;
import com.polymath.jobboard.models.JobSeekers;
import com.polymath.jobboard.models.Users;
import com.polymath.jobboard.models.enums.UserRole;
import com.polymath.jobboard.repositories.JobSeekersRepository;
import com.polymath.jobboard.repositories.UsersRepositories;
import com.polymath.jobboard.services.JobSeekerService;
import com.polymath.jobboard.services.JwtService;
import com.polymath.jobboard.services.MyUserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class JobSeekersServiceImpl implements JobSeekerService {
    private final JobSeekersRepository jobSeekersRepository;
    private final UsersRepositories usersRepositories;
    private final JwtService jwtService;
    private final MyUserDetailsService myUserDetailsService;
    public JobSeekersServiceImpl(JobSeekersRepository jobSeekersRepository, UsersRepositories usersRepositories, JwtService jwtService,MyUserDetailsService myUserDetailsService) {
        this.jobSeekersRepository = jobSeekersRepository;
        this.usersRepositories = usersRepositories;
        this.jwtService = jwtService;
        this.myUserDetailsService = myUserDetailsService;

    }

    @Override
    public JobSeekers addJobSeeker(JobSeekersDto request) {
        String accessToken = jwtService.generateAccessToken(request.email());
        System.out.println("Generated access token: " + accessToken);

        String extractedEmail = jwtService.extractEmail(accessToken);
        System.out.println("Extracted email: " + extractedEmail);

        boolean isValid = jwtService.validateToken(accessToken, myUserDetailsService.loadUserByUsername(request.email()));
        System.out.println("Token is valid: " + isValid);
        try {
            if(request.email()==null||request.firstName()==null||request.lastName()==null){
                throw new CustomBadRequest("Enter appropriate data");
            }
            Users user= usersRepositories.findByEmail(request.email()).orElseThrow(() -> new UserDoesNotExists("User is not authenticated yet"));
            UserRole role = user.getRole();
            if(role!=UserRole.JOB_SEEKER){
                throw new UserDoesNotExists("You need to register as a job seeker in order to access this");
            }
            JobSeekers jobSeekers = new JobSeekers();
            jobSeekers.setFirstName(request.firstName());
            jobSeekers.setLastName(request.lastName());
            jobSeekers.setUser(user);
            jobSeekers.setResumeUrl(request.resumeUrl());
            jobSeekers.setSkills(request.skills());
            jobSeekers.setExperiences(request.experiences());
            jobSeekersRepository.save(jobSeekers);
            return new JobSeekers();
        } catch (CustomBadRequest | UserDoesNotExists e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void updateJobSeeker(JobSeekersDto jobSeeker) {

    }

    @Override
    public void deleteJobSeeker(JobSeekersDto jobSeeker) {

    }
}
