package com.polymath.jobboard.controllers;

import com.polymath.jobboard.dto.response.JobsResponse;
import com.polymath.jobboard.services.JobsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/jobs")
public class PublicController {
    private final JobsService jobsService;
    public PublicController(JobsService jobsService) {
        this.jobsService = jobsService;
    }

    @GetMapping("")
    public ResponseEntity<Page<JobsResponse>> getAllJobs(Pageable pageable) {
        return ResponseEntity.ok(jobsService.getAllJobs(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobsResponse> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobsService.getJobById(id));
    }
    @GetMapping
    public ResponseEntity<Page<JobsResponse>> filterAllByJobsTitle(Pageable pageable,@RequestParam("title") String jobsTitle) {
        
    }

}
