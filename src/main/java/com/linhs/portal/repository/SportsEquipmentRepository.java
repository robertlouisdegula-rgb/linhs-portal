package com.linhs.portal.repository;

import com.linhs.portal.model.SportsEquipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SportsEquipmentRepository extends JpaRepository<SportsEquipment, Long> {
    // Fixed: Added generic type <SportsEquipment> to the return List
    List<com.linhs.portal.controller.PageController.SportsEquipment> findByStudentLrnAndStatus(String studentLrn, String status);
}