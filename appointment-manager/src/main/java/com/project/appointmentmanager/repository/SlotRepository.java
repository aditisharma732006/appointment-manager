package com.project.appointmentmanager.repository;

import com.project.appointmentmanager.entity.TimeSlot;
import com.project.appointmentmanager.entity.SlotStatus;
import com.project.appointmentmanager.entity.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SlotRepository extends JpaRepository<TimeSlot, Long> {

    List<TimeSlot> findByServiceProviderAndStatusAndDateTimeBetween(
            ServiceProvider provider,
            SlotStatus status,
            LocalDateTime start,
            LocalDateTime end
    );
//    fetch available slots for a provider in next 5 days
//    SELECT * FROM time_slots
//    WHERE provider_id = ?
//    AND status = ?
//    AND date_time BETWEEN ? AND ?

    List<TimeSlot> findByStatusAndDateTimeBefore(SlotStatus status, LocalDateTime dateTime);
//    used by scheduler to find expired slots
//Scheduler runs every midnight. It needs to find all slots that:
//    Are still marked AVAILABLE
//    But their dateTime has already passed
//    SELECT * FROM time_slots
//    WHERE status = ?
//    AND date_time < ?

    List<TimeSlot> findByServiceProviderAndDateTimeBetween(
            ServiceProvider provider,
            LocalDateTime start,
            LocalDateTime end
    );
    //Scheduler generates new slots every day. But before generating —
    // it checks if slots already exist for that day to avoid duplicates.
//    SELECT * FROM time_slots
//    WHERE provider_id = ?
//    AND date_time BETWEEN ? AND ?
}