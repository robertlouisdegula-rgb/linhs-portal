package com.linhs.portal.repository;

import com.linhs.portal.model.SportsEquipment; // <--- THIS LINE IS CRITICAL
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SportsEquipmentRepository extends JpaRepository<SportsEquipment, Long> {
    List<SportsEquipment> findByStudentLrnAndStatus(String studentLrn, String status);

    Optional<SportsEquipment> findByEquipmentName(String equipmentName);
}