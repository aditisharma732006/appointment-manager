package com.project.appointmentmanager.controller;

import com.project.appointmentmanager.dto.response.AppointmentResponseDTO;
import com.project.appointmentmanager.dto.response.ProviderResponseDTO;
import com.project.appointmentmanager.dto.response.SlotResponseDTO;
import com.project.appointmentmanager.entity.Category;
import com.project.appointmentmanager.service.ProviderService;
import com.project.appointmentmanager.service.SlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProviderController {

    @Autowired private ProviderService providerService;
    @Autowired private SlotService slotService;

    // GET /providers?category=MEDICAL
    @GetMapping("/providers")
    public ResponseEntity<List<ProviderResponseDTO>> getProviders(
            @RequestParam Category category) {
        return ResponseEntity.ok(providerService.getProvidersByCategory(category));
    }

    // GET /providers/{id}/slots → patient sees available slots
    @GetMapping("/providers/{id}/slots")
    public ResponseEntity<List<SlotResponseDTO>> getSlots(@PathVariable Long id) {
        return ResponseEntity.ok(slotService.getAvailableSlots(id));
    }

    // GET /provider/dashboard → provider sees booked appointments
    @GetMapping("/provider/dashboard")
    public ResponseEntity<List<AppointmentResponseDTO>> getDashboard(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(providerService.getProviderDashboard(email));
    }

    // GET /provider/slots → provider sees all slots to manage availability
    @GetMapping("/provider/slots")
    public ResponseEntity<List<SlotResponseDTO>> getProviderSlots(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(providerService.getProviderSlots(email));
    }
}