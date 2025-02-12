package com.polymath.jobboard.services.impl;

import com.polymath.jobboard.dto.requests.AdvancedFilterRequest;
import com.polymath.jobboard.dto.requests.JobsRequest;
import com.polymath.jobboard.dto.response.JobsResponse;
import com.polymath.jobboard.exceptions.CustomBadRequest;
import com.polymath.jobboard.exceptions.CustomNotFound;
import com.polymath.jobboard.exceptions.UserDoesNotExists;
import com.polymath.jobboard.models.Employers;

import com.polymath.jobboard.models.Jobs;
import com.polymath.jobboard.models.Users;
import com.polymath.jobboard.models.enums.JobStatus;
import com.polymath.jobboard.models.enums.UserRole;
import com.polymath.jobboard.repositories.EmployersRepository;
import com.polymath.jobboard.repositories.JobSeekersRepository;
import com.polymath.jobboard.repositories.JobsRepositories;
import com.polymath.jobboard.repositories.UsersRepositories;
import com.polymath.jobboard.repositories.specifications.JobSpecification;
import com.polymath.jobboard.services.JobsService;

import com.polymath.jobboard.utils.RoleUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class JobsServiceImpl implements JobsService {
    private final JobsRepositories jobsRepositories;
    private final UsersRepositories usersRepositories;
    private final RoleUtils roleUtils;
    private final EmployersRepository employersRepository;

    public JobsServiceImpl(JobsRepositories jobsRepositories, UsersRepositories usersRepositories, RoleUtils roleUtils, EmployersRepository employersRepository, JobSeekersRepository jobSeekersRepository) {
        this.jobsRepositories = jobsRepositories;
        this.usersRepositories = usersRepositories;
        this.roleUtils = roleUtils;
        this.employersRepository = employersRepository;
    }

    @Override
    @CacheEvict(value = {"allJobs","advancedSearch","filterJobs","searchJobsByTitle"},allEntries = true)
    public void postJob(JobsRequest jobsRequest, String employerEmail) {
        if(jobsRequest.title()==null|| jobsRequest.title().isEmpty() || jobsRequest.description()==null || jobsRequest.description().isEmpty()) {
            throw new CustomBadRequest("Title and description are required");
        }
        if(jobsRequest.expiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomBadRequest("You cannot set an expiration time in the  past");
        }
        Users users = usersRepositories.findByEmail(employerEmail).orElseThrow(()->new UserDoesNotExists(String.format("User with email %s not found", employerEmail)));
        Employers employers = employersRepository.findByUser(users).orElseThrow(()->new UserDoesNotExists("Update your employer's data"));
        roleUtils.validateSingleRole(users.getRole());
        JobStatus jobStatus;
        jobStatus=JobStatus.ACTIVE;
        if(jobsRequest.expiresAt().isBefore(LocalDateTime.now())) {
            jobStatus = JobStatus.EXPIRED;
        }
       // if(jobsRequest.s)
        Jobs jobs = new Jobs();
        jobs.setTitle(jobsRequest.title());
        jobs.setDescription(jobsRequest.description());
        jobs.setCategory(jobsRequest.category());
        jobs.setSalary(jobsRequest.salary());
        jobs.setEmployers(employers);
        jobs.setStatus(jobStatus);
        jobs.setExpiresAt(jobsRequest.expiresAt());
        jobs.setPostedAt(LocalDateTime.now());
        jobs.setLocation(jobsRequest.location());
       jobsRepositories.save(jobs);
    }

    @Override
    @CacheEvict(value = {"allJobs","advancedSearch","filterJobs","searchJobsByTitle","jobById"},key = "#jobId")
    public void updateJob(JobsRequest jobRequest,Long jobId,String employerEmail) {
        if(jobRequest.title()==null || jobRequest.title().isEmpty() || jobRequest.description()==null || jobRequest.description().isEmpty()) {
            throw new CustomBadRequest("Title and description are required");
        }
        if(jobRequest.expiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomBadRequest("You cannot set an expiration time in the  past");
        }
        Jobs existingJobs = jobsRepositories.findById(jobId).orElseThrow(()->new CustomNotFound("This job does not exist"));
        existingJobs.setTitle(jobRequest.title());
        existingJobs.setDescription(jobRequest.description());
        existingJobs.setEmployers(existingJobs.getEmployers());
        existingJobs.setStatus(jobRequest.status());
        existingJobs.setExpiresAt(jobRequest.expiresAt());
        existingJobs.setLocation(jobRequest.location());
        existingJobs.setCategory(jobRequest.category());
        existingJobs.setSalary(jobRequest.salary());
        existingJobs.setPostedAt(existingJobs.getPostedAt());
        jobsRepositories.save(existingJobs);
    }

    @Override
    @CacheEvict(value = {"allJobs","advancedSearch","filterJobs","searchJobsByTitle","jobById"},key = "#jobId")
    public void deleteJob(Long jobId,String employerEmail) {
        Users users = usersRepositories.findByEmail(employerEmail).orElseThrow(()->new UserDoesNotExists("This user does not exist"));
        Employers employers = employersRepository.findByUser(users).orElseThrow(()->new UserDoesNotExists("This employer does not exist"));

        roleUtils.validateAnyRoles(UserRole.ADMIN,employers.getUser().getRole());
        Jobs jobs = jobsRepositories.findById(jobId).orElseThrow(()->new CustomNotFound("This job does not exist"));
        jobsRepositories.deleteById(jobs.getId());
        //jobsRepositories.findAndDeleteById(jobId).orElseThrow(()->new CustomNotFound("This job does not exist"));
    }

    @Override
    @Cacheable(value = "allJobs",key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<JobsResponse> getAllJobs(Pageable pageable) {
        Page<Jobs> allJobs= jobsRepositories.findAll(pageable);
        return getJobsResponses(pageable, allJobs);

    }

//    Implementing this later
//    @Override
//    @Cacheable(value = "authorizedJobs",key = "#email + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
//    public Page<JobsResponse> getAllJobsForAuthorizedUsers(Pageable pageable, String email) {
//        Optional<JobSeekers> jobSeekers = jobSeekersRepository.findByJobSeekerEmail(email);
//        if (jobSeekers.isEmpty()){
//          return   getAllJobs(pageable);
//        }
//        String query = GenerateTsQuery.generateTsQuery(jobSeekers.get());
//        Page<Jobs> allJobs = jobsRepositories.findSimilarJobs(query, pageable);
//        return getJobsResponses(pageable, allJobs);
//    }

    @Override
    @Cacheable(value = "jobById",key = "#jobId")
    public JobsResponse getJobById(Long jobId) {
        Jobs job = jobsRepositories.findById(jobId).orElseThrow(()->new CustomNotFound("There is no job with this id"));
        return new JobsResponse(jobId,job.getEmployers().getCompanyName(),job.getEmployers().getCompanyDescription(),job.getEmployers().getWebsiteUrl(),job.getTitle(),job.getDescription(),job.getLocation(),job.getCategory(),job.getSalary(),job.getPostedAt(),job.getExpiresAt(),job.getStatus());
    }

    @Override
    @Cacheable(value = "advancedSearch",key = "#search + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<JobsResponse> advanceSearch(String search, Pageable pageable) {
        Page<Jobs> response = jobsRepositories.advanceJobsSearch(search,pageable);
        return getJobsResponses(pageable, response);
    }

    @Override
    @Cacheable(value = "filterJobs",key = "#title + '_' + #description + '_' + #companyName + '_' + #location + '_' + #category + '_' + #minSalary + '_' + #maxSalary + '_' + #startsAt + '_' + #endsAt + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<JobsResponse> filterJobs(String title,String description,String companyName,String location,String category,Double minSalary,Double maxSalary, LocalDateTime startsAt, LocalDateTime endsAt,Pageable pageable) {
        boolean hasValidSearchCriteria = Stream.of(
                title, location, description, companyName, category
        ).anyMatch(str -> str != null && !str.trim().isEmpty()) ||
                (minSalary != null && minSalary >= 0) ||
                (maxSalary != null  && maxSalary >= 0) ||
                startsAt != null ||
                endsAt != null;
        if (!hasValidSearchCriteria) {
            throw new CustomBadRequest("At least one valid search parameter is required (job title, description, company name, category, salary range, or date range)");
        }
        AdvancedFilterRequest filter = new AdvancedFilterRequest(title, description, companyName, location, category, minSalary, maxSalary, startsAt, endsAt);
        Specification<Jobs> spec = JobSpecification.advancedFilter(filter);
        Page<Jobs> jobs = jobsRepositories.findAll(spec,pageable);
        return getJobsResponses(pageable, jobs);
    }

    @Override
    @Cacheable(value = "searchJobsByTitle",key = "#title + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<JobsResponse> searchJobsByTitle(String title,Pageable pageable) {
        if(title==null || title.isEmpty()) {
            throw new CustomBadRequest("Title is required");
        }
        Page<Jobs> jobs = jobsRepositories.findAllByTitleContainingIgnoreCase(title,pageable);
        return getJobsResponses(pageable, jobs);
    }

    @Scheduled(fixedRate = 3600000)
    @CacheEvict(value = {"allJobs","advancedSearch","filterJobs","searchJobsByTitle","jobById"},allEntries = true)
    public void updateExpiredJobs(){
        LocalDateTime now = LocalDateTime.now();
        List<Jobs> expiredJobs = jobsRepositories.findExpiredJobs(now);
        for(Jobs job:expiredJobs){
            job.updateJobStatus();
            jobsRepositories.save(job);

        }
    }


    private Page<JobsResponse> getJobsResponses(Pageable pageable, Page<Jobs> allJobs) {
        List<JobsResponse> responses = allJobs.getContent().stream().map(j->new JobsResponse(j.getId(),j.getEmployers().getCompanyName(),j.getEmployers().getCompanyDescription(),j.getEmployers().getWebsiteUrl(),j.getTitle(),j.getDescription(),j.getLocation(),j.getCategory(),j.getSalary(),j.getPostedAt(),j.getExpiresAt(),j.getStatus())).toList();
        return new PageImpl<>(responses,pageable,allJobs.getTotalElements());
    }

}
