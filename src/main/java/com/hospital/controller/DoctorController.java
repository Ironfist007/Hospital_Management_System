package com.hospital.controller;

import com.hospital.dto.DoctorDTO;
import com.hospital.service.DoctorService;
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
 * REST Controller for Doctor Management APIs
 */
@Slf4j
@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctor Management", description = "APIs for managing doctor information")
public class DoctorController {
    
    private final DoctorService doctorService;
    
    @PostMapping
    @Operation(summary = "Create a new doctor", description = "Register a new doctor in the system")
    public ResponseEntity<DoctorDTO> createDoctor(@Valid @RequestBody DoctorDTO doctorDTO) {
        log.info("POST /doctors - Creating new doctor");
        DoctorDTO createdDoctor = doctorService.createDoctor(doctorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDoctor);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get doctor by ID", description = "Retrieve doctor details by doctor ID")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable Long id) {
        log.info("GET /doctors/{} - Fetching doctor", id);
        DoctorDTO doctor = doctorService.getDoctorById(id);
        return ResponseEntity.ok(doctor);
    }
    
    @GetMapping
    @Operation(summary = "Get all doctors", description = "Retrieve all doctors with pagination and sorting")
    public ResponseEntity<Page<DoctorDTO>> getAllDoctors(Pageable pageable) {
        log.info("GET /doctors - Fetching all doctors");
        Page<DoctorDTO> doctors = doctorService.getAllDoctors(pageable);
        return ResponseEntity.ok(doctors);
    }
    
    @GetMapping("/specialization/{specialization}")
    @Operation(summary = "Get doctors by specialization", description = "Find doctors by their specialization")
    public ResponseEntity<List<DoctorDTO>> getDoctorsBySpecialization(
            @PathVariable String specialization) {
        log.info("GET /doctors/specialization/{} - Fetching doctors", specialization);
        List<DoctorDTO> doctors = doctorService.getDoctorsBySpecialization(specialization);
        return ResponseEntity.ok(doctors);
    }
    
    @GetMapping("/department/{department}")
    @Operation(summary = "Get doctors by department", description = "Find doctors by their department")
    public ResponseEntity<List<DoctorDTO>> getDoctorsByDepartment(
            @PathVariable String department) {
        log.info("GET /doctors/department/{} - Fetching doctors", department);
        List<DoctorDTO> doctors = doctorService.getDoctorsByDepartment(department);
        return ResponseEntity.ok(doctors);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update doctor", description = "Update doctor information")
    public ResponseEntity<DoctorDTO> updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody DoctorDTO doctorDTO) {
        log.info("PUT /doctors/{} - Updating doctor", id);
        DoctorDTO updatedDoctor = doctorService.updateDoctor(id, doctorDTO);
        return ResponseEntity.ok(updatedDoctor);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete doctor", description = "Remove a doctor from the system")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        log.info("DELETE /doctors/{} - Deleting doctor", id);
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/availability")
    @Operation(summary = "Check doctor availability", description = "Check if a doctor is available")
    public ResponseEntity<Boolean> isDoctorAvailable(@PathVariable Long id) {
        log.info("GET /doctors/{}/availability - Checking availability", id);
        boolean available = doctorService.isDoctorAvailable(id);
        return ResponseEntity.ok(available);
    }
}
