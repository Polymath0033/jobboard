package com.polymath.jobboard.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.polymath.jobboard.dto.response.ErrorResponse;
import com.polymath.jobboard.dto.response.ErrorValidationResponse;
import com.polymath.jobboard.utils.responseHandler.ResponseHandler;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionsHandler {

    @ExceptionHandler(UserAlreadyExists.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserAlreadyExists(UserAlreadyExists ex) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(),ex.getMessage(),System.currentTimeMillis());
    }

    @ExceptionHandler(UserDoesNotExists.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserDoesNotExists(UserDoesNotExists ex) {
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(),ex.getMessage(),System.currentTimeMillis());
    }

    @ExceptionHandler(CustomBadRequest.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCustomBadRequest(CustomBadRequest ex){
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(),ex.getMessage(),System.currentTimeMillis());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorValidationResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach((error) -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return new ErrorValidationResponse(HttpStatus.BAD_REQUEST.value(),errors,System.currentTimeMillis());
    }

    @ExceptionHandler(CustomNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleCustomNotFound(CustomNotFound ex) {
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(),ex.getMessage(),System.currentTimeMillis());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDeniedException(AccessDeniedException ex) {
        return new ErrorResponse(HttpStatus.FORBIDDEN.value(),ex.getMessage(),System.currentTimeMillis());

    }

    @ExceptionHandler(MultiRoleException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleRoleBasedException(MultiRoleException ex) {
        String roleRequirement = ex.isRequireAll()?"all of":"any of";
        String roles = ex.getRequiredRoles().stream().map(Enum::name).collect(Collectors.joining(","));
        String message = String.format("This actions require(s) %s these roles %s",roleRequirement,roles);
        return new ErrorResponse(HttpStatus.FORBIDDEN.value(),message,System.currentTimeMillis());
    }

    @ExceptionHandler(SingleRoleBasedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleRoleBasedException(SingleRoleBasedException ex) {
        String message = String.format("This action requires %s role",ex.getRole());
        return new ErrorResponse(HttpStatus.FORBIDDEN.value(),message,System.currentTimeMillis());
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthenticationException(AuthenticationException ex) {
        return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(),ex.getMessage(),System.currentTimeMillis());
    }

    @ExceptionHandler(CustomNotAuthorized.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleNotAuthorizedException(CustomNotAuthorized ex) {
        return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(),ex.getMessage(),System.currentTimeMillis());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleExpiredJwtException(ExpiredJwtException ex) {
        return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(),ex.getMessage(),System.currentTimeMillis());
    }
    @ExceptionHandler(AuthorizationDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthorizationException(AuthorizationDeniedException ex) {
        return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(),ex.getMessage(),System.currentTimeMillis());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<Map<String,Object>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        Map<String,Object> responseBody = new HashMap<>();
        responseBody.put("status",HttpStatus.METHOD_NOT_ALLOWED.value());
        responseBody.put("message",ex.getMethod());
        responseBody.put("path",request.getDescription(false));
        responseBody.put("error","Method Not Allowed");
        return new ResponseEntity<>(responseBody,HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex){
        String message= "Invalid input value";
        if(ex.getCause() instanceof InvalidFormatException cause){
            if(cause.getTargetType()!=null&&cause.getTargetType().isEnum()){
                message=String.format(
                        "Invalid value: '%s'. Allowed values for %s are: %s",
                        cause.getValue(),cause.getTargetType().getSimpleName(),
                        Arrays.toString(cause.getTargetType().getEnumConstants())
                );
            }
        }
        return ResponseHandler.handleResponse(null,HttpStatus.BAD_REQUEST,message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleException(Exception ex){
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        responseBody.put("message", "An error occurred. Please try again later.");
        responseBody.put("error","Internal Server Error");
        responseBody.put("cause",ex.getCause());
        return new ResponseEntity<>(responseBody,HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
