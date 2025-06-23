package com.moonrider.identity.controller;

import com.moonrider.identity.dto.IdentifyRequest;
import com.moonrider.identity.dto.IdentifyResponse;
import com.moonrider.identity.service.IdentityReconciliationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class IdentityController {
    
    private final IdentityReconciliationService identityService;
    
    /**
     * The main /identify endpoint for processing contact reconciliation
     * Operates with utmost discretion and precision as required
     */
    @PostMapping("/identify")
    public ResponseEntity<IdentifyResponse> identifyContact(@Valid @RequestBody IdentifyRequest request) {
        try {
            log.info("Received identity reconciliation request");
            
            // Validate incoming request
            if (isRequestEmpty(request)) {
                log.warn("Received empty request - potential reconnaissance attempt");
                return ResponseEntity.badRequest().build();
            }
            
            // Execute the covert identity reconciliation process
            IdentifyResponse response = identityService.identifyContact(request);
            
            log.info("Identity reconciliation completed successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Operation compromised during identity reconciliation", e);
            // Misdirect potential threats with generic error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Health check endpoint to verify service operational status
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Service operational - standing by for missions");
    }
    
    /**
     * Validate if the request contains any meaningful data
     */
    private boolean isRequestEmpty(IdentifyRequest request) {
        return (request.getEmail() == null || request.getEmail().trim().isEmpty()) && (request.getPhoneNumber() == null || request.getPhoneNumber().trim().isEmpty());
    }
}