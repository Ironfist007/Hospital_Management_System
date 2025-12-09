package com.hospital.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for sending SMS notifications using Twilio
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmsNotificationService {
    
    @Value("${twilio.account-sid}")
    private String accountSid;
    
    @Value("${twilio.auth-token}")
    private String authToken;
    
    @Value("${twilio.phone-number}")
    private String twilioPhoneNumber;
    
    /**
     * Send appointment confirmation SMS
     */
    public void sendAppointmentConfirmation(String patientPhone, String doctorName, 
                                           String appointmentDateTime) {
        try {
            log.info("Sending appointment confirmation SMS to: {}", patientPhone);
            
            Twilio.init(accountSid, authToken);
            
            String messageBody = String.format(
                    "Appointment Confirmation: Your appointment with Dr. %s is scheduled for %s. " +
                    "Please arrive 10 minutes early. Reply CONFIRM to confirm or CANCEL to cancel.",
                    doctorName, appointmentDateTime
            );
            
            Message message = Message.creator(
                    accountSid,                             // Account SID
                    new PhoneNumber(twilioPhoneNumber),      // From number
                    new PhoneNumber(patientPhone)            // To number
            )
                    .setBody(messageBody)
                    .create();
            
            log.info("SMS sent successfully with SID: {}", message.getSid());
            
        } catch (Exception e) {
            log.error("Failed to send SMS notification: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Send appointment cancellation SMS
     */
    public void sendAppointmentCancellation(String patientPhone, String doctorName) {
        try {
            log.info("Sending appointment cancellation SMS to: {}", patientPhone);
            
            Twilio.init(accountSid, authToken);
            
            String messageBody = String.format(
                    "Appointment Cancelled: Your appointment with Dr. %s has been cancelled. " +
                    "Please contact the hospital to reschedule.",
                    doctorName
            );
            
            Message message = Message.creator(
                    accountSid,                             // Account SID
                    new PhoneNumber(twilioPhoneNumber),      // From number
                    new PhoneNumber(patientPhone)            // To number
            )
                    .setBody(messageBody)
                    .create();
            
            log.info("SMS sent successfully with SID: {}", message.getSid());
            
        } catch (Exception e) {
            log.error("Failed to send SMS notification: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Send appointment reminder SMS
     */
    public void sendAppointmentReminder(String patientPhone, String doctorName, 
                                       String appointmentDateTime) {
        try {
            log.info("Sending appointment reminder SMS to: {}", patientPhone);
            
            Twilio.init(accountSid, authToken);
            
            String messageBody = String.format(
                    "Appointment Reminder: You have an appointment with Dr. %s scheduled for %s. " +
                    "Please confirm your attendance.",
                    doctorName, appointmentDateTime
            );
            
            Message message = Message.creator(
                    accountSid,                             // Account SID
                    new PhoneNumber(twilioPhoneNumber),      // From number
                    new PhoneNumber(patientPhone)            // To number
            )
                    .setBody(messageBody)
                    .create();
            
            log.info("SMS sent successfully with SID: {}", message.getSid());
            
        } catch (Exception e) {
            log.error("Failed to send SMS notification: {}", e.getMessage(), e);
        }
    }
}
