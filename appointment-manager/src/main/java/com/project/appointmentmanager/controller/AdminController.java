package com.project.appointmentmanager.controller;

import com.project.appointmentmanager.dto.response.ProviderResponseDTO;
import com.project.appointmentmanager.dto.response.StatsResponseDTO;
import com.project.appointmentmanager.entity.ServiceProvider;
import com.project.appointmentmanager.entity.SlotStatus;
import com.project.appointmentmanager.repository.AppointmentRepository;
import com.project.appointmentmanager.repository.ProviderRepository;
import com.project.appointmentmanager.repository.UserRepository;
import com.project.appointmentmanager.repository.SlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserRepository userRepository;
    @Autowired private ProviderRepository providerRepository;
    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private SlotRepository slotRepository;

    // GET /admin/providers
    @GetMapping("/providers")
    public ResponseEntity<List<ProviderResponseDTO>> getAllProviders() {
        List<ProviderResponseDTO> providers = providerRepository.findAll()
                .stream()
                .map(p -> new ProviderResponseDTO(
                        p.getId(),
                        p.getName(),
                        p.getCategory().name(),
                        p.getDescription(),
                        p.getLocation()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(providers);
    }

    // DELETE /admin/providers/{id}
    @DeleteMapping("/providers/{id}")
    public ResponseEntity<String> deleteProvider(@PathVariable Long id) {
        ServiceProvider provider = providerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Provider not found with id: " + id));

        // delete all slots for this provider first
        slotRepository.deleteAll(slotRepository.findByServiceProvider(provider));

        // then delete user → cascade deletes provider
        userRepository.delete(provider.getUser());

        return ResponseEntity.ok("Provider removed successfully");
    }

    // GET /admin/stats
    @GetMapping("/stats")
    public ResponseEntity<StatsResponseDTO> getStats() {
        long totalProviders = providerRepository.count();
        long totalUsers = userRepository.count();
        long totalAppointments = appointmentRepository.count();

        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        long todayBookings = appointmentRepository.countByBookedAtBetween(startOfDay, endOfDay);

        return ResponseEntity.ok(new StatsResponseDTO(
                totalProviders,
                totalUsers,
                totalAppointments,
                todayBookings
        ));
    }
}