package com.linhs.portal.repository;

import com.linhs.portal.model.LabLiability;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LabLiabilityRepository extends JpaRepository<LabLiability, Long> {
    List<LabLiability> findByStudentLrnAndStatus(String lrn, String status);
}