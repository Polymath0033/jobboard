package com.polymath.jobboard.services.impl;

import com.polymath.jobboard.dto.requests.JobApplicationRequest;
import com.polymath.jobboard.dto.response.JobsResponse;
import com.polymath.jobboard.dto.response.applications.ApplicationResponse;
import com.polymath.jobboard.dto.response.applications.JobSeeker;
import com.polymath.jobboard.dto.response.applications.JobSeekerApplication;
import com.polymath.jobboard.dto.response.applications.JobsData;
import com.polymath.jobboard.exceptions.CustomBadRequest;
import com.polymath.jobboard.exceptions.CustomNotFound;
import com.polymath.jobboard.exceptions.UserDoesNotExists;
import com.polymath.jobboard.models.Applications;
import com.polymath.jobboard.models.JobSeekers;
import com.polymath.jobboard.models.Jobs;
import com.polymath.jobboard.models.enums.ApplicationStatus;
import com.polymath.jobboard.models.enums.UserRole;
import com.polymath.jobboard.repositories.ApplicationsRepository;
import com.polymath.jobboard.repositories.JobSeekersRepository;
import com.polymath.jobboard.repositories.JobsRepositories;

import com.polymath.jobboard.services.ApplicationService;
import com.polymath.jobboard.services.CloudinaryUploadService;
import com.polymath.jobboard.utils.RoleUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationsRepository applicationsRepository;
    private final JobsRepositories jobsRepository;
    private final JobSeekersRepository jobSeekersRepository;
    private final RoleUtils roleUtils;
    private final CloudinaryUploadService cloudinaryUploadService;

    public ApplicationServiceImpl(ApplicationsRepository applicationsRepository, JobsRepositories jobsRepository, JobSeekersRepository jobSeekersRepository, RoleUtils roleUtils, CloudinaryUploadService cloudinaryUploadService) {
        this.applicationsRepository = applicationsRepository;
        this.jobsRepository = jobsRepository;
        this.jobSeekersRepository = jobSeekersRepository;
        this.roleUtils = roleUtils;
        this.cloudinaryUploadService = cloudinaryUploadService;
    }

    @Override
    public void applyForJob(Long jobId,JobApplicationRequest request,String email) {
        if(jobId==null) {
            throw new CustomBadRequest("Bad request");
        }
        roleUtils.validateSingleRole(UserRole.JOB_SEEKER);
        Jobs jobs = jobsRepository.findById(jobId).orElseThrow(() -> new CustomBadRequest("Job not found"));
        JobSeekers jobSeekers = jobSeekersRepository.findByJobSeekerEmail(email).orElseThrow(() -> new CustomBadRequest("Either you are not authenticated or you need to update your profile"));
        if(applicationsRepository.existsByJobsAndJobSeekers(jobs,jobSeekers)){
            throw new CustomBadRequest("You can't apply for a job twice is already a job seeker");
        }

        String resumeUrl;
        if(request.resumeUrl()==null||request.resumeUrl().isEmpty()) {
            if(jobSeekers.getResumeUrl()!=null) {
                resumeUrl = jobSeekers.getResumeUrl();
            }else {
                throw new CustomBadRequest("There must be a resume");
            }
        }else {
          resumeUrl =  cloudinaryUploadService.uploadFile(request.resumeUrl(), "application");
        }
        Applications applications = new Applications();
        applications.setJobs(jobs);
        applications.setJobSeekers(jobSeekers);
        applications.setAppliedAt(LocalDateTime.now());
        applications.setResumeUrl(resumeUrl);
        applications.setCoverLetter(request.coverLetter());
        applications.setStatus(ApplicationStatus.PENDING);
        applicationsRepository.save(applications);
    }

    @Override
    public List<JobSeekerApplication> getAllMyApplicationsForJobSeeker(String email) {
        if(email==null) {
            throw new CustomBadRequest("Bad request");
        }
       JobSeekers jobSeekers = jobSeekersRepository.findByJobSeekerEmail(email).orElseThrow(()->new UserDoesNotExists("Either you are not authenticated or you need to update your profile"));
        List<Applications> myApplications = applicationsRepository.findAllByJobSeekers(jobSeekers);
        return myApplications.stream().map(applications -> new JobSeekerApplication(applications.getResumeUrl(),applications.getCoverLetter(),new JobsResponse(applications.getJobs().getId(),applications.getJobs().getEmployers().getCompanyName(),applications.getJobs().getEmployers().getCompanyDescription(),applications.getJobs().getEmployers().getWebsiteUrl(),applications.getJobs().getTitle(),applications.getJobs().getDescription(),applications.getJobs().getLocation(),applications.getJobs().getCategory(),applications.getJobs().getSalary(),applications.getJobs().getPostedAt(),applications.getJobs().getExpiresAt(),applications.getJobs().getStatus()))).toList();
    }

    @Override
    public List<ApplicationResponse> getAllApplicationsForJobIdByEmployee(String email,Long jobId) {
        if(email==null||jobId==null) {
            throw new CustomBadRequest("Bad request");
        }
        List<Applications> jobIdApplications=applicationsRepository.findAllApplicationByJobIdAndEmployerEmail(jobId,email);
        if (jobIdApplications.isEmpty()){
            throw new CustomBadRequest("No applications found for job "+jobId +" and company email "+email);
        }
        return getApplicationResponses(jobIdApplications);
    }

    @Override
    public List<ApplicationResponse> getAllApplicationsByEmployee(String email) {
        roleUtils.validateSingleRole(UserRole.EMPLOYER);
        if(email==null) {
            throw new CustomBadRequest("Bad request");
        }
        List<Applications> applications = applicationsRepository.findAllByJobsEmployerEmail(email);

        if(applications.isEmpty()){
            throw new CustomNotFound("No application found this company");
        }
        return getApplicationResponses(applications);

    }

    @Override
    public void updateApplicationStatus(Long applicationId,Long jobSeekerId,String employerEmail, ApplicationStatus status) {
        if (applicationId == null || employerEmail == null || jobSeekerId == null) {
            throw new CustomBadRequest("Bad request - required parameters missing");
        }
        try{
            switch (status) {
                case PENDING:
                case ACCEPTED:
                case REJECTED:
                case REVIEWED:
                    break;
                default:
                    throw new CustomBadRequest("Invalid application status: "+status);

            }
        } catch (IllegalArgumentException e) {
            throw new CustomBadRequest("Invalid application status: "+status);
        }
        Applications applications = applicationsRepository.findApplicationsByApplicationJobSeekerIdEmployerEmail(applicationId,jobSeekerId,employerEmail).orElseThrow(()->new CustomNotFound("Application not found with id: "+applicationId+" for jobSeeker id: "+jobSeekerId+" and employer email: "+employerEmail));
        applications.setStatus(status);
        applicationsRepository.save(applications);
    }

    @Override
    public void deleteApplication(Long applicationId, Long jobId, String email) {
        if(applicationId == null || jobId == null || email == null) {
            throw new CustomBadRequest("Bad request - required parameters missing");
        }
        JobSeekers jobSeekers = jobSeekersRepository.findByJobSeekerEmail(email).orElseThrow(()->new UserDoesNotExists("Either you are not authenticated or you are yet to complete your profile before you can delete an application"));
      Applications applications = applicationsRepository.findApplicationsByApplicationJobIdJobSeekerId(applicationId,jobId, jobSeekers.getId()).orElseThrow(()->new CustomNotFound("Application not found with id: "+applicationId + " for job id: "+jobId+" and job seeker id: "+ jobSeekers.getId()));
        applicationsRepository.delete(applications);
    }

    private List<ApplicationResponse> getApplicationResponses(List<Applications> applications) {
        return     applications.stream().map(ap -> new ApplicationResponse(ap.getResumeUrl(),ap.getCoverLetter(),ap.getAppliedAt(),ap.getStatus(),new JobSeeker(ap.getJobSeekers().getUser().getEmail(),ap.getJobSeekers().getFirstName(),ap.getJobSeekers().getLastName(),ap.getJobSeekers().getSkills()),new JobsData(ap.getJobs().getEmployers().getCompanyName(),ap.getJobs().getEmployers().getWebsiteUrl(),ap.getJobs().getTitle(),ap.getJobs().getStatus(),ap.getJobs().getPostedAt(),ap.getJobs().getExpiresAt(),ap.getJobs().getSalary(),ap.getJobs().getCategory()))).toList();
    }

}
