package com.polymath.jobboard.services;

import com.polymath.jobboard.dto.requests.JobsRequest;
import com.polymath.jobboard.models.Jobs;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface JobsService {
    void postJob(JobsRequest jobsRequest, String employerEmail);
    void updateJob(JobsRequest jobsRequest,Long jobId,String employerEmail);
    void deleteJob(Long jobId,String employerEmail);
    List<Jobs> getAllJobs();
    //void applyForJob(Jobs job);
}
