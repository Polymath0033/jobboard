package com.polymath.jobboard.services.impl;

import com.polymath.jobboard.dto.response.JobsResponse;
import com.polymath.jobboard.dto.response.SavedJobsResponse;
import com.polymath.jobboard.exceptions.CustomBadRequest;
import com.polymath.jobboard.exceptions.CustomNotFound;
import com.polymath.jobboard.models.JobSeekers;
import com.polymath.jobboard.models.Jobs;
import com.polymath.jobboard.models.SavedJobs;
import com.polymath.jobboard.repositories.JobSeekersRepository;

import com.polymath.jobboard.repositories.JobsRepositories;
import com.polymath.jobboard.repositories.SavedJobsRepository;
import com.polymath.jobboard.services.SavedJobsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SavedJobsServiceImpl implements SavedJobsService {

    private final SavedJobsRepository savedJobsRepository;
    private final JobsRepositories jobsRepository;
    private final JobSeekersRepository jobSeekersRepository;

    public SavedJobsServiceImpl(SavedJobsRepository savedJobsRepository, JobsRepositories jobsRepository, JobSeekersRepository jobSeekersRepository) {
        this.savedJobsRepository = savedJobsRepository;
        this.jobsRepository = jobsRepository;
        this.jobSeekersRepository = jobSeekersRepository;
    }

    @Override
    public void saveJob(Long jobId,String jobSeekerEmail) {
        if(jobId==null || jobSeekerEmail==null){
            throw new CustomBadRequest("Job id or seekerEmail is null");
        }
        Jobs jobs = jobsRepository.findById(jobId).orElseThrow(()->new CustomNotFound("Job not found"));
        JobSeekers jobSeekers = jobSeekersRepository.findByJobSeekerEmail(jobSeekerEmail).orElseThrow(()->new CustomNotFound("Complete your job seeker details"));
        if(savedJobsRepository.existsByJobsAndJobSeekers(jobs,jobSeekers)){
            throw new CustomBadRequest("You cannot save a job that already exists");
        }
        SavedJobs savedJobs = new SavedJobs();
        savedJobs.setJobs(jobs);
        savedJobs.setJobSeekers(jobSeekers);
        savedJobs.setSavedAt(LocalDateTime.now());
        savedJobsRepository.save(savedJobs);

    }

    @Override
    public void deleteJob(Long savedJobId, String jobSeekerEmail) {
        if(savedJobId==null || jobSeekerEmail==null){
            throw new CustomBadRequest("Job id or seekerEmail is null");
        }
        JobSeekers jobSeekers = jobSeekersRepository.findByJobSeekerEmail(jobSeekerEmail).orElseThrow(()->new CustomNotFound("Complete your job seeker details"));
        savedJobsRepository.findById(savedJobId).orElseThrow(()->new CustomNotFound("Job not found"));
        SavedJobs savedJobs = savedJobsRepository.findByIdAndJobSeekers(savedJobId,jobSeekers).orElseThrow(()->new CustomNotFound("Either this Job not saved or you can't delete it if you are the not the one who saved it"));
        savedJobsRepository.delete(savedJobs);
    }

    @Override
    public SavedJobsResponse getSavedJobsById(Long savedJobId, String jobSeekerEmail) {
     if(savedJobId==null || jobSeekerEmail==null){
         throw new CustomBadRequest("Saved id or seekerEmail is null");
     }
     JobSeekers jobSeekers = jobSeekersRepository.findByJobSeekerEmail(jobSeekerEmail).orElseThrow(()->new CustomNotFound("Complete your job seeker details"));
     SavedJobs s = savedJobsRepository.findSavedJobsByIdAndJobSeekers(savedJobId,jobSeekers).orElseThrow(()->new CustomNotFound("Job not found"));
     return getSavedJobsResponse(s);
    }

    @Override
    public List<SavedJobsResponse> getAllSavedJobsByJobSeeker(String jobSeekerEmail) {
        if(jobSeekerEmail==null){
            throw new CustomBadRequest("Job seekerEmail is null");
        }
        JobSeekers jobSeekers = jobSeekersRepository.findByJobSeekerEmail(jobSeekerEmail).orElseThrow(()->new CustomNotFound("Complete your job seeker details"));
        List<SavedJobs> savedJobs = savedJobsRepository.findAllByJobSeekers(jobSeekers);
        return savedJobs.stream().map(this::getSavedJobsResponse).toList();
    }

    private SavedJobsResponse getSavedJobsResponse(SavedJobs s){
        return new SavedJobsResponse(s.getId(),s.getSavedAt(),new JobsResponse(s.getJobs().getId(),s.getJobs().getEmployers().getCompanyName(),s.getJobs().getEmployers().getCompanyDescription(),s.getJobs().getEmployers().getWebsiteUrl(),s.getJobs().getTitle(),s.getJobs().getDescription(),s.getJobs().getLocation(),s.getJobs().getCategory(),s.getJobs().getSalary(),s.getJobs().getPostedAt(),s.getJobs().getExpiresAt(),s.getJobs().getStatus()));
    }
}
