package com.project.appointmentmanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SlotResponseDTO {
    private Long id;
    private LocalDateTime dateTime;
    private String status;
}