package com.hospital.service;

import com.hospital.dto.PatientDTO;
import com.hospital.entity.Patient;
import com.hospital.entity.User;
import com.hospital.exception.DuplicateResourceException;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.repository.PatientRepository;
import com.hospital.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer for Patient management
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PatientService {
    
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    
    /**
     * Create a new patient
     */
    public PatientDTO createPatient(PatientDTO patientDTO) {
        log.info("Creating new patient: {}", patientDTO.getEmail());
        
        // Check for duplicate email
        if (patientRepository.findByEmail(patientDTO.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Patient with email " + patientDTO.getEmail() + " already exists");
        }
        
        // Check for duplicate phone
        if (patientDTO.getPhone() != null && patientRepository.findByPhone(patientDTO.getPhone()).isPresent()) {
            throw new DuplicateResourceException("Patient with phone " + patientDTO.getPhone() + " already exists");
        }
        
        Patient patient = convertToEntity(patientDTO);
        Patient savedPatient = patientRepository.save(patient);
        
        log.info("Patient created successfully with ID: {}", savedPatient.getId());
        return convertToDTO(savedPatient);
    }
    
    /**
     * Get patient by ID
     */
    @Transactional(readOnly = true)
    public PatientDTO getPatientById(Long id) {
        log.info("Fetching patient with ID: {}", id);
        
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));
        
        return convertToDTO(patient);
    }
    
    /**
     * Get all patients with pagination
     */
    @Transactional(readOnly = true)
    public Page<PatientDTO> getAllPatients(Pageable pageable) {
        log.info("Fetching all patients with pagination");
        return patientRepository.findAll(pageable).map(this::convertToDTO);
    }
    
    /**
     * Update patient information
     */
    public PatientDTO updatePatient(Long id, PatientDTO patientDTO) {
        log.info("Updating patient with ID: {}", id);
        
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));
        
        // Update fields
        patient.setFirstName(patientDTO.getFirstName());
        patient.setLastName(patientDTO.getLastName());
        patient.setEmail(patientDTO.getEmail());
        patient.setPhone(patientDTO.getPhone());
        patient.setAddress(patientDTO.getAddress());
        patient.setAge(patientDTO.getAge());
        patient.setBloodType(patientDTO.getBloodType());
        patient.setGender(patientDTO.getGender());
        
        Patient updatedPatient = patientRepository.save(patient);
        log.info("Patient updated successfully with ID: {}", id);
        
        return convertToDTO(updatedPatient);
    }
    
    /**
     * Delete patient
     */
    public void deletePatient(Long id) {
        log.info("Deleting patient with ID: {}", id);
        
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));
        
        patientRepository.delete(patient);
        log.info("Patient deleted successfully with ID: {}", id);
    }
    
    /**
     * Get patient medical history
     */
    @Transactional(readOnly = true)
    public PatientDTO getPatientHistory(Long id) {
        log.info("Fetching medical history for patient ID: {}", id);
        
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));
        
        return convertToDTO(patient);
    }
    
    // Helper methods
    private PatientDTO convertToDTO(Patient patient) {
        return PatientDTO.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .email(patient.getEmail())
                .phone(patient.getPhone())
                .address(patient.getAddress())
                .age(patient.getAge())
                .bloodType(patient.getBloodType())
                .gender(patient.getGender())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .build();
    }
    
    private Patient convertToEntity(PatientDTO patientDTO) {
        return Patient.builder()
                .firstName(patientDTO.getFirstName())
                .lastName(patientDTO.getLastName())
                .email(patientDTO.getEmail())
                .phone(patientDTO.getPhone())
                .address(patientDTO.getAddress())
                .age(patientDTO.getAge())
                .bloodType(patientDTO.getBloodType())
                .gender(patientDTO.getGender())
                .build();
    }
}
