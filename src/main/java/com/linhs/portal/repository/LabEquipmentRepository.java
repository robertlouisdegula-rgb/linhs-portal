package com.linhs.portal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.linhs.portal.model.LabEquipment;

public interface LabEquipmentRepository extends JpaRepository<LabEquipment, Long> {
}