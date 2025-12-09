package com.hospital.controller;

import com.hospital.dto.MedicalRecordDTO;
import com.hospital.service.MedicalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Medical Record Management APIs
 */
@Slf4j
@RestController
@RequestMapping("/medical-records")
@RequiredArgsConstructor
@Tag(name = "Medical Records", description = "APIs for managing patient medical records")
public class MedicalRecordController {
    
    private final MedicalRecordService medicalRecordService;
    
    @PostMapping
    @Operation(summary = "Create a medical record", description = "Create a new medical record for a patient")
    public ResponseEntity<MedicalRecordDTO> createMedicalRecord(
            @Valid @RequestBody MedicalRecordDTO recordDTO) {
        log.info("POST /medical-records - Creating new medical record");
        MedicalRecordDTO createdRecord = medicalRecordService.createMedicalRecord(recordDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecord);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get medical record by ID", description = "Retrieve medical record details by ID")
    public ResponseEntity<MedicalRecordDTO> getMedicalRecordById(@PathVariable Long id) {
        log.info("GET /medical-records/{} - Fetching medical record", id);
        MedicalRecordDTO record = medicalRecordService.getMedicalRecordById(id);
        return ResponseEntity.ok(record);
    }
    
    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get patient medical records", description = "Retrieve all medical records for a specific patient")
    public ResponseEntity<Page<MedicalRecordDTO>> getPatientMedicalRecords(
            @PathVariable Long patientId,
            Pageable pageable) {
        log.info("GET /medical-records/patient/{} - Fetching patient medical records", patientId);
        Page<MedicalRecordDTO> records = medicalRecordService
                .getPatientMedicalRecords(patientId, pageable);
        return ResponseEntity.ok(records);
    }
    
    @GetMapping("/patient/{patientId}/history")
    @Operation(summary = "Get patient medical history", description = "Retrieve complete medical history for a patient")
    public ResponseEntity<List<MedicalRecordDTO>> getPatientMedicalHistory(
            @PathVariable Long patientId) {
        log.info("GET /medical-records/patient/{}/history - Fetching patient medical history", patientId);
        List<MedicalRecordDTO> history = medicalRecordService.getPatientMedicalHistory(patientId);
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Get doctor medical records", description = "Retrieve all medical records created by a specific doctor")
    public ResponseEntity<Page<MedicalRecordDTO>> getDoctorMedicalRecords(
            @PathVariable Long doctorId,
            Pageable pageable) {
        log.info("GET /medical-records/doctor/{} - Fetching doctor medical records", doctorId);
        Page<MedicalRecordDTO> records = medicalRecordService
                .getDoctorMedicalRecords(doctorId, pageable);
        return ResponseEntity.ok(records);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update medical record", description = "Update an existing medical record")
    public ResponseEntity<MedicalRecordDTO> updateMedicalRecord(
            @PathVariable Long id,
            @Valid @RequestBody MedicalRecordDTO recordDTO) {
        log.info("PUT /medical-records/{} - Updating medical record", id);
        MedicalRecordDTO updatedRecord = medicalRecordService.updateMedicalRecord(id, recordDTO);
        return ResponseEntity.ok(updatedRecord);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete medical record", description = "Remove a medical record from the system")
    public ResponseEntity<Void> deleteMedicalRecord(@PathVariable Long id) {
        log.info("DELETE /medical-records/{} - Deleting medical record", id);
        medicalRecordService.deleteMedicalRecord(id);
        return ResponseEntity.noContent().build();
    }
}
