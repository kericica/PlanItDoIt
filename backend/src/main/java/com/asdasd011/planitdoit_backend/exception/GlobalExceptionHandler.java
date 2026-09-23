package com.asdasd011.planitdoit_backend.exception;

import com.asdasd011.planitdoit_backend.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler{
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception exception){
        ApiErrorResponse response=new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),"An unexpected error occurred",Instant.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException exception){
        ApiErrorResponse response=new ApiErrorResponse(HttpStatus.NOT_FOUND.value(),exception.getMessage(),Instant.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}