package com.polymath.jobboard.services;

import com.polymath.jobboard.dto.requests.JobsRequest;
import com.polymath.jobboard.dto.response.AllUserResponse;
import com.polymath.jobboard.dto.response.JobsResponse;
import com.polymath.jobboard.dto.response.PaginationResponse;
import com.polymath.jobboard.models.Jobs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public interface JobsService {
    void postJob(JobsRequest jobsRequest, String employerEmail);
    void updateJob(JobsRequest jobsRequest,Long jobId,String employerEmail);
    void deleteJob(Long jobId,String employerEmail);
    Page<JobsResponse> getAllJobs(Pageable pageable);
    Page<JobsResponse> getAllJobsForAuthorizedUsers(Pageable pageable, String email);
    JobsResponse getJobById(Long jobId);
    Page<JobsResponse> advanceSearch(String search, Pageable pageable);
    Page<JobsResponse> filterJobs(String title,String description,String companyName,String location,String category,Double minSalary,Double maxSalary, LocalDateTime startsAt, LocalDateTime endsAt,Pageable pageable);
    Page<JobsResponse> searchJobsByTitle(String title,Pageable pageable);
    //void applyForJob(Jobs job);
}
