package com.polymath.jobboard.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.polymath.jobboard.models.enums.JobStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
//@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public record JobsResponse(Long id, String companyName, String companyDescription,
                           String websiteUrl,String title, String description, String location,
                           String category, Double salary,
                           LocalDateTime postedAt, LocalDateTime expiresAt, JobStatus status)  {
}
