package com.polymath.jobboard.exceptions;

public class UserDoesNotExists extends RuntimeException {
    public UserDoesNotExists(String message) {
        super(message);
    }
}
