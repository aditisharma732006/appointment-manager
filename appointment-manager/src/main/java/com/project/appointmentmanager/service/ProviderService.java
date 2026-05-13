package com.project.appointmentmanager.service;

import com.project.appointmentmanager.dto.response.AppointmentResponseDTO;
import com.project.appointmentmanager.dto.response.ProviderResponseDTO;
import com.project.appointmentmanager.entity.*;
import com.project.appointmentmanager.repository.AppointmentRepository;
import com.project.appointmentmanager.repository.ProviderRepository;
import com.project.appointmentmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProviderService {

    @Autowired private ProviderRepository providerRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private AppointmentRepository appointmentRepository;

    // GET /providers?category=MEDICAL
    public List<ProviderResponseDTO> getProvidersByCategory(Category category) {
        return providerRepository.findByCategory(category)
                .stream()
                .map(p -> new ProviderResponseDTO(
                        p.getId(),
                        p.getName(),
                        p.getCategory().name(),
                        p.getDescription(),
                        p.getLocation()
                ))
                .collect(Collectors.toList());
    }

    // GET /provider/dashboard → provider sees all their appointments
    public List<AppointmentResponseDTO> getProviderDashboard(String email) {

        // find the provider profile linked to this logged in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ServiceProvider provider = user.getServiceProvider();

        // get all slots of this provider, then get appointments from those slots
        return provider.getTimeSlots()
                .stream()
                .filter(slot -> slot.getStatus() == SlotStatus.BOOKED)
                .map(slot -> slot.getAppointment())
                .map(appointment -> new AppointmentResponseDTO(
                        appointment.getId(),
                        appointment.getBookedAt(),
                        appointment.getTimeSlot().getDateTime(),
                        appointment.getUser().getName()   // patient name for provider view
                ))
                .collect(Collectors.toList());
    }
}