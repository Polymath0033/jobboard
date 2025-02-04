package com.polymath.jobboard.services;

import com.polymath.jobboard.dto.requests.EmployersDto;
import com.polymath.jobboard.dto.requests.JobSeekersDto;
import com.polymath.jobboard.dto.response.AllUserResponse;
import com.polymath.jobboard.dto.response.EmployersResponse;
import com.polymath.jobboard.dto.response.JobSeekersResponse;
import org.springframework.stereotype.Service;

@Service
public interface UserDataService {
    JobSeekersResponse addJobSeeker(JobSeekersDto jobSeeker);
    JobSeekersResponse updateJobSeeker(Long id,JobSeekersDto jobSeeker);
    void deleteJobSeeker(Long id);
    JobSeekersResponse getJobSeeker(String email);
    AllUserResponse getAllJobSeekers();
    EmployersResponse addNewEmployer(EmployersDto employer);
    EmployersResponse updateEmployer(Long id, EmployersDto employer);
    void deleteEmployer(Long id);
    EmployersResponse getEmployer(String email);
    AllUserResponse getAllEmployers();
    AllUserResponse getAllUsers();


}
