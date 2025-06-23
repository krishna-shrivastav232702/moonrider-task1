package com.moonrider.identity.repository;

import com.moonrider.identity.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    // Find contacts by email or phone number, excluding deleted records
        @Query("SELECT c FROM Contact c WHERE c.deletedAt IS NULL AND " + "(c.email = :email OR c.phoneNumber = :phoneNumber)")
        List<Contact> findByEmailOrPhoneNumber(@Param("email") String email, @Param("phoneNumber") String phoneNumber);

    // Find all contacts in a linked group (including primary and all secondaries)
        @Query("SELECT c FROM Contact c WHERE c.deletedAt IS NULL AND " + "(c.id = :primaryId OR c.linkedId = :primaryId) ORDER BY c.createdAt")
        List<Contact> findAllLinkedContacts(@Param("primaryId") Long primaryId);

    // Find primary contact by ID
        @Query("SELECT c FROM Contact c WHERE c.deletedAt IS NULL AND " + "c.id = :id AND c.linkPrecedence = 'PRIMARY'")
        Optional<Contact> findPrimaryById(@Param("id") Long id);

    // Find the oldest contact (potential primary) from a list of contacts
        @Query("SELECT c FROM Contact c WHERE c.deletedAt IS NULL AND c.id IN :ids " + "ORDER BY c.createdAt ASC")
        List<Contact> findOldestContacts(@Param("ids") List<Long> ids);
}