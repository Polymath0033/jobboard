package com.polymath.jobboard.controllers;

import com.polymath.jobboard.dto.requests.EmployersDto;
import com.polymath.jobboard.dto.requests.JobSeekersDto;
import com.polymath.jobboard.models.JobSeekers;
import com.polymath.jobboard.services.JobSeekerService;
import com.polymath.jobboard.utils.responseHandler.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1/user")
public class UsersController {
    private final JobSeekerService jobSeekerService;

    public UsersController(JobSeekerService jobSeekerService) {
        this.jobSeekerService = jobSeekerService;
    }

    @PostMapping("/save-job-seeker")
    public ResponseEntity<?> savedJobSeeker(@RequestBody JobSeekersDto jobSeekers){
        JobSeekers response = jobSeekerService.addJobSeeker(jobSeekers);
        return ResponseHandler.handleResponse(response,HttpStatus.CREATED,"Job Seeker details created");
    }
    @PostMapping("/save-employer")
    public void savedEmployer(@RequestBody EmployersDto employers){}
}
