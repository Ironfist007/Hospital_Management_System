package com.hospital.controller;

import com.hospital.dto.AppointmentDTO;
import com.hospital.service.AppointmentService;
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
 * REST Controller for Appointment Management APIs
 */
@Slf4j
@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointment Management", description = "APIs for managing appointments")
public class AppointmentController {
    
    private final AppointmentService appointmentService;
    
    @PostMapping
    @Operation(summary = "Book an appointment", description = "Create a new appointment with pessimistic locking to prevent overbooking")
    public ResponseEntity<AppointmentDTO> bookAppointment(
            @Valid @RequestBody AppointmentDTO appointmentDTO) {
        log.info("POST /appointments - Booking new appointment");
        AppointmentDTO bookedAppointment = appointmentService.bookAppointment(appointmentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookedAppointment);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get appointment by ID", description = "Retrieve appointment details by appointment ID")
    public ResponseEntity<AppointmentDTO> getAppointmentById(@PathVariable Long id) {
        log.info("GET /appointments/{} - Fetching appointment", id);
        AppointmentDTO appointment = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(appointment);
    }
    
    @GetMapping
    @Operation(summary = "Get all appointments", description = "Retrieve all appointments with pagination and sorting")
    public ResponseEntity<Page<AppointmentDTO>> getAllAppointments(Pageable pageable) {
        log.info("GET /appointments - Fetching all appointments");
        Page<AppointmentDTO> appointments = appointmentService.getAllAppointments(pageable);
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get patient appointments", description = "Retrieve all appointments for a specific patient")
    public ResponseEntity<Page<AppointmentDTO>> getPatientAppointments(
            @PathVariable Long patientId,
            Pageable pageable) {
        log.info("GET /appointments/patient/{} - Fetching patient appointments", patientId);
        Page<AppointmentDTO> appointments = appointmentService
                .getPatientAppointments(patientId, pageable);
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Get doctor appointments", description = "Retrieve all appointments for a specific doctor")
    public ResponseEntity<Page<AppointmentDTO>> getDoctorAppointments(
            @PathVariable Long doctorId,
            Pageable pageable) {
        log.info("GET /appointments/doctor/{} - Fetching doctor appointments", doctorId);
        Page<AppointmentDTO> appointments = appointmentService
                .getDoctorAppointments(doctorId, pageable);
        return ResponseEntity.ok(appointments);
    }
    
    @PutMapping("/{id}/status")
    @Operation(summary = "Update appointment status", description = "Change the status of an appointment")
    public ResponseEntity<AppointmentDTO> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        log.info("PUT /appointments/{}/status - Updating status to: {}", id, status);
        AppointmentDTO updatedAppointment = appointmentService
                .updateAppointmentStatus(id, status);
        return ResponseEntity.ok(updatedAppointment);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel appointment", description = "Cancel an existing appointment")
    public ResponseEntity<AppointmentDTO> cancelAppointment(@PathVariable Long id) {
        log.info("DELETE /appointments/{} - Cancelling appointment", id);
        AppointmentDTO cancelledAppointment = appointmentService.cancelAppointment(id);
        return ResponseEntity.ok(cancelledAppointment);
    }
}
