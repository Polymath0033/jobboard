package com.polymath.jobboard.dto.response.applications;

import com.polymath.jobboard.dto.response.JobsResponse;

public record JobSeekerApplication(String resumeUrl, String coverLetter, JobsResponse jobsData) {
}
