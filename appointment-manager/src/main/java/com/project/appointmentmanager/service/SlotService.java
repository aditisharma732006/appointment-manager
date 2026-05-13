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

        // generate for next 5 days
        for (int i = 0; i < 5; i++) {
            LocalDate date = today.plusDays(i);

            // check if slots already exist for this day → avoid duplicates
            List<TimeSlot> existing = slotRepository.findByServiceProviderAndDateTimeBetween(
                    provider,
                    LocalDateTime.of(date, LocalTime.MIN),
                    LocalDateTime.of(date, LocalTime.MAX)
            );

            if (!existing.isEmpty()) continue;  // slots exist → skip

            // 9am to 5pm, one slot per hour
            for (int hour = 9; hour <= 17; hour++) {
                TimeSlot slot = new TimeSlot();
                slot.setDateTime(LocalDateTime.of(date, LocalTime.of(hour, 0)));
                slot.setStatus(SlotStatus.AVAILABLE);
                slot.setServiceProvider(provider);
                slotRepository.save(slot);
            }
        }
    }

    // called by scheduler every midnight to expire past slots
    public void expirePastSlots() {
        List<TimeSlot> pastSlots = slotRepository.findByStatusAndDateTimeBefore(
                SlotStatus.AVAILABLE,
                LocalDateTime.now()
        );
        pastSlots.forEach(slot -> slot.setStatus(SlotStatus.EXPIRED));
        slotRepository.saveAll(pastSlots);
    }

    // GET /providers/{id}/slots → available slots for next 5 days
    public List<SlotResponseDTO> getAvailableSlots(Long providerId) {
        ServiceProvider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new SlotNotAvailableException("Provider not found"));

        List<TimeSlot> slots = slotRepository.findByServiceProviderAndStatusAndDateTimeBetween(
                provider,
                SlotStatus.AVAILABLE,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(5)
        );

        // convert entity → DTO
        return slots.stream()
                .map(slot -> new SlotResponseDTO(
                        slot.getId(),
                        slot.getDateTime(),
                        slot.getStatus().name()
                ))
                .collect(Collectors.toList());
    }

    // PUT /slots/{id} → provider marks slot available or unavailable
    public SlotResponseDTO updateSlotStatus(Long slotId, SlotStatus newStatus) {
        TimeSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new SlotNotAvailableException("Slot not found with id: " + slotId));

        slot.setStatus(newStatus);
        slotRepository.save(slot);

        return new SlotResponseDTO(slot.getId(), slot.getDateTime(), slot.getStatus().name());
    }
}