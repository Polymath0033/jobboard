package com.polymath.jobboard.dto.response;

import java.time.LocalDateTime;

public record SavedJobsResponse(Long savedJobId, LocalDateTime savedAt,JobsResponse jobsData) {
}
