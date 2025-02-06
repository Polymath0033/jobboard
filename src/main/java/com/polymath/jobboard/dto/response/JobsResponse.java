package com.polymath.jobboard.dto.response;

import com.polymath.jobboard.models.enums.JobStatus;

import java.time.LocalDateTime;

public record JobsResponse(Long id, String companyName, String companyDescription,
                           String websiteUrl,String title, String description, String location,
                           String category, Double salary,
                           LocalDateTime postedAt, LocalDateTime expiresAt, JobStatus status) {
}
