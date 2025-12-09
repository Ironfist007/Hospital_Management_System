package com.hospital.service;

import com.hospital.dto.MedicalRecordDTO;
import com.hospital.entity.Doctor;
import com.hospital.entity.MedicalRecord;
import com.hospital.entity.Patient;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.MedicalRecordRepository;
import com.hospital.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Medical Record management
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MedicalRecordService {
    
    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    
    /**
     * Create a new medical record
     */
    public MedicalRecordDTO createMedicalRecord(MedicalRecordDTO recordDTO) {
        log.info("Creating medical record for patient ID: {}", recordDTO.getPatientId());
        
        Patient patient = patientRepository.findById(recordDTO.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with ID: " + recordDTO.getPatientId()));
        
        Doctor doctor = doctorRepository.findById(recordDTO.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found with ID: " + recordDTO.getDoctorId()));
        
        MedicalRecord record = MedicalRecord.builder()
                .patient(patient)
                .doctor(doctor)
                .diagnosis(recordDTO.getDiagnosis())
                .treatment(recordDTO.getTreatment())
                .medications(recordDTO.getMedications())
                .notes(recordDTO.getNotes())
                .allergies(recordDTO.getAllergies())
                .chronicDiseases(recordDTO.getChronicDiseases())
                .build();
        
        MedicalRecord savedRecord = medicalRecordRepository.save(record);
        log.info("Medical record created successfully with ID: {}", savedRecord.getId());
        
        return convertToDTO(savedRecord);
    }
    
    /**
     * Get medical record by ID
     */
    @Transactional(readOnly = true)
    public MedicalRecordDTO getMedicalRecordById(Long id) {
        log.info("Fetching medical record with ID: {}", id);
        
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Medical record not found with ID: " + id));
        
        return convertToDTO(record);
    }
    
    /**
     * Get patient's medical records with pagination
     */
    @Transactional(readOnly = true)
    public Page<MedicalRecordDTO> getPatientMedicalRecords(Long patientId, Pageable pageable) {
        log.info("Fetching medical records for patient ID: {}", patientId);
        
        // Verify patient exists
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with ID: " + patientId);
        }
        
        return medicalRecordRepository.findByPatientId(patientId, pageable)
                .map(this::convertToDTO);
    }
    
    /**
     * Get patient's medical history (last records)
     */
    @Transactional(readOnly = true)
    public List<MedicalRecordDTO> getPatientMedicalHistory(Long patientId) {
        log.info("Fetching medical history for patient ID: {}", patientId);
        
        // Verify patient exists
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with ID: " + patientId);
        }
        
        return medicalRecordRepository.findByPatientIdOrderByRecordedAtDesc(patientId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get doctor's medical records with pagination
     */
    @Transactional(readOnly = true)
    public Page<MedicalRecordDTO> getDoctorMedicalRecords(Long doctorId, Pageable pageable) {
        log.info("Fetching medical records created by doctor ID: {}", doctorId);
        
        // Verify doctor exists
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor not found with ID: " + doctorId);
        }
        
        return medicalRecordRepository.findByDoctorId(doctorId, pageable)
                .map(this::convertToDTO);
    }
    
    /**
     * Update medical record
     */
    public MedicalRecordDTO updateMedicalRecord(Long id, MedicalRecordDTO recordDTO) {
        log.info("Updating medical record with ID: {}", id);
        
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Medical record not found with ID: " + id));
        
        record.setDiagnosis(recordDTO.getDiagnosis());
        record.setTreatment(recordDTO.getTreatment());
        record.setMedications(recordDTO.getMedications());
        record.setNotes(recordDTO.getNotes());
        record.setAllergies(recordDTO.getAllergies());
        record.setChronicDiseases(recordDTO.getChronicDiseases());
        
        MedicalRecord updatedRecord = medicalRecordRepository.save(record);
        log.info("Medical record updated successfully with ID: {}", id);
        
        return convertToDTO(updatedRecord);
    }
    
    /**
     * Delete medical record
     */
    public void deleteMedicalRecord(Long id) {
        log.info("Deleting medical record with ID: {}", id);
        
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Medical record not found with ID: " + id));
        
        medicalRecordRepository.delete(record);
        log.info("Medical record deleted successfully with ID: {}", id);
    }
    
    // Helper method
    private MedicalRecordDTO convertToDTO(MedicalRecord record) {
        return MedicalRecordDTO.builder()
                .id(record.getId())
                .patientId(record.getPatient().getId())
                .doctorId(record.getDoctor().getId())
                .diagnosis(record.getDiagnosis())
                .treatment(record.getTreatment())
                .medications(record.getMedications())
                .notes(record.getNotes())
                .allergies(record.getAllergies())
                .chronicDiseases(record.getChronicDiseases())
                .recordedAt(record.getRecordedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }
}
