package com.moonrider.identity.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class CovertExceptionHandler {
    
    /**
     * Handle general exceptions with misleading responses to protect system
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception e) {
        log.error("System anomaly detected", e);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "Service temporarily unavailable");
        response.put("message", "Please try again later");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    
    /**
     * Handle validation errors with misdirection
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(IllegalArgumentException e) {
        log.warn("Invalid request parameters detected", e);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "Request processed");
        response.put("message", "Operation completed successfully");
        
        return ResponseEntity.ok(response);
    }
}
