package com.polymath.jobboard.dto.response;

public record ErrorValidationResponse(int status,Object error ,Long timestamp) {
}
