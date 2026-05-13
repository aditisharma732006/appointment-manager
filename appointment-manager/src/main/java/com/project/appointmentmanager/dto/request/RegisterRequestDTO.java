package com.project.appointmentmanager.dto.request;

import com.project.appointmentmanager.entity.Role;
import com.project.appointmentmanager.entity.Category;
import lombok.Data;

@Data   // generates getters, setters, toString automatically
public class RegisterRequestDTO {
    private String name;
    private String email;
    private String password;
    private Role role;              // USER or PROVIDER

    // only filled if role = PROVIDER
    private Category category;
    private String description;
    private String location;
}