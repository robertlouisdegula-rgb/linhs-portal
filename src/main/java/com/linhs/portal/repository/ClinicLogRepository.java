package com.linhs.portal.repository;

import com.linhs.portal.model.ClinicLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClinicLogRepository extends JpaRepository<ClinicLog, Long> {
    
    // Kept as an available helper method in case you want chronological descending logs later
    List<ClinicLog> findAllByOrderByLoggedAtDesc();
}