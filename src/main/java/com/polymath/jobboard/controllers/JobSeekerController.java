package com.polymath.jobboard.controllers;
import com.polymath.jobboard.dto.requests.JobAlertsRequest;
import com.polymath.jobboard.dto.requests.JobsId;
import com.polymath.jobboard.dto.response.SavedJobsResponse;
import com.polymath.jobboard.services.JobAlertsService;
import com.polymath.jobboard.services.JwtService;
import com.polymath.jobboard.services.SavedJobsService;
import com.polymath.jobboard.utils.responseHandler.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasRole('JOB_SEEKER')")
@RequestMapping("/api/v1")
public class JobSeekerController {

    private final SavedJobsService savedJobsService;
    private final JwtService jwtService;
    private final JobAlertsService jobAlertsService;

    public JobSeekerController(SavedJobsService savedJobsService, JwtService jwtService, JobAlertsService jobAlertsService) {
        this.savedJobsService = savedJobsService;
        this.jwtService = jwtService;
        this.jobAlertsService = jobAlertsService;
    }

//    Saved job endpoint
    @GetMapping("/saved-job")
    public ResponseEntity<?> getAllSavedJobsByAJobSeeker(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);
        List<SavedJobsResponse> responses = savedJobsService.getAllSavedJobsByJobSeeker(email);
        return ResponseHandler.handleResponse(responses,HttpStatus.OK,"Successfully retrieved all jobs");
    }

    @GetMapping("/saved-job/{id}")
    public ResponseEntity<?> getSavedJobById(@RequestHeader("Authorization") String authHeader,@PathVariable Long id) {
        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);
        SavedJobsResponse response = savedJobsService.getSavedJobsById(id,email);
        return ResponseHandler.handleResponse(response,HttpStatus.OK,"Successfully retrieved job");
    }

    @PostMapping("/saved-job")
    public ResponseEntity<?> saveJobs(@RequestBody JobsId jobsId, @RequestHeader("Authorization") String authorization) {
        String token = authorization.substring(7);
        String email = jwtService.extractEmail(token);
        savedJobsService.saveJob(jobsId.jobId(), email);
        return ResponseHandler.handleResponse(null, HttpStatus.OK,"Successfully saved job");
    }

    @DeleteMapping("saved-job/{id}")
    public ResponseEntity<?> deleteJobs(@PathVariable("id") Long savedJobId, @RequestHeader("Authorization") String authorization) {
        String token = authorization.substring(7);
        String email = jwtService.extractEmail(token);
        savedJobsService.deleteJob(savedJobId,email);
        return ResponseHandler.handleResponse(null, HttpStatus.OK,"Successfully deleted");
    }


//    Job alerts endpoint

    @PostMapping("/job-alerts")
    public ResponseEntity<?> setJobAlerts(@RequestBody JobAlertsRequest alert, @RequestHeader("Authorization") String authorization) {
        String token = authorization.substring(7);
        String email = jwtService.extractEmail(token);
        jobAlertsService.setJobAlerts(alert,email);
        return ResponseHandler.handleResponse(null, HttpStatus.OK,"Successfully set job alerts");
    }

    @DeleteMapping("job-alerts/{id}")
    public ResponseEntity<?> deleteJobAlerts(@PathVariable("id") Long jobAlertId, @RequestHeader("Authorization") String authorization) {
        String token = authorization.substring(7);
        String email = jwtService.extractEmail(token);
        jobAlertsService.deleteJobAlerts(jobAlertId,email);
        return ResponseHandler.handleResponse(null, HttpStatus.OK,"Successfully deleted");
    }


}
