package com.project.appointmentmanager.service;

import com.project.appointmentmanager.dto.response.SlotResponseDTO;
import com.project.appointmentmanager.entity.*;
import com.project.appointmentmanager.exception.SlotNotAvailableException;
import com.project.appointmentmanager.repository.ProviderRepository;
import com.project.appointmentmanager.repository.SlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SlotService {

    @Autowired private SlotRepository slotRepository;
    @Autowired private ProviderRepository providerRepository;

    // called at registration + by scheduler every midnight
    public void generateSlotsForProvider(ServiceProvider provider) {
        LocalDate today = LocalDate.now();

        for (int i = 0; i < 5; i++) {
            LocalDate date = today.plusDays(i);

            List<TimeSlot> existing = slotRepository.findByServiceProviderAndDateTimeBetween(
                    provider,
                    LocalDateTime.of(date, LocalTime.MIN),
                    LocalDateTime.of(date, LocalTime.MAX)
            );

            if (!existing.isEmpty()) continue;

            for (int hour = 9; hour <= 17; hour++) {
                TimeSlot slot = new TimeSlot();
                slot.setDateTime(LocalDateTime.of(date, LocalTime.of(hour, 0)));
                slot.setStatus(SlotStatus.AVAILABLE);
                slot.setServiceProvider(provider);
                slotRepository.save(slot);
            }
        }
    }

    // called by scheduler every midnight
    public void expirePastSlots() {
        List<TimeSlot> pastSlots = slotRepository.findByStatusAndDateTimeBefore(
                SlotStatus.AVAILABLE,
                LocalDateTime.now()
        );
        pastSlots.forEach(slot -> slot.setStatus(SlotStatus.EXPIRED));
        slotRepository.saveAll(pastSlots);
    }

    // GET /providers/{id}/slots → patients see only AVAILABLE slots
    public List<SlotResponseDTO> getAvailableSlots(Long providerId) {
        ServiceProvider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new SlotNotAvailableException("Provider not found"));

        List<TimeSlot> slots = slotRepository.findByServiceProviderAndStatusAndDateTimeBetween(
                provider,
                SlotStatus.AVAILABLE,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(5)
        );

        return slots.stream()
                .map(slot -> new SlotResponseDTO(
                        slot.getId(),
                        slot.getDateTime(),
                        slot.getStatus().name()
                ))
                .collect(Collectors.toList());
    }

    // PUT /slots/{id} → provider toggles AVAILABLE ↔ UNAVAILABLE
    public SlotResponseDTO toggleSlotStatus(Long slotId, String email) {
        TimeSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new SlotNotAvailableException("Slot not found with id: " + slotId));

        // verify slot belongs to this provider
        if (!slot.getServiceProvider().getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to modify this slot");
        }

        // cannot modify booked or expired slots
        if (slot.getStatus() == SlotStatus.BOOKED || slot.getStatus() == SlotStatus.EXPIRED) {
            throw new RuntimeException("Cannot modify a BOOKED or EXPIRED slot");
        }

        // toggle
        if (slot.getStatus() == SlotStatus.AVAILABLE) {
            slot.setStatus(SlotStatus.UNAVAILABLE);
        } else {
            slot.setStatus(SlotStatus.AVAILABLE);
        }

        slotRepository.save(slot);
        return new SlotResponseDTO(slot.getId(), slot.getDateTime(), slot.getStatus().name());
    }
}