package com.polymath.jobboard.dto.requests;

import com.polymath.jobboard.models.enums.AlertFrequency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JobAlertsRequest( @NotNull @NotBlank(message = "Job title must be set") String searchedQuery, AlertFrequency frequency) {
}
