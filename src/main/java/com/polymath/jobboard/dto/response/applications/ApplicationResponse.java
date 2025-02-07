package com.polymath.jobboard.dto.response.applications;


import com.polymath.jobboard.models.enums.ApplicationStatus;

import java.time.LocalDateTime;

public record ApplicationResponse(String resumeUrl, String coverLetter, LocalDateTime appliedAt, ApplicationStatus applicationStatus,
                                  JobSeeker jobSeeker, JobsData jobsData) {
}
