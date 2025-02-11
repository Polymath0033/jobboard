package com.polymath.jobboard.controllers;

import com.polymath.jobboard.dto.requests.JobsRequest;
import com.polymath.jobboard.dto.response.JobsResponse;
import com.polymath.jobboard.models.Jobs;
import com.polymath.jobboard.services.JobsService;
import com.polymath.jobboard.services.JwtService;
import com.polymath.jobboard.utils.responseHandler.ResponseHandler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employer")
@PreAuthorize("hasRole('EMPLOYER')")
public class EmployersController {

    private final JobsService jobsService;
    private final JwtService jwtService;

    public EmployersController(JobsService jobsService, JwtService jwtService) {
        this.jobsService = jobsService;
        this.jwtService = jwtService;
    }

//    @GetMapping("/jobs")
//    public ResponseEntity<Page<JobsResponse>> getAllJobs(Pageable pageable) {
//        Page<JobsResponse> jobsPage = jobsService.getAllJobs(pageable);
//        return new ResponseEntity<>(jobsPage, HttpStatus.OK);
//
//    }
    @PostMapping("/jobs")
    public ResponseEntity<?> postJob(@RequestBody JobsRequest jobsRequest, @RequestHeader("Authorization") String token) {
        String authHeader=token.substring(7);
        String email = jwtService.extractEmail(authHeader);
        jobsService.postJob(jobsRequest, email);
        return ResponseHandler.handleResponse(null, HttpStatus.CREATED,"New job is added");
    }

    @PutMapping("/jobs/{id}")
    public ResponseEntity<?> updateJob(@PathVariable("id") Long id, @RequestBody JobsRequest jobsRequest, @RequestHeader("Authorization") String token) {
        String authHeader=token.substring(7);
        String email = jwtService.extractEmail(authHeader);
        jobsService.updateJob(jobsRequest,id,email);
        return ResponseHandler.handleResponse(null, HttpStatus.OK,"Job is updated");
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable("id") Long id, @RequestHeader("Authorization") String token) {
        String authHeader=token.substring(7);
        String email = jwtService.extractEmail(authHeader);
        jobsService.deleteJob(id, email);
        return ResponseHandler.handleResponse(null, HttpStatus.OK,"Job is deleted");
    }

}
