package com.polymath.jobboard.services;


import com.polymath.jobboard.dto.response.SavedJobsResponse;
import com.polymath.jobboard.models.Jobs;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SavedJobsService {
    void saveJob(Long jobsId,String jobSeekerEmail);
    void deleteJob(Long savedJobId,String jobSeekerEmail);
    SavedJobsResponse getSavedJobsById(Long savedJobId, String jobSeekerEmail);
    List<SavedJobsResponse> getAllSavedJobsByJobSeeker(String jobSeekerEmail);
}
