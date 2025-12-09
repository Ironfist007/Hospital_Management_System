package com.hospital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Hospital Management System - Main Application Class
 * This is a full-stack backend application built using Spring Boot to manage
 * patient records, doctor schedules, and appointments for a hospital or clinic.
 */
@SpringBootApplication
@EnableScheduling
@EnableCaching
public class HospitalManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(HospitalManagementApplication.class, args);
    }
}
