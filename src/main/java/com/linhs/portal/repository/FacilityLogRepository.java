package com.linhs.portal.repository;

import com.linhs.portal.model.FacilityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityLogRepository extends JpaRepository<FacilityLog, Long> {
    List<FacilityLog> findByLrnAndStatus(String lrn, String status); // Used later to check if a student has unsolved clearance
}