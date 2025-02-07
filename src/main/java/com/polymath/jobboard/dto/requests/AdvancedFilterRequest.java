package com.polymath.jobboard.dto.requests;

import java.time.LocalDateTime;

public record AdvancedFilterRequest(String title,String description,String companyName,String location,String category,Double minSalary,Double maxSalary, LocalDateTime startsAt, LocalDateTime endsAt) {
}
