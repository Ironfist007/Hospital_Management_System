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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AppointmentService
 */
@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {
    
    @Mock
    private AppointmentRepository appointmentRepository;
    
    @Mock
    private PatientRepository patientRepository;
    
    @Mock
    private DoctorRepository doctorRepository;
    
    @InjectMocks
    private AppointmentService appointmentService;
    
    private Patient testPatient;
    private Doctor testDoctor;
    private AppointmentDTO appointmentDTO;
    
    @BeforeEach
    void setUp() {
        testPatient = Patient.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();
        
        testDoctor = Doctor.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Smith")
                .specialization("Cardiology")
                .availabilities(new ArrayList<>())
                .build();
        
        appointmentDTO = AppointmentDTO.builder()
                .patientId(1L)
                .doctorId(1L)
                .appointmentDateTime(LocalDateTime.now().plusDays(1))
                .reason("Checkup")
                .build();
    }
    
    @Test
    void testBookAppointmentSuccess() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(appointmentRepository.findPatientAppointmentsByDateRange(any(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(appointmentRepository.findDoctorAppointmentsByDateRange(any(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(appointmentRepository.countDailyAppointments(any(), any(), any())).thenReturn(0L);
        
        Appointment savedAppointment = Appointment.builder()
                .id(1L)
                .patient(testPatient)
                .doctor(testDoctor)
                .appointmentDateTime(appointmentDTO.getAppointmentDateTime())
                .status(Appointment.AppointmentStatus.SCHEDULED)
                .build();
        
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);
        
        AppointmentDTO result = appointmentService.bookAppointment(appointmentDTO);
        
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("SCHEDULED", result.getStatus());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }
    
    @Test
    void testBookAppointmentPatientNotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, 
                () -> appointmentService.bookAppointment(appointmentDTO));
    }
    
    @Test
    void testBookAppointmentDoctorNotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, 
                () -> appointmentService.bookAppointment(appointmentDTO));
    }
    
    @Test
    void testBookAppointmentInPast() {
        appointmentDTO.setAppointmentDateTime(LocalDateTime.now().minusDays(1));
        
        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        
        assertThrows(AppointmentBookingException.class, 
                () -> appointmentService.bookAppointment(appointmentDTO));
    }
    
    @Test
    void testCancelAppointment() {
        Appointment existingAppointment = Appointment.builder()
                .id(1L)
                .status(Appointment.AppointmentStatus.SCHEDULED)
                .build();
        
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(existingAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(existingAppointment);
        
        AppointmentDTO result = appointmentService.cancelAppointment(1L);
        
        assertNotNull(result);
        assertEquals("CANCELLED", result.getStatus());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }
}
