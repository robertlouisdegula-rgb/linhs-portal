package com.linhs.portal.repository;

import com.linhs.portal.model.SportsEquipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SportsEquipmentRepository extends JpaRepository<SportsEquipment, Long> {
    // Aligned to return the actual database managed Entity model
    List<SportsEquipment> findByStudentLrnAndStatus(String studentLrn, String status);
}