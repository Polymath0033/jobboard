package com.polymath.jobboard.services;

import com.polymath.jobboard.dto.requests.JobSeekersDto;
import com.polymath.jobboard.models.JobSeekers;
import org.springframework.stereotype.Service;

@Service
public interface JobSeekerService {
    JobSeekers addJobSeeker(JobSeekersDto jobSeeker);
    void updateJobSeeker(JobSeekersDto jobSeeker);
    void deleteJobSeeker(JobSeekersDto jobSeeker);

}
