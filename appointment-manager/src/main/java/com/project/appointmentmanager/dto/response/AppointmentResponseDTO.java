package com.project.appointmentmanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AppointmentResponseDTO {
    private Long id;
    private LocalDateTime bookedAt;
    private LocalDateTime slotDateTime;
    private String providerName;
    private String providerCategory;
    private String providerLocation;
}