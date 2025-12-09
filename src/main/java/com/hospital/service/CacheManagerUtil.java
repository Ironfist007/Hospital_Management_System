package com.hospital.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * Caching utilities using Spring Cache abstraction
 */
@Component
public class CacheManagerUtil {
    
    public static final String PATIENT_CACHE = "patients";
    public static final String DOCTOR_CACHE = "doctors";
    public static final String APPOINTMENT_CACHE = "appointments";
    public static final String MEDICAL_RECORD_CACHE = "medicalRecords";
    
    /**
     * Clear all caches
     */
    @CacheEvict(value = {PATIENT_CACHE, DOCTOR_CACHE, APPOINTMENT_CACHE, MEDICAL_RECORD_CACHE}, 
            allEntries = true)
    public void clearAllCaches() {
        // Cache will be cleared
    }
    
    /**
     * Clear patient cache
     */
    @CacheEvict(value = PATIENT_CACHE, allEntries = true)
    public void clearPatientCache() {
        // Cache will be cleared
    }
    
    /**
     * Clear doctor cache
     */
    @CacheEvict(value = DOCTOR_CACHE, allEntries = true)
    public void clearDoctorCache() {
        // Cache will be cleared
    }
    
    /**
     * Clear appointment cache
     */
    @CacheEvict(value = APPOINTMENT_CACHE, allEntries = true)
    public void clearAppointmentCache() {
        // Cache will be cleared
    }
}
