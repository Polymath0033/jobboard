package com.polymath.jobboard.services.impl;

import com.polymath.jobboard.dto.requests.JobAlertsRequest;
import com.polymath.jobboard.exceptions.CustomBadRequest;
import com.polymath.jobboard.exceptions.CustomNotFound;
import com.polymath.jobboard.models.JobAlerts;
import com.polymath.jobboard.models.JobSeekers;
import com.polymath.jobboard.models.Jobs;
import com.polymath.jobboard.models.enums.AlertFrequency;
import com.polymath.jobboard.repositories.JobAlertsRepository;
import com.polymath.jobboard.repositories.JobSeekersRepository;
import com.polymath.jobboard.repositories.JobsRepositories;

import com.polymath.jobboard.services.EmailService;
import com.polymath.jobboard.services.JobAlertsService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobAlertsServiceImpl implements JobAlertsService {
    private final JobAlertsRepository jobAlertsRepository;
    private final JobSeekersRepository jobSeekersRepository;
    private final JobsRepositories jobsRepositories;
    private final EmailService emailService;

    public JobAlertsServiceImpl(JobAlertsRepository jobAlertsRepository, JobSeekersRepository jobSeekersRepository, JobsRepositories jobsRepositories, EmailService emailService) {
        this.jobAlertsRepository = jobAlertsRepository;
        this.jobSeekersRepository = jobSeekersRepository;
        this.jobsRepositories = jobsRepositories;
        this.emailService = emailService;
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

    @Scheduled(cron = "0 0 8 * * ?")
//    @Scheduled(fixedDelay = 600)
    public void sendDailyJobAlerts() {
        List<JobAlerts> jobAlerts = jobAlertsRepository.findAllByFrequency(AlertFrequency.DAILY);
        sendJobAlerts(jobAlerts,"Daily Job Alerts");

    }

    @Scheduled(cron = "0 0 8 * * MON")
    public void sendWeeklyJobAlerts() {
        List<JobAlerts> jobAlerts = jobAlertsRepository.findAllByFrequency(AlertFrequency.WEEKLY);
        sendJobAlerts(jobAlerts,"Weekly Job Alerts");
    }
    private void sendJobAlerts(List<JobAlerts> jobAlerts,String subject) {
        for (JobAlerts jobAlert : jobAlerts) {
            List<Jobs> availableJobs = jobsRepositories.searchJobs(jobAlert.getSearchedQuery());
            if(!availableJobs.isEmpty()){
                emailService.sendJobAlertsEmail(jobAlert.getJobSeeker().getUser().getEmail(),subject,jobAlert.getJobSeeker().getFirstName(),availableJobs,jobAlert.getSearchedQuery(),"http://localhost:8081/api/v1/unsubscribe/"+jobAlert.getId()+"/"+jobAlert.getJobSeeker().getUser().getEmail());
            }
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
