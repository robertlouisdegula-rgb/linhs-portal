package com.linhs.portal.repository;

import com.linhs.portal.model.GuidanceLog; // <-- This is the missing import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GuidanceLogRepository extends JpaRepository<GuidanceLog, Long> {
    List<GuidanceLog> findByLrn(String lrn);
    List<GuidanceLog> findByLrnAndStatus(String lrn, String status);
}