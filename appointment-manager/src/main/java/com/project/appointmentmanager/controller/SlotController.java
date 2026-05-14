package com.project.appointmentmanager.controller;

import com.project.appointmentmanager.dto.response.SlotResponseDTO;
import com.project.appointmentmanager.service.SlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class SlotController {

    @Autowired private SlotService slotService;

    // PUT /slots/{id} → provider toggles AVAILABLE ↔ UNAVAILABLE
    @PutMapping("/slots/{id}")
    public ResponseEntity<SlotResponseDTO> toggleSlot(
            @PathVariable Long id,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(slotService.toggleSlotStatus(id, email));
    }
}