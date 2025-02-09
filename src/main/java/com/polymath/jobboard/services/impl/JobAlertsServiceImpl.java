package com.polymath.jobboard.services.impl;

import com.polymath.jobboard.dto.requests.JobAlertsRequest;
import com.polymath.jobboard.exceptions.CustomBadRequest;
import com.polymath.jobboard.exceptions.CustomNotFound;
import com.polymath.jobboard.models.JobAlerts;
import com.polymath.jobboard.models.JobSeekers;
import com.polymath.jobboard.models.enums.AlertFrequency;
import com.polymath.jobboard.repositories.JobAlertsRepository;
import com.polymath.jobboard.repositories.JobSeekersRepository;
import com.polymath.jobboard.services.JobAlertsService;
import org.springframework.stereotype.Service;

@Service
public class JobAlertsServiceImpl implements JobAlertsService {
    private final JobAlertsRepository jobAlertsRepository;
    private final JobSeekersRepository jobSeekersRepository;

    public JobAlertsServiceImpl(JobAlertsRepository jobAlertsRepository, JobSeekersRepository jobSeekersRepository) {
        this.jobAlertsRepository = jobAlertsRepository;
        this.jobSeekersRepository = jobSeekersRepository;
    }

    @Override
    public void setJobAlerts(JobAlertsRequest jobAlerts,String email) {
        if (jobAlerts.searchedQuery()==null||jobAlerts.searchedQuery().isEmpty()){
            throw new CustomBadRequest("Searched query is empty");
        }

        JobSeekers jobSeekers = jobSeekersRepository.findByJobSeekerEmail(email).orElseThrow(()->new CustomNotFound("Could not find JobSeeker, please complete your job seeker details"));
        AlertFrequency frequency;
        if (jobAlerts.frequency()==null){
            frequency=AlertFrequency.DAILY;
        }else {
            frequency=jobAlerts.frequency();
        }
        if (jobAlertsRepository.existsByJobSeekerId(jobSeekers.getId())) {
            JobAlerts existingJobAlerts = jobAlertsRepository.findByJobSeekerId(jobSeekers.getId());
            existingJobAlerts.setFrequency(frequency);
            existingJobAlerts.setSearchedQuery(jobAlerts.searchedQuery());
            jobAlertsRepository.saveAndFlush(existingJobAlerts);
        }else{
            JobAlerts jobAlert = new JobAlerts();
            jobAlert.setSearchedQuery(jobAlerts.searchedQuery());
            jobAlert.setJobSeeker(jobSeekers);
            jobAlert.setFrequency(frequency);
            jobAlertsRepository.save(jobAlert);
        }
    }

    @Override
    public void deleteJobAlerts(Long jobAlertId,String email) {
        if (jobAlertId == null) {
            throw new CustomBadRequest("Job alert id is null");
        }
        if(email==null||email.isEmpty()){
            throw new CustomBadRequest("Email is empty");
        }
        JobAlerts jobAlerts = jobAlertsRepository.findById(jobAlertId).orElseThrow(()->new CustomNotFound("You've not set any job alerts"));
        jobAlertsRepository.delete(jobAlerts);


    }

}
