package com.hospital.service;

import com.hospital.dto.AppointmentDTO;
import com.hospital.entity.Appointment;
import com.hospital.entity.Doctor;
import com.hospital.entity.Patient;
import com.hospital.exception.AppointmentBookingException;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service layer for Appointment management
 * Implements pessimistic locking to prevent overbooking
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService {
    
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    
    @Value("${hospital.appointment.max-slots-per-day:10}")
    private int maxSlotsPerDay;
    
    /**
     * Book an appointment with pessimistic locking
     * Prevents race conditions and overbooking
     */
    public AppointmentDTO bookAppointment(AppointmentDTO appointmentDTO) {
        log.info("Booking appointment for patient ID: {}, doctor ID: {}", 
                 appointmentDTO.getPatientId(), appointmentDTO.getDoctorId());
        
        // Fetch patient and doctor with validation
        Patient patient = patientRepository.findById(appointmentDTO.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with ID: " + appointmentDTO.getPatientId()));
        
        Doctor doctor = doctorRepository.findById(appointmentDTO.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found with ID: " + appointmentDTO.getDoctorId()));
        
        LocalDateTime appointmentTime = appointmentDTO.getAppointmentDateTime();
        
        // Validate appointment time is in the future
        if (appointmentTime.isBefore(LocalDateTime.now())) {
            throw new AppointmentBookingException("Appointment time must be in the future");
        }
        
        // Check for conflicting appointments for the patient
        List<Appointment> patientConflicts = appointmentRepository
                .findPatientAppointmentsByDateRange(
                        appointmentDTO.getPatientId(),
                        appointmentTime.minusHours(1),
                        appointmentTime.plusHours(1)
                );
        
        if (!patientConflicts.isEmpty()) {
            throw new AppointmentBookingException(
                    "Patient has a conflicting appointment at this time");
        }
        
        // Check for conflicting appointments for the doctor
        List<Appointment> doctorConflicts = appointmentRepository
                .findDoctorAppointmentsByDateRange(
                        appointmentDTO.getDoctorId(),
                        appointmentTime.minusMinutes(30),
                        appointmentTime.plusMinutes(30)
                );
        
        if (!doctorConflicts.isEmpty()) {
            throw new AppointmentBookingException(
                    "Doctor is not available at this time");
        }
        
        // Check daily slot limit for doctor
        long dailyAppointments = appointmentRepository.countDailyAppointments(
                appointmentDTO.getDoctorId(),
                appointmentTime.toLocalDate().atStartOfDay(),
                appointmentTime.toLocalDate().atTime(23, 59, 59)
        );
        
        if (dailyAppointments >= maxSlotsPerDay) {
            throw new AppointmentBookingException(
                    "Doctor has reached maximum appointments for this day");
        }
        
        // Create and save appointment
        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentDateTime(appointmentTime)
                .reason(appointmentDTO.getReason())
                .notes(appointmentDTO.getNotes())
                .status(Appointment.AppointmentStatus.SCHEDULED)
                .build();
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment booked successfully with ID: {}", savedAppointment.getId());
        
        return convertToDTO(savedAppointment);
    }
    
    /**
     * Get appointment by ID
     */
    @Transactional(readOnly = true)
    public AppointmentDTO getAppointmentById(Long id) {
        log.info("Fetching appointment with ID: {}", id);
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with ID: " + id));
        
        return convertToDTO(appointment);
    }
    
    /**
     * Get all appointments with pagination
     */
    @Transactional(readOnly = true)
    public Page<AppointmentDTO> getAllAppointments(Pageable pageable) {
        log.info("Fetching all appointments with pagination");
        return appointmentRepository.findAll(pageable).map(this::convertToDTO);
    }
    
    /**
     * Get patient's appointments with pagination
     */
    @Transactional(readOnly = true)
    public Page<AppointmentDTO> getPatientAppointments(Long patientId, Pageable pageable) {
        log.info("Fetching appointments for patient ID: {}", patientId);
        
        // Verify patient exists
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with ID: " + patientId);
        }
        
        return appointmentRepository.findByPatientId(patientId, pageable)
                .map(this::convertToDTO);
    }
    
    /**
     * Get doctor's appointments with pagination
     */
    @Transactional(readOnly = true)
    public Page<AppointmentDTO> getDoctorAppointments(Long doctorId, Pageable pageable) {
        log.info("Fetching appointments for doctor ID: {}", doctorId);
        
        // Verify doctor exists
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor not found with ID: " + doctorId);
        }
        
        return appointmentRepository.findByDoctorId(doctorId, pageable)
                .map(this::convertToDTO);
    }
    
    /**
     * Update appointment status
     */
    public AppointmentDTO updateAppointmentStatus(Long id, String newStatus) {
        log.info("Updating appointment ID: {} status to: {}", id, newStatus);
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with ID: " + id));
        
        try {
            Appointment.AppointmentStatus status = 
                    Appointment.AppointmentStatus.valueOf(newStatus.toUpperCase());
            appointment.setStatus(status);
        } catch (IllegalArgumentException e) {
            throw new AppointmentBookingException("Invalid appointment status: " + newStatus);
        }
        
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment status updated successfully");
        
        return convertToDTO(updatedAppointment);
    }
    
    /**
     * Cancel appointment
     */
    public AppointmentDTO cancelAppointment(Long id) {
        log.info("Cancelling appointment with ID: {}", id);
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with ID: " + id));
        
        if (appointment.getStatus() == Appointment.AppointmentStatus.CANCELLED) {
            throw new AppointmentBookingException("Appointment is already cancelled");
        }
        
        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        Appointment cancelledAppointment = appointmentRepository.save(appointment);
        
        log.info("Appointment cancelled successfully with ID: {}", id);
        return convertToDTO(cancelledAppointment);
    }
    
    // Helper method
    private AppointmentDTO convertToDTO(Appointment appointment) {
        return AppointmentDTO.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatient().getId())
                .doctorId(appointment.getDoctor().getId())
                .appointmentDateTime(appointment.getAppointmentDateTime())
                .status(appointment.getStatus().toString())
                .reason(appointment.getReason())
                .notes(appointment.getNotes())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }
}
