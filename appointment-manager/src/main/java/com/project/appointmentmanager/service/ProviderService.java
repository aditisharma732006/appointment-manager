package com.project.appointmentmanager.service;

import com.project.appointmentmanager.dto.response.AppointmentResponseDTO;
import com.project.appointmentmanager.dto.response.ProviderResponseDTO;
import com.project.appointmentmanager.dto.response.SlotResponseDTO;
import com.project.appointmentmanager.entity.*;
import com.project.appointmentmanager.exception.UserNotFoundException;
import com.project.appointmentmanager.repository.AppointmentRepository;
import com.project.appointmentmanager.repository.ProviderRepository;
import com.project.appointmentmanager.repository.SlotRepository;
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
    @Autowired private SlotRepository slotRepository;

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

    // GET /provider/dashboard → provider sees all their booked appointments
    public List<AppointmentResponseDTO> getProviderDashboard(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        ServiceProvider provider = user.getServiceProvider();

        return provider.getTimeSlots()
                .stream()
                .filter(slot -> slot.getStatus() == SlotStatus.BOOKED)
                .map(slot -> new AppointmentResponseDTO(
                        slot.getAppointment().getId(),
                        slot.getAppointment().getBookedAt(),
                        slot.getDateTime(),
                        slot.getAppointment().getUser().getName(),
                        slot.getServiceProvider().getCategory().name(),
                        slot.getServiceProvider().getLocation()
                ))
                .collect(Collectors.toList());
    }

    // GET /provider/slots → provider sees all their slots to manage availability
    public List<SlotResponseDTO> getProviderSlots(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        ServiceProvider provider = user.getServiceProvider();

        return slotRepository.findByServiceProvider(provider)
                .stream()
                .map(slot -> new SlotResponseDTO(
                        slot.getId(),
                        slot.getDateTime(),
                        slot.getStatus().name()
                ))
                .collect(Collectors.toList());
    }
}