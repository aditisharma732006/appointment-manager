package com.project.appointmentmanager.controller;

import com.project.appointmentmanager.dto.response.SlotResponseDTO;
import com.project.appointmentmanager.entity.SlotStatus;
import com.project.appointmentmanager.service.SlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SlotController {

    @Autowired private SlotService slotService;

    // PUT /slots/{id} → provider marks slot available/unavailable
    @PutMapping("/slots/{id}")
    public ResponseEntity<SlotResponseDTO> updateSlot(
            @PathVariable Long id,
            @RequestParam SlotStatus status) {
        return ResponseEntity.ok(slotService.updateSlotStatus(id, status));
    }
}