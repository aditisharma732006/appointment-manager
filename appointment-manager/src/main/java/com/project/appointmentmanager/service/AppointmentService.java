package com.project.appointmentmanager.service;

import com.project.appointmentmanager.dto.response.AppointmentResponseDTO;
import com.project.appointmentmanager.entity.*;
import com.project.appointmentmanager.exception.SlotNotAvailableException;
import com.project.appointmentmanager.exception.UserNotFoundException;
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

        // find the slot
        TimeSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new SlotNotAvailableException("Slot not found with id: " + slotId));

        // check slot is still available
        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new SlotNotAvailableException("Slot is already booked or expired");
        }

        // find user by email — email comes from JWT token via @AuthenticationPrincipal
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        // mark slot as booked
        slot.setStatus(SlotStatus.BOOKED);
        slotRepository.save(slot);

        // create and save appointment
        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setTimeSlot(slot);
        appointment.setBookedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);

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
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

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