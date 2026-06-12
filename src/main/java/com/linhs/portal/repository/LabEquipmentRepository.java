package com.linhs.portal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.linhs.portal.model.LabEquipment;

public interface LabEquipmentRepository extends JpaRepository<LabEquipment, Long> {
    Optional<LabEquipment> findByName(String name);
}