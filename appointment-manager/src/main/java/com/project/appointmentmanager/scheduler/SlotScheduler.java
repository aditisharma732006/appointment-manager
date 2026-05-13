package com.project.appointmentmanager.scheduler;

import com.project.appointmentmanager.entity.ServiceProvider;
import com.project.appointmentmanager.repository.ProviderRepository;
import com.project.appointmentmanager.service.SlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SlotScheduler {

    @Autowired private SlotService slotService;
    @Autowired private ProviderRepository providerRepository;

    // runs every day at midnight automatically
    // cron = "second minute hour day month weekday"
    // 0 0 0 * * * → at 00:00:00, every day, every month
    @Scheduled(cron = "0 0 0 * * *")
    public void runDailySlotMaintenance() {

        // Job 1 → expire all past slots
        slotService.expirePastSlots();

        // Job 2 → generate next day slots for every provider
        List<ServiceProvider> allProviders = providerRepository.findAll();

        for (ServiceProvider provider : allProviders) {
            // generateSlotsForProvider already checks for duplicates internally
            // so calling it here is always safe
            slotService.generateSlotsForProvider(provider);
        }
    }
}