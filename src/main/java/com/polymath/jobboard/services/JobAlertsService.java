package com.polymath.jobboard.services;

import com.polymath.jobboard.dto.requests.JobAlertsRequest;
import com.polymath.jobboard.models.JobAlerts;
import org.springframework.stereotype.Service;

@Service
public interface JobAlertsService {
    void setJobAlerts(JobAlertsRequest jobAlerts,String email);
    void deleteJobAlerts(Long jobAlertId,String email);
}
