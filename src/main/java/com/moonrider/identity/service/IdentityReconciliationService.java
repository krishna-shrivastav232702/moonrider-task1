package com.moonrider.identity.service;

import com.moonrider.identity.dto.IdentifyRequest;
import com.moonrider.identity.dto.IdentifyResponse;
import com.moonrider.identity.entity.Contact;
import com.moonrider.identity.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdentityReconciliationService {
    
    private final ContactRepository contactRepository;
    
    /**
     * Main method to identify and reconcile contact information
     * Implements the covert strategy for linking contacts across purchases
     */
    @Transactional
    public IdentifyResponse identifyContact(IdentifyRequest request) {
        log.info("Processing identity reconciliation for email: {} and phone: {}", request.getEmail(), request.getPhoneNumber());
        
        // Phase 1: Reconnaissance - Find existing contacts
        List<Contact> existingContacts = findExistingContacts(request);
        
        if (existingContacts.isEmpty()) {
            // Phase 2a: No existing contacts - Create new primary contact
            return createNewPrimaryContact(request);
        } else {
            // Phase 2b: Existing contacts found - Execute reconciliation strategy
            return executeReconciliationStrategy(request, existingContacts);
        }
    }
    
    /**
     * Covert reconnaissance to find existing contacts
     */
    private List<Contact> findExistingContacts(IdentifyRequest request) {
        return contactRepository.findByEmailOrPhoneNumber(
            request.getEmail(), 
            request.getPhoneNumber()
        );
    }
    
    /**
     * Create new primary contact when no existing records are found
     */
    private IdentifyResponse createNewPrimaryContact(IdentifyRequest request) {
        log.info("Creating new primary contact - operating under deep cover");
        
        Contact newContact = new Contact();
        newContact.setEmail(request.getEmail());
        newContact.setPhoneNumber(request.getPhoneNumber());
        newContact.setLinkPrecedence(Contact.LinkPrecedence.PRIMARY);
        
        Contact savedContact = contactRepository.save(newContact);
        
        return buildResponse(savedContact, Collections.singletonList(savedContact));
    }
    
    /**
     * Execute sophisticated reconciliation strategy for existing contacts
     */
    private IdentifyResponse executeReconciliationStrategy(IdentifyRequest request, 
                                                            List<Contact> existingContacts) {
        log.info("Executing reconciliation strategy for {} existing contacts", existingContacts.size());
        
        // Gather intelligence on all linked contacts
        Set<Long> allContactIds = gatherLinkedContactIds(existingContacts);
        List<Contact> allLinkedContacts = getAllLinkedContacts(allContactIds);
        
        // Identify the primary contact (oldest in the network)
        Contact primaryContact = identifyPrimaryContact(allLinkedContacts);
        
        // Check if new information requires creating a secondary contact
        boolean needsNewSecondary = checkIfNewSecondaryRequired(request, allLinkedContacts);
        
        if (needsNewSecondary) {
            createSecondaryContact(request, primaryContact);
            // Refresh the linked contacts list
            allLinkedContacts = contactRepository.findAllLinkedContacts(primaryContact.getId());
        }
        
        return buildResponse(primaryContact, allLinkedContacts);
    }
    
    /**
     * Gather all contact IDs in the linked network
     */
    private Set<Long> gatherLinkedContactIds(List<Contact> contacts) {
        Set<Long> allIds = new HashSet<>();
        
        for (Contact contact : contacts) {
            allIds.add(contact.getId());
            if (contact.getLinkedId() != null) {
                allIds.add(contact.getLinkedId());
            }
        }
        
        return allIds;
    }
    
    /**
     * Retrieve all contacts in the linked network
     */
    private List<Contact> getAllLinkedContacts(Set<Long> contactIds) {
        List<Contact> allContacts = new ArrayList<>();
        
        for (Long id : contactIds) {
            List<Contact> linkedContacts = contactRepository.findAllLinkedContacts(id);
            allContacts.addAll(linkedContacts);
        }
        
        // Remove duplicates and sort by creation time
        return allContacts.stream()
                .distinct()
                .sorted(Comparator.comparing(Contact::getCreatedAt))
                .collect(Collectors.toList());
    }
    
    /**
     * Identify the primary contact in the network (oldest contact becomes primary)
     */
    private Contact identifyPrimaryContact(List<Contact> allContacts) {
        Contact oldestContact = allContacts.stream()
                .min(Comparator.comparing(Contact::getCreatedAt))
                .orElseThrow(() -> new RuntimeException("No contacts found in network"));
        
        // If the oldest contact is not primary, promote it and demote others
        if (oldestContact.getLinkPrecedence() != Contact.LinkPrecedence.PRIMARY) {
            promoteToPrimary(oldestContact, allContacts);
        }
        
        return oldestContact;
    }
    
    /**
     * Promote a contact to primary and adjust the network accordingly
     */
    private void promoteToPrimary(Contact newPrimary, List<Contact> allContacts) {
        log.info("Promoting contact {} to primary status", newPrimary.getId());
        
        // Set the new primary
        newPrimary.setLinkPrecedence(Contact.LinkPrecedence.PRIMARY);
        newPrimary.setLinkedId(null);
        contactRepository.save(newPrimary);
        
        // Update all other contacts to be secondary and link to new primary
        for (Contact contact : allContacts) {
            if (!contact.getId().equals(newPrimary.getId())) {
                contact.setLinkPrecedence(Contact.LinkPrecedence.SECONDARY);
                contact.setLinkedId(newPrimary.getId());
                contactRepository.save(contact);
            }
        }
    }
    
    /**
     * Check if new secondary contact is required
     */
    private boolean checkIfNewSecondaryRequired(IdentifyRequest request, List<Contact> existingContacts) {
        // Check if exact combination of email and phone already exists
        return existingContacts.stream()
                .noneMatch(contact -> 
                    Objects.equals(contact.getEmail(), request.getEmail()) &&
                    Objects.equals(contact.getPhoneNumber(), request.getPhoneNumber())
                );
    }
    
    /**
     * Create a new secondary contact linked to the primary
     */
    private void createSecondaryContact(IdentifyRequest request, Contact primaryContact) {
        log.info("Creating secondary contact linked to primary: {}", primaryContact.getId());
        
        Contact secondaryContact = new Contact();
        secondaryContact.setEmail(request.getEmail());
        secondaryContact.setPhoneNumber(request.getPhoneNumber());
        secondaryContact.setLinkedId(primaryContact.getId());
        secondaryContact.setLinkPrecedence(Contact.LinkPrecedence.SECONDARY);
        
        contactRepository.save(secondaryContact);
    }
    
    /**
     * Build the final response with consolidated contact information
     */
    private IdentifyResponse buildResponse(Contact primaryContact, List<Contact> allContacts) {
        // Collect all unique emails and phone numbers
        Set<String> emails = new LinkedHashSet<>();
        Set<String> phoneNumbers = new LinkedHashSet<>();
        List<Long> secondaryContactIds = new ArrayList<>();
        
        for (Contact contact : allContacts) {
            if (contact.getEmail() != null && !contact.getEmail().trim().isEmpty()) {
                emails.add(contact.getEmail());
            }
            if (contact.getPhoneNumber() != null && !contact.getPhoneNumber().trim().isEmpty()) {
                phoneNumbers.add(contact.getPhoneNumber());
            }
            
            // Collect secondary contact IDs
            if (contact.getLinkPrecedence() == Contact.LinkPrecedence.SECONDARY) {
                secondaryContactIds.add(contact.getId());
            }
        }
        
        // Sort secondary contact IDs for consistent response
        secondaryContactIds.sort(Long::compareTo);
        
        IdentifyResponse.ContactInfo contactInfo = new IdentifyResponse.ContactInfo(
            primaryContact.getId(),
            new ArrayList<>(emails),
            new ArrayList<>(phoneNumbers),
            secondaryContactIds
        );
        
        log.info("Successfully consolidated identity with primary ID: {} and {} secondary contacts", primaryContact.getId(), secondaryContactIds.size());
        
        return new IdentifyResponse(contactInfo);
    }
}