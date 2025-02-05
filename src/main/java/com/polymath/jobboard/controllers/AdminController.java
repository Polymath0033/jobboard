package com.polymath.jobboard.controllers;

import com.polymath.jobboard.dto.response.AllUserResponse;
import com.polymath.jobboard.services.UserDataService;
import com.polymath.jobboard.utils.responseHandler.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserDataService userDataService;

    public AdminController(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    @GetMapping("/all/users-data")
    public ResponseEntity<?> getAllUsersData() {
        AllUserResponse response = userDataService.getAllUsersData();
        return ResponseHandler.handleResponse(response,HttpStatus.OK,"GET all users with their data");
    }


    @GetMapping("/all/users")
    public ResponseEntity<?> getAllUsers() {
        AllUserResponse response = userDataService.getAllUsers();
        return ResponseHandler.handleResponse(response,HttpStatus.OK,"GET all registered users");
    }


    @GetMapping("/all/job-seekers")
    public ResponseEntity<?> getAllJobSeekers() {
        AllUserResponse response = userDataService.getAllJobSeekersData();
        return ResponseHandler.handleResponse(response,HttpStatus.OK,"GET all job seekers with their data");
    }


    @GetMapping("/all/employers")
    public ResponseEntity<?> getAllEmployers() {
        AllUserResponse response = userDataService.getAllEmployersData();
        return ResponseHandler.handleResponse(response,HttpStatus.OK,"GET all employers with their data");
    }

    @DeleteMapping("/job-seeker/{id}")
    public ResponseEntity<?> deleteJobSeeker(@PathVariable Long id) {
        userDataService.deleteJobSeeker(id);
        return ResponseHandler.handleResponse(null,HttpStatus.NO_CONTENT,"Delete job seeker with id: " + id);
    }

    @DeleteMapping("/employer/{id}")
    public ResponseEntity<?> deleteEmployer(@PathVariable Long id) {
        userDataService.deleteEmployer(id);
        return ResponseHandler.handleResponse(null,HttpStatus.OK,"Delete employer with id: " + id);
    }


}
