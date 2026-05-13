package com.project.appointmentmanager.repository;

import com.project.appointmentmanager.entity.Appointment;
import com.project.appointmentmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByUser(User user);
//    fetch all appointments for a specific patient
}