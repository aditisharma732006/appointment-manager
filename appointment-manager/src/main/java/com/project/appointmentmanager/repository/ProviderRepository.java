package com.project.appointmentmanager.repository;

import com.project.appointmentmanager.entity.ServiceProvider;
import com.project.appointmentmanager.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProviderRepository extends JpaRepository<ServiceProvider, Long> {

    List<ServiceProvider> findByCategory(Category category);
}