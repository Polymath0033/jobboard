package com.polymath.jobboard.controllers;

import com.polymath.jobboard.services.JobAlertsService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class JobsController {
    private final JobAlertsService jobAlertsService;

    public JobsController(JobAlertsService jobAlertsService) {
        this.jobAlertsService = jobAlertsService;
    }

    @GetMapping("/unsubscribe/{id}/{email}")
    public ResponseEntity<?> unsubscribeFromJobAlerts(@PathVariable("id") Long id, @PathVariable("email") String email) {
        jobAlertsService.deleteJobAlerts(id,email);
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body("<html><body style='display:flex;justify-items:center;align-items:center'><h1>You've successfully unsubscribe from Job board Job Alerts</h1></body></html>");

    }

}
