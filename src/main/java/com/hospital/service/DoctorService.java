package com.hospital.service;

import com.hospital.dto.DoctorDTO;
import com.hospital.entity.Doctor;
import com.hospital.exception.DuplicateResourceException;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Doctor management
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DoctorService {
    
    private final DoctorRepository doctorRepository;
    
    /**
     * Create a new doctor
     */
    public DoctorDTO createDoctor(DoctorDTO doctorDTO) {
        log.info("Creating new doctor: {}", doctorDTO.getEmail());
        
        // Check for duplicate email
        if (doctorRepository.findByEmail(doctorDTO.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Doctor with email " + doctorDTO.getEmail() + " already exists");
        }
        
        Doctor doctor = convertToEntity(doctorDTO);
        Doctor savedDoctor = doctorRepository.save(doctor);
        
        log.info("Doctor created successfully with ID: {}", savedDoctor.getId());
        return convertToDTO(savedDoctor);
    }
    
    /**
     * Get doctor by ID
     */
    @Transactional(readOnly = true)
    public DoctorDTO getDoctorById(Long id) {
        log.info("Fetching doctor with ID: {}", id);
        
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + id));
        
        return convertToDTO(doctor);
    }
    
    /**
     * Get all doctors with pagination
     */
    @Transactional(readOnly = true)
    public Page<DoctorDTO> getAllDoctors(Pageable pageable) {
        log.info("Fetching all doctors with pagination");
        return doctorRepository.findAll(pageable).map(this::convertToDTO);
    }
    
    /**
     * Get doctors by specialization
     */
    @Transactional(readOnly = true)
    public List<DoctorDTO> getDoctorsBySpecialization(String specialization) {
        log.info("Fetching doctors with specialization: {}", specialization);
        return doctorRepository.findBySpecialization(specialization)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get doctors by department
     */
    @Transactional(readOnly = true)
    public List<DoctorDTO> getDoctorsByDepartment(String department) {
        log.info("Fetching doctors from department: {}", department);
        return doctorRepository.findByDepartment(department)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Update doctor information
     */
    public DoctorDTO updateDoctor(Long id, DoctorDTO doctorDTO) {
        log.info("Updating doctor with ID: {}", id);
        
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + id));
        
        // Update fields
        doctor.setFirstName(doctorDTO.getFirstName());
        doctor.setLastName(doctorDTO.getLastName());
        doctor.setEmail(doctorDTO.getEmail());
        doctor.setPhone(doctorDTO.getPhone());
        doctor.setSpecialization(doctorDTO.getSpecialization());
        doctor.setLicenseNumber(doctorDTO.getLicenseNumber());
        doctor.setDepartment(doctorDTO.getDepartment());
        doctor.setYearsOfExperience(doctorDTO.getYearsOfExperience());
        
        Doctor updatedDoctor = doctorRepository.save(doctor);
        log.info("Doctor updated successfully with ID: {}", id);
        
        return convertToDTO(updatedDoctor);
    }
    
    /**
     * Delete doctor
     */
    public void deleteDoctor(Long id) {
        log.info("Deleting doctor with ID: {}", id);
        
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + id));
        
        doctorRepository.delete(doctor);
        log.info("Doctor deleted successfully with ID: {}", id);
    }
    
    /**
     * Check doctor availability
     */
    @Transactional(readOnly = true)
    public boolean isDoctorAvailable(Long doctorId) {
        log.info("Checking availability for doctor ID: {}", doctorId);
        
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + doctorId));
        
        return !doctor.getAvailabilities().isEmpty() && 
               doctor.getAvailabilities().stream().anyMatch(av -> av.isAvailable());
    }
    
    // Helper methods
    private DoctorDTO convertToDTO(Doctor doctor) {
        return DoctorDTO.builder()
                .id(doctor.getId())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .email(doctor.getEmail())
                .phone(doctor.getPhone())
                .specialization(doctor.getSpecialization())
                .licenseNumber(doctor.getLicenseNumber())
                .department(doctor.getDepartment())
                .yearsOfExperience(doctor.getYearsOfExperience())
                .createdAt(doctor.getCreatedAt())
                .updatedAt(doctor.getUpdatedAt())
                .build();
    }
    
    private Doctor convertToEntity(DoctorDTO doctorDTO) {
        return Doctor.builder()
                .firstName(doctorDTO.getFirstName())
                .lastName(doctorDTO.getLastName())
                .email(doctorDTO.getEmail())
                .phone(doctorDTO.getPhone())
                .specialization(doctorDTO.getSpecialization())
                .licenseNumber(doctorDTO.getLicenseNumber())
                .department(doctorDTO.getDepartment())
                .yearsOfExperience(doctorDTO.getYearsOfExperience())
                .build();
    }
}
