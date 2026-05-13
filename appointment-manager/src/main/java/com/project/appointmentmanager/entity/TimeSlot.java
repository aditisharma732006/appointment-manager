package com.project.appointmentmanager.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "time_slots")
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateTime;

    @Enumerated(EnumType.STRING)
    private SlotStatus status;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "provider_id")
    private ServiceProvider serviceProvider;

    @JsonIgnore
    @OneToOne(mappedBy = "timeSlot")
    private Appointment appointment;
}
