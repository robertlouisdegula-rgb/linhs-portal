package com.linhs.portal.repository;

import com.linhs.portal.model.GuidanceRecord; // <--- THIS LINE IS CRITICAL
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GuidanceRecordRepository extends JpaRepository<GuidanceRecord, Long> {
    List<GuidanceRecord> findByStudentLrn(String studentLrn);
    List<GuidanceRecord> findByStudentLrnAndStatus(String lrn, String status);
}