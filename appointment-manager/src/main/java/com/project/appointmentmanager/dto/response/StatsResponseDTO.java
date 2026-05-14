package com.project.appointmentmanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatsResponseDTO {
    private long totalProviders;
    private long totalUsers;
    private long totalAppointments;
    private long todayBookings;
}