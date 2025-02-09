package com.polymath.jobboard.exceptions;

public class CustomNotAuthorized extends RuntimeException {
    public CustomNotAuthorized(String message) {
        super(message);
    }
}
