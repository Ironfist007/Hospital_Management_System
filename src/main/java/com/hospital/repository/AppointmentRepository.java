package com.hospital.repository;

import com.hospital.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    Page<Appointment> findByPatientId(Long patientId, Pageable pageable);
    
    Page<Appointment> findByDoctorId(Long doctorId, Pageable pageable);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Appointment a WHERE a.id = :id")
    Optional<Appointment> findByIdWithLock(Long id);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId " +
           "AND a.appointmentDateTime BETWEEN :start AND :end " +
           "AND a.status != 'CANCELLED'")
    List<Appointment> findDoctorAppointmentsByDateRange(Long doctorId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId " +
           "AND a.appointmentDateTime BETWEEN :start AND :end " +
           "AND a.status != 'CANCELLED'")
    List<Appointment> findPatientAppointmentsByDateRange(Long patientId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId " +
           "AND a.appointmentDateTime >= :startOfDay " +
           "AND a.appointmentDateTime < :endOfDay " +
           "AND a.status != 'CANCELLED'")
    long countDailyAppointments(Long doctorId, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
