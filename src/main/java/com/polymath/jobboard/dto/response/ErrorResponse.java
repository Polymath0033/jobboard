package com.polymath.jobboard.dto.response;

public record ErrorResponse(int status,String message, long timestamp) {
}
