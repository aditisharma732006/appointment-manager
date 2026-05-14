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

    List<TimeSlot> findByStatusAndDateTimeBefore(SlotStatus status, LocalDateTime dateTime);

    List<TimeSlot> findByServiceProviderAndDateTimeBetween(
            ServiceProvider provider,
            LocalDateTime start,
            LocalDateTime end
    );

    // for provider slot management dashboard
    List<TimeSlot> findByServiceProvider(ServiceProvider provider);

    // for admin stats - total appointments count
    long countByStatus(SlotStatus status);
}