package com.polymath.jobboard.controllers;

import com.polymath.jobboard.dto.requests.EmployersDto;
import com.polymath.jobboard.dto.requests.JobSeekersDto;
import com.polymath.jobboard.dto.response.EmployersResponse;
import com.polymath.jobboard.dto.response.JobSeekersResponse;
import com.polymath.jobboard.services.UserDataService;
import com.polymath.jobboard.services.JwtService;
import com.polymath.jobboard.utils.responseHandler.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UsersController {

    private final UserDataService userDataService;
    private final JwtService jwtService;

    public UsersController(UserDataService userDataService, JwtService jwtService) {
        this.userDataService = userDataService;
        this.jwtService = jwtService;
    }

    @PostMapping("/job-seeker")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<?> savedJobSeeker(@RequestBody JobSeekersDto jobSeekers){
        JobSeekersResponse response = userDataService.addJobSeeker(jobSeekers);
        return ResponseHandler.handleResponse(response,HttpStatus.CREATED,"Job Seeker details created");
    }

    @PutMapping("/job-seeker/{id}")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<?> updateJobSeeker(@RequestBody JobSeekersDto jobSeekers, @PathVariable Long id){
        JobSeekersResponse response = userDataService.updateJobSeeker(id,jobSeekers);
        return ResponseHandler.handleResponse(response,HttpStatus.OK,"Job Seeker details updated");
   }

   @GetMapping("/job-seeker")
   @PreAuthorize("hasRole('JOB_SEEKER')")
   public ResponseEntity<?> getJobSeekers(@RequestHeader("Authorization") String authHeader){
    String token = authHeader.substring(7);
    String email = jwtService.extractEmail(token);
    JobSeekersResponse response = userDataService.getJobSeeker(email);
    return ResponseHandler.handleResponse(response,HttpStatus.OK,"job seeker data");
    }

    @PostMapping("/employer")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<?> savedEmployer(@RequestBody EmployersDto employers){
        EmployersResponse response = userDataService.addNewEmployer(employers);
        return ResponseHandler.handleResponse(response,HttpStatus.CREATED,"Employer details created");
    }

    @PutMapping("/employer/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<?> updateEmployer(@RequestBody EmployersDto employers, @PathVariable Long id){
        EmployersResponse response = userDataService.updateEmployer(id,employers);
        return ResponseHandler.handleResponse(response,HttpStatus.OK,"Employer details updated");
    }
    @GetMapping("/employer")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<?> getEmployers(@RequestHeader("Authorization") String authHeader){
        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);
        EmployersResponse response = userDataService.getEmployer(email);
        return ResponseHandler.handleResponse(response,HttpStatus.OK,"employer data");
    }
}
