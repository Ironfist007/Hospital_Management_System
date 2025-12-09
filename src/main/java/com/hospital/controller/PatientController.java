package com.hospital.controller;

import com.hospital.dto.PatientDTO;
import com.hospital.service.PatientService;
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

/**
 * REST Controller for Patient Management APIs
 */
@Slf4j
@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
@Tag(name = "Patient Management", description = "APIs for managing patient information")
public class PatientController {
    
    private final PatientService patientService;
    
    @PostMapping
    @Operation(summary = "Create a new patient", description = "Register a new patient in the system")
    public ResponseEntity<PatientDTO> createPatient(@Valid @RequestBody PatientDTO patientDTO) {
        log.info("POST /patients - Creating new patient");
        PatientDTO createdPatient = patientService.createPatient(patientDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPatient);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get patient by ID", description = "Retrieve patient details by patient ID")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable Long id) {
        log.info("GET /patients/{} - Fetching patient", id);
        PatientDTO patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }
    
    @GetMapping
    @Operation(summary = "Get all patients", description = "Retrieve all patients with pagination and sorting")
    public ResponseEntity<Page<PatientDTO>> getAllPatients(Pageable pageable) {
        log.info("GET /patients - Fetching all patients");
        Page<PatientDTO> patients = patientService.getAllPatients(pageable);
        return ResponseEntity.ok(patients);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update patient", description = "Update patient information")
    public ResponseEntity<PatientDTO> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientDTO patientDTO) {
        log.info("PUT /patients/{} - Updating patient", id);
        PatientDTO updatedPatient = patientService.updatePatient(id, patientDTO);
        return ResponseEntity.ok(updatedPatient);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete patient", description = "Remove a patient from the system")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        log.info("DELETE /patients/{} - Deleting patient", id);
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/history")
    @Operation(summary = "Get patient medical history", description = "Retrieve patient's medical records and history")
    public ResponseEntity<PatientDTO> getPatientHistory(@PathVariable Long id) {
        log.info("GET /patients/{}/history - Fetching patient medical history", id);
        PatientDTO patientHistory = patientService.getPatientHistory(id);
        return ResponseEntity.ok(patientHistory);
    }
}
