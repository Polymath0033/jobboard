package com.polymath.jobboard.services.impl;

import com.polymath.jobboard.dto.requests.EmployersDto;
import com.polymath.jobboard.dto.requests.JobSeekersDto;
import com.polymath.jobboard.dto.response.*;
import com.polymath.jobboard.exceptions.CustomBadRequest;
import com.polymath.jobboard.exceptions.CustomNotFound;
import com.polymath.jobboard.exceptions.UserDoesNotExists;
import com.polymath.jobboard.models.Employers;
import com.polymath.jobboard.models.JobSeekers;
import com.polymath.jobboard.models.Users;
import com.polymath.jobboard.models.enums.UserRole;
import com.polymath.jobboard.repositories.EmployersRepository;
import com.polymath.jobboard.repositories.JobSeekersRepository;
import com.polymath.jobboard.repositories.UsersRepositories;
import com.polymath.jobboard.services.UserDataService;
import com.polymath.jobboard.utils.RoleUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDataServiceImpl implements UserDataService {
    private final JobSeekersRepository jobSeekersRepository;
    private final UsersRepositories usersRepositories;
    private final EmployersRepository employersRepository;
    private final RoleUtils roleUtils;


    public UserDataServiceImpl(JobSeekersRepository jobSeekersRepository, UsersRepositories usersRepositories, EmployersRepository employersRepository, RoleUtils roleUtils) {
        this.jobSeekersRepository = jobSeekersRepository;
        this.usersRepositories = usersRepositories;
        this.employersRepository = employersRepository;
        this.roleUtils = roleUtils;
    }

    @Override
    public JobSeekersResponse addJobSeeker(JobSeekersDto request) {
        roleUtils.validateSingleRole(UserRole.JOB_SEEKER);
        try {
            if(request.email()==null||request.firstName()==null||request.lastName()==null){
                throw new CustomBadRequest("Enter appropriate data");
            }
            Users user= usersRepositories.findByEmail(request.email()).orElseThrow(() -> new UserDoesNotExists("User is not authenticated yet"));
            UserRole role = user.getRole();
            if(role!=UserRole.JOB_SEEKER){
                throw new UserDoesNotExists("You need to register as a job seeker in order to access this");
            }
            JobSeekers jobSeekers = new JobSeekers();
            jobSeekers.setFirstName(request.firstName());
            jobSeekers.setLastName(request.lastName());
            jobSeekers.setUser(user);
            jobSeekers.setResumeUrl(request.resumeUrl());
            jobSeekers.setSkills(request.skills());
            jobSeekers.setExperiences(request.experiences());
            jobSeekersRepository.save(jobSeekers);
            return new JobSeekersResponse(jobSeekers.getId(),user.getEmail(), request.firstName(),request.lastName(),request.resumeUrl(),request.skills(),request.experiences());
        } catch (CustomBadRequest | UserDoesNotExists e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public JobSeekersResponse updateJobSeeker(Long id, JobSeekersDto request) {
        roleUtils.validateSingleRole(UserRole.JOB_SEEKER);
        if(id==null||request.email()==null||request.firstName()==null||request.lastName()==null){
            throw new CustomBadRequest("Enter appropriate data");
        }
        JobSeekers existingJobSeekers = jobSeekersRepository.findById(id).orElseThrow(()->new CustomNotFound("JobSeeker with this is id: " + id + " does not exist"));
        existingJobSeekers.setFirstName(request.firstName());
        existingJobSeekers.setLastName(request.lastName());
        existingJobSeekers.setSkills(request.skills());
        existingJobSeekers.setExperiences(request.experiences());
        existingJobSeekers.setResumeUrl(request.resumeUrl());
        jobSeekersRepository.save(existingJobSeekers);
        return new JobSeekersResponse(existingJobSeekers.getId(), existingJobSeekers.getUser().getEmail(),request.firstName(),request.lastName(),request.resumeUrl(),request.skills(),request.experiences());
    }

    @Override
    public void deleteJobSeeker(Long id) {
        roleUtils.validateSingleRole(UserRole.ADMIN);
        if(id==null||jobSeekersRepository.existsById(id)){
            throw new CustomBadRequest("JobSeeker with this is id: " + id + " does not exist");
        }
        jobSeekersRepository.deleteById(id);



    }

    @Override
    public JobSeekersResponse getJobSeeker(String email) {
        roleUtils.validateAnyRoles(UserRole.JOB_SEEKER,UserRole.ADMIN);
        if(email==null){
            throw new CustomBadRequest("Enter appropriate token");
        }
        Users user = usersRepositories.findByEmail(email).orElseThrow(() -> new UserDoesNotExists("User is not authenticated yet"));
        JobSeekers jobSeekers = jobSeekersRepository.findByUser(user).orElseThrow(() -> new UserDoesNotExists("No user data found, add your data first"));
        return new JobSeekersResponse(jobSeekers.getId(),user.getEmail(),jobSeekers.getFirstName(),jobSeekers.getLastName(),jobSeekers.getResumeUrl(),jobSeekers.getSkills(),jobSeekers.getExperiences());
    }

    @Override
    public AllUserResponse getAllJobSeekersData() {
        roleUtils.validateSingleRole(UserRole.ADMIN);
        List<JobSeekers> allJobSeekers = jobSeekersRepository.findAll();
        List<JobSeekerDto> jobSeekerDtoList = new ArrayList<>();
        allJobSeekers.forEach(js->jobSeekerDtoList.add(new JobSeekerDto(js.getId(),js.getUser().getEmail(),js.getUser().getRole(),js.getFirstName(),js.getLastName(),js.getResumeUrl(),js.getSkills(),js.getExperiences())));
        return new AllUserResponse(jobSeekerDtoList);
    }


    @Override
    public EmployersResponse addNewEmployer(EmployersDto newEmployer) {
        roleUtils.validateSingleRole(UserRole.EMPLOYER);
        if (newEmployer.email()==null||newEmployer.companyName()==null){
            throw new CustomBadRequest("Enter appropriate data");
        }
        Users users = usersRepositories.findByEmail(newEmployer.email()).orElseThrow(()->new UserDoesNotExists("This user is not authenticated yet"));
        Employers employers = new Employers();
        employers.setCompanyName(newEmployer.companyName());
        employers.setUser(users);
        employers.setCompanyDescription(newEmployer.companyDescription());
        employers.setLogoUrl(newEmployer.companyLogo());
        employers.setWebsiteUrl(newEmployer.websiteUrl());
        employersRepository.save(employers);
        return new EmployersResponse(employers.getId(),users.getEmail(), employers.getCompanyName(),employers.getCompanyDescription(),employers.getLogoUrl(),employers.getWebsiteUrl());
    }

    @Override
    public EmployersResponse updateEmployer(Long id, EmployersDto employer) {
        roleUtils.validateSingleRole(UserRole.EMPLOYER);
        if(employer.email()==null||employer.companyName()==null){
            throw new CustomBadRequest("Enter appropriate data");
        }
        Employers existingEmployer = employersRepository.findById(id).orElseThrow(()->new UserDoesNotExists("Employer with this is id: " + id + " does not exist"));
        existingEmployer.setCompanyName(employer.companyName());
        existingEmployer.setCompanyDescription(employer.companyDescription());
        existingEmployer.setLogoUrl(employer.companyLogo());
        existingEmployer.setWebsiteUrl(employer.websiteUrl());
        employersRepository.save(existingEmployer);
        return new EmployersResponse(existingEmployer.getId(),existingEmployer.getUser().getEmail(),existingEmployer.getCompanyName(),existingEmployer.getCompanyDescription(),existingEmployer.getLogoUrl(),existingEmployer.getWebsiteUrl());
    }

    @Override
    public void deleteEmployer(Long id) {
        roleUtils.validateSingleRole(UserRole.ADMIN);
        if (id==null||jobSeekersRepository.existsById(id)){
            throw new CustomBadRequest("Employer with this is id: " + id + " does not exist");
        }
        employersRepository.deleteById(id);
    }
    @Override
    public EmployersResponse getEmployer(String email) {
        roleUtils.validateSingleRole(UserRole.EMPLOYER);
        if (email==null){
            throw new CustomBadRequest("Enter appropriate data");
        }
        Users user = usersRepositories.findByEmail(email).orElseThrow(()->new UserDoesNotExists("User is not authenticated yet"));
        Employers employer = employersRepository.findByUser(user).orElseThrow(()->new CustomNotFound("you don't have any employer data "));
        return new  EmployersResponse(employer.getId(),user.getEmail(),employer.getCompanyName(),employer.getCompanyDescription(),employer.getLogoUrl(),employer.getWebsiteUrl());
    }

    @Override
    public AllUserResponse getAllEmployersData() {
        roleUtils.validateSingleRole(UserRole.ADMIN);
        List<Employers> allEmployers = employersRepository.findAll();
        List<EmployerDto> employerDtoList = new ArrayList<>();
        allEmployers.forEach(employer -> employerDtoList.add(new EmployerDto(employer.getId(),employer.getUser().getEmail(),employer.getUser().getRole(),employer.getCompanyName(),employer.getCompanyDescription(),employer.getLogoUrl(),employer.getWebsiteUrl())));
        return new AllUserResponse(employerDtoList);
    }

    @Override
    public AllUserResponse getAllUsersData() {
        roleUtils.validateSingleRole(UserRole.ADMIN);
        List<Employers> allEmployers = employersRepository.findAll();
        List<JobSeekers> allJobSeekers = jobSeekersRepository.findAll();
        System.out.println(allEmployers);
        System.out.println(allJobSeekers);
        List<Object> combinedUsers = new ArrayList<>();
        allEmployers.forEach(employers -> combinedUsers.add(new EmployerDto(employers.getId(),employers.getUser().getEmail(),employers.getUser().getRole(),employers.getCompanyName(),employers.getCompanyDescription(),employers.getLogoUrl(),employers.getWebsiteUrl())));
        allJobSeekers.forEach(jobSeekers -> combinedUsers.add(new JobSeekerDto(jobSeekers.getId(), jobSeekers.getUser().getEmail(),jobSeekers.getUser().getRole(),jobSeekers.getFirstName(),jobSeekers.getLastName(),jobSeekers.getResumeUrl(),jobSeekers.getSkills(),jobSeekers.getExperiences())));
        return new AllUserResponse(combinedUsers);
    }

    public AllUserResponse getAllUsers(){
        roleUtils.validateSingleRole(UserRole.ADMIN);
        List<Users> users = usersRepositories.findAll();
        List<UsersDto> usersDtoList = new ArrayList<>();
        users.forEach(user->usersDtoList.add(new UsersDto(user.getId(),user.getEmail(),user.getRole(),user.getCreatedAt(),user.getUpdatedAt())));
        return new AllUserResponse(usersDtoList);
    }


}
