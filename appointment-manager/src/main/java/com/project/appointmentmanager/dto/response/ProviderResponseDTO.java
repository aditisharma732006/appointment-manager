package com.project.appointmentmanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProviderResponseDTO {
    private Long id;
    private String name;
    private String category;
    private String description;
    private String location;
}