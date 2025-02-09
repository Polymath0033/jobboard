package com.polymath.jobboard.services;

import com.polymath.jobboard.dto.requests.EmployersDto;
import com.polymath.jobboard.dto.requests.JobSeekersDto;
import com.polymath.jobboard.dto.response.AllUserResponse;
import com.polymath.jobboard.dto.response.EmployersResponse;
import com.polymath.jobboard.dto.response.JobSeekersResponse;
import com.polymath.jobboard.models.enums.UserRole;
import jakarta.mail.Multipart;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface UserDataService {

//    JobSeeker service
    JobSeekersResponse addJobSeeker(JobSeekersDto jobSeeker, MultipartFile file);

    JobSeekersResponse updateJobSeeker(Long id,JobSeekersDto jobSeeker,MultipartFile file);

    JobSeekersResponse getJobSeeker(String email);


    //  Employer service
    EmployersResponse addNewEmployer(EmployersDto employer);

    EmployersResponse updateEmployer(Long id, EmployersDto employer);

    EmployersResponse getEmployer(String email);


//    Admin Service
    AllUserResponse getAllJobSeekersData();

    AllUserResponse getAllEmployersData();

    AllUserResponse getAllUsersData();

    AllUserResponse getAllUsers();

    void deleteJobSeeker(Long id);

    void deleteEmployer(Long id);


}
