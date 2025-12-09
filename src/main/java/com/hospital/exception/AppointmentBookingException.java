package com.hospital.exception;

/**
 * Custom exception for invalid appointment booking scenarios
 */
public class AppointmentBookingException extends RuntimeException {
    public AppointmentBookingException(String message) {
        super(message);
    }
    
    public AppointmentBookingException(String message, Throwable cause) {
        super(message, cause);
    }
}
