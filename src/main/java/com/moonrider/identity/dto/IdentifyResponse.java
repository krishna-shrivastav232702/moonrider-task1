package com.moonrider.identity.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdentifyResponse {
    private ContactInfo contact;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactInfo {
        private Long primaryContactId;
        private List<String> emails;
        private List<String> phoneNumbers;
        private List<Long> secondaryContactIds;
    }
}