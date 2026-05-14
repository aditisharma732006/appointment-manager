package com.project.appointmentmanager.controller;

import com.project.appointmentmanager.dto.response.AppointmentResponseDTO;
import com.project.appointmentmanager.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AppointmentController {

    @Autowired private AppointmentService appointmentService;

    // GET /appointments → patient sees their appointments
    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentResponseDTO>> getUserAppointments(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(appointmentService.getUserAppointments(email));
    }

    // POST /appointments/book/{slotId}
    @PostMapping("/appointments/book/{slotId}")
    public ResponseEntity<AppointmentResponseDTO> bookAppointment(
            @PathVariable Long slotId,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(appointmentService.bookAppointment(slotId, email));
    }

    // DELETE /appointments/{id} → user cancels their appointment
    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<String> cancelAppointment(
            @PathVariable Long id,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id, email));
    }
}