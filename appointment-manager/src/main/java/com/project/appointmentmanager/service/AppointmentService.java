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

        TimeSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new SlotNotAvailableException("Slot not found with id: " + slotId));

        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new SlotNotAvailableException("Slot is not available");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        slot.setStatus(SlotStatus.BOOKED);
        slotRepository.save(slot);

        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setTimeSlot(slot);
        appointment.setBookedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);

        return new AppointmentResponseDTO(
                appointment.getId(),
                appointment.getBookedAt(),
                slot.getDateTime(),
                slot.getServiceProvider().getName(),
                slot.getServiceProvider().getCategory().name(),
                slot.getServiceProvider().getLocation()
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
                        appointment.getTimeSlot().getServiceProvider().getName(),
                        appointment.getTimeSlot().getServiceProvider().getCategory().name(),
                        appointment.getTimeSlot().getServiceProvider().getLocation()
                ))
                .collect(Collectors.toList());
    }

    // DELETE /appointments/{id} → user cancels their appointment
    public String cancelAppointment(Long appointmentId, String email) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));

        // only the user who booked can cancel
        if (!appointment.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to cancel this appointment");
        }

        // cannot cancel past appointments
        if (appointment.getTimeSlot().getDateTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot cancel a past appointment");
        }

        // free the slot back to available
        TimeSlot slot = appointment.getTimeSlot();
        slot.setStatus(SlotStatus.AVAILABLE);
        slotRepository.save(slot);

        appointmentRepository.delete(appointment);

        return "Appointment cancelled successfully";
    }
}