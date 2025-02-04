package com.polymath.jobboard.dto.response;

public record JobSeekersResponse(Long id,String email,String firstName,String lastName,String resumeUrl,String skills,String experiences) {
}
