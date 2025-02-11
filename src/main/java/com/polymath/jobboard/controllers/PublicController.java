package com.polymath.jobboard.controllers;

import com.polymath.jobboard.dto.response.JobsResponse;
import com.polymath.jobboard.services.JobsService;
import com.polymath.jobboard.services.JwtService;
import com.polymath.jobboard.utils.responseHandler.ResponseHandler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/jobs")
public class PublicController {
    private final JobsService jobsService;
    private final JwtService jwtService;
    public PublicController(JobsService jobsService, JwtService jwtService) {
        this.jobsService = jobsService;
        this.jwtService = jwtService;
    }

    @GetMapping("")
    public ResponseEntity<Page<JobsResponse>> getAllJobs(Pageable pageable) {
//Implementing this later
//        if(authHeader != null && authHeader.startsWith("Bearer ")) {
//            String token = authHeader.substring(7);
//            String email = jwtService.extractEmail(token);
//            return ResponseEntity.ok(jobsService.getAllJobsForAuthorizedUsers(pageable, email));
//        }
        return ResponseEntity.ok(jobsService.getAllJobs(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobsResponse> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobsService.getJobById(id));
    }
    @GetMapping("/search/title")
    public ResponseEntity<Object> filterAllByJobsTitle(Pageable pageable, @RequestParam("title") String jobsTitle) {
     Page<JobsResponse> responses=   jobsService.searchJobsByTitle(jobsTitle,pageable);
     return ResponseHandler.handleResponse(responses, HttpStatus.OK,"");
    }
    @GetMapping("/search/advanced")
    public ResponseEntity<Object> searchJobsByTitleOrDescriptionOrCategory(@RequestParam("search") String search, Pageable pageable) {
        Page<JobsResponse> responses = jobsService.advanceSearch(search,pageable);
        return ResponseHandler.handleResponse(responses,HttpStatus.OK,"");
    }
   @GetMapping("/filter")
    public ResponseEntity<Object> filterSearch(@RequestParam(value = "title",required = false) String title, @RequestParam(value = "description",required = false) String description, @RequestParam(value = "category",required = false) String category, @RequestParam(value = "location",required = false) String location, @RequestParam(value = "companyName",required = false) String companyName, @RequestParam(value = "minSalary",required = false) Double minSalary, @RequestParam(value = "maxSalary",required = false) Double maxSalary, @RequestParam(value = "startsAt",required = false)LocalDateTime startsAt,@RequestParam(value = "endsAt",required = false) LocalDateTime endsAt, Pageable pageable) {
        Page<JobsResponse> responses = jobsService.filterJobs(title, description, companyName, location, category, minSalary, maxSalary, startsAt, endsAt,pageable);
        return ResponseHandler.handleResponse(responses,HttpStatus.OK,"");
    }

}
