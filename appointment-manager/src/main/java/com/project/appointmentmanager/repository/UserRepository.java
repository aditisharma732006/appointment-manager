package com.project.appointmentmanager.repository;

import com.project.appointmentmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
//tells Spring this is Layer 3
//User is the entity, Long is the type of its @Id
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
//    SELECT * FROM users WHERE email = ?
}