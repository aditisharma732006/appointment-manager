package com.project.appointmentmanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor   // generates constructor with all fields
public class LoginResponseDTO {
    private String token;
    private String role;
}