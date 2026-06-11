package com.linhs.portal.repository;

import com.linhs.portal.model.ClinicLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClinicLogRepository extends JpaRepository<ClinicLog, Long> {
    // Clinic logs are just historical, no "unsolved" status needed to check clearance.
}