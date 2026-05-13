package com.project.appointmentmanager.controller;

import com.project.appointmentmanager.entity.Appointment;
import com.project.appointmentmanager.entity.ServiceProvider;
import com.project.appointmentmanager.entity.User;
import com.project.appointmentmanager.repository.AppointmentRepository;
import com.project.appointmentmanager.repository.ProviderRepository;
import com.project.appointmentmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserRepository userRepository;
    @Autowired private ProviderRepository providerRepository;
    @Autowired private AppointmentRepository appointmentRepository;

    // GET /admin/users
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // GET /admin/providers
    @GetMapping("/providers")
    public ResponseEntity<List<ServiceProvider>> getAllProviders() {
        return ResponseEntity.ok(providerRepository.findAll());
    }

    // GET /admin/appointments
    @GetMapping("/appointments")
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return ResponseEntity.ok(appointmentRepository.findAll());
    }
}