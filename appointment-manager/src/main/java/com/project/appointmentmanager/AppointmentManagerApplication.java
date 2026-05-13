package com.project.appointmentmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling          // ← add this
public class AppointmentManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppointmentManagerApplication.class, args);
    }
}