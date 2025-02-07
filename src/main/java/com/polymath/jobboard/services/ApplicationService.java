package com.polymath.jobboard.services;

import com.polymath.jobboard.dto.requests.JobApplicationRequest;
import com.polymath.jobboard.dto.response.applications.ApplicationResponse;
import com.polymath.jobboard.dto.response.applications.JobSeekerApplication;
import com.polymath.jobboard.models.enums.ApplicationStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ApplicationService {
    void applyForJob(Long jobId,JobApplicationRequest request,String email);
    List<JobSeekerApplication> getAllMyApplicationsForJobSeeker(String email);
    List<ApplicationResponse> getAllApplicationsForJobIdByEmployee(String email, Long jobId);
    List<ApplicationResponse> getAllApplicationsByEmployee(String email);
    void updateApplicationStatus(Long applicationId,Long jobSeekerId,String employerEmail, ApplicationStatus status);
}
