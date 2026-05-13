package com.project.appointmentmanager.service;

import com.project.appointmentmanager.dto.response.AppointmentResponseDTO;
import com.project.appointmentmanager.entity.*;
import com.project.appointmentmanager.repository.AppointmentRepository;
import com.project.appointmentmanager.repository.SlotRepository;
import com.project.appointmentmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private SlotRepository slotRepository;
    @Autowired private UserRepository userRepository;

    // POST /appointments/book/{slotId}
    public AppointmentResponseDTO bookAppointment(Long slotId, String email) {

        // Step 1: Find the slot
        TimeSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        // Step 2: Check slot is still available
        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new RuntimeException("Slot is not available");
        }

        // Step 3: Find the user by email (email comes from JWT token)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Step 4: Mark slot as BOOKED
        slot.setStatus(SlotStatus.BOOKED);
        slotRepository.save(slot);

        // Step 5: Create and save appointment
        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setTimeSlot(slot);
        appointment.setBookedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);

        // Step 6: Return response DTO
        return new AppointmentResponseDTO(
                appointment.getId(),
                appointment.getBookedAt(),
                slot.getDateTime(),
                slot.getServiceProvider().getName()
        );
    }

    // GET /appointments → patient sees their own appointments
    public List<AppointmentResponseDTO> getUserAppointments(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return appointmentRepository.findByUser(user)
                .stream()
                .map(appointment -> new AppointmentResponseDTO(
                        appointment.getId(),
                        appointment.getBookedAt(),
                        appointment.getTimeSlot().getDateTime(),
                        appointment.getTimeSlot().getServiceProvider().getName()
                ))
                .collect(Collectors.toList());
    }
}