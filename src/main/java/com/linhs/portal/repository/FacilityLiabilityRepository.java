package com.linhs.portal.repository;

import com.linhs.portal.model.FacilityLiability;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FacilityLiabilityRepository extends JpaRepository<FacilityLiability, Long> {
    List<FacilityLiability> findByStatus(String status);
    List<FacilityLiability> findByStudentLrn(String studentLrn);
    List<FacilityLiability> findByStudentLrnAndStatus(String studentLrn, String status);
}