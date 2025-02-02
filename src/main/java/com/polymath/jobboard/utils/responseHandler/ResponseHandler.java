package com.polymath.jobboard.utils.responseHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHandler {
    public static ResponseEntity<Object> handleResponse(Object response, HttpStatus status,String message) {
        Map<String,Object> responseMap = new LinkedHashMap<>();
        responseMap.put("status", status);
        responseMap.put("message", message);
        if(response!=null) responseMap.put("data", response);
        return new ResponseEntity<>(responseMap, status);
    }
}
