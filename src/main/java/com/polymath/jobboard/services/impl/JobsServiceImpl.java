package com.polymath.jobboard.services.impl;

import com.polymath.jobboard.dto.requests.JobsRequest;
import com.polymath.jobboard.exceptions.CustomBadRequest;
import com.polymath.jobboard.exceptions.CustomNotFound;
import com.polymath.jobboard.exceptions.UserDoesNotExists;
import com.polymath.jobboard.models.Employers;
import com.polymath.jobboard.models.Jobs;
import com.polymath.jobboard.models.Users;
import com.polymath.jobboard.models.enums.JobStatus;
import com.polymath.jobboard.models.enums.UserRole;
import com.polymath.jobboard.repositories.EmployersRepository;
import com.polymath.jobboard.repositories.JobsRepositories;
import com.polymath.jobboard.repositories.UsersRepositories;
import com.polymath.jobboard.services.JobsService;
import com.polymath.jobboard.services.UserService;
import com.polymath.jobboard.utils.RoleUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobsServiceImpl implements JobsService {
    private final JobsRepositories jobsRepositories;
    private final UserService userService;
    private final UsersRepositories usersRepositories;
    private final RoleUtils roleUtils;
    private final EmployersRepository employersRepository;

    public JobsServiceImpl(JobsRepositories jobsRepositories, UserService userService, UsersRepositories usersRepositories, RoleUtils roleUtils, EmployersRepository employersRepository) {
        this.jobsRepositories = jobsRepositories;
        this.userService = userService;
        this.usersRepositories = usersRepositories;
        this.roleUtils = roleUtils;
        this.employersRepository = employersRepository;
    }

    @Override
    public void postJob(JobsRequest jobsRequest, String employerEmail) {
        if(jobsRequest.title()==null|| jobsRequest.title().isEmpty() || jobsRequest.description()==null || jobsRequest.description().isEmpty()) {
            throw new CustomBadRequest("Title and description are required");
        }
        if(jobsRequest.expiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomBadRequest("You cannot set an expiration time in the  past");
        }
        Users users = usersRepositories.findByEmail(employerEmail).orElseThrow(()->new UserDoesNotExists(String.format("User with email %s not found", employerEmail)));
        roleUtils.validateSingleRole(users.getRole());
        Employers employers = employersRepository.findByUser(users).orElseThrow(()->new UserDoesNotExists(String.format("Employer %s not found", users.getEmail())));
        JobStatus jobStatus;
        jobStatus=JobStatus.ACTIVE;
        if(jobsRequest.expiresAt().isBefore(LocalDateTime.now())) {
            jobStatus = JobStatus.EXPIRED;
        }
       // if(jobsRequest.s)
        Jobs jobs = new Jobs();
        jobs.setTitle(jobsRequest.title());
        jobs.setDescription(jobsRequest.description());
        jobs.setCategory(jobsRequest.category());
        jobs.setSalary(jobsRequest.salary());
        jobs.setEmployers(employers);
        jobs.setStatus(jobStatus);
        jobs.setExpiresAt(jobsRequest.expiresAt());
        jobs.setPostedAt(LocalDateTime.now());
        jobs.setLocation(jobsRequest.location());
       jobsRepositories.save(jobs);
    }
    @Override
    public void updateJob(JobsRequest jobRequest,Long jobId,String employerEmail) {
        if(jobRequest.title()==null || jobRequest.title().isEmpty() || jobRequest.description()==null || jobRequest.description().isEmpty()) {
            throw new CustomBadRequest("Title and description are required");
        }
        if(jobRequest.expiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomBadRequest("You cannot set an expiration time in the  past");
        }
        Jobs existingJobs = jobsRepositories.findById(jobId).orElseThrow(()->new CustomNotFound("This job does not exist"));
        existingJobs.setTitle(jobRequest.title());
        existingJobs.setDescription(jobRequest.description());
        existingJobs.setEmployers(existingJobs.getEmployers());
        existingJobs.setStatus(jobRequest.status());
        existingJobs.setExpiresAt(jobRequest.expiresAt());
        existingJobs.setLocation(jobRequest.location());
        existingJobs.setCategory(jobRequest.category());
        existingJobs.setSalary(jobRequest.salary());
        existingJobs.setPostedAt(existingJobs.getPostedAt());
        jobsRepositories.save(existingJobs);
    }

    @Override
    public void deleteJob(Long jobId,String employerEmail) {
        Users users = usersRepositories.findByEmail(employerEmail).orElseThrow(()->new UserDoesNotExists("This user does not exist"));
        Employers employers = employersRepository.findByUser(users).orElseThrow(()->new UserDoesNotExists("This employer does not exist"));
        roleUtils.validateAnyRoles(UserRole.ADMIN,employers.getUser().getRole());
        Jobs existingJobs = jobsRepositories.findById(jobId).orElseThrow(()->new CustomNotFound("This job does not exist"));
        jobsRepositories.delete(existingJobs);
    }

    @Override
    public List<Jobs> getAllJobs() {
        List<Jobs> allJobs= (List<Jobs>) jobsRepositories.findAll();
        return List.of();
    }
}
