package com.polymath.jobboard.dto.response.applications;

import com.polymath.jobboard.models.enums.JobStatus;

import java.time.LocalDateTime;

public record JobsData(String companyName,String websiteUrl, String roleTitle, JobStatus jobStatus, LocalDateTime startDate, LocalDateTime endDate,Double salary,String category) {
}
