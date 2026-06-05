package com.linhs.portal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class SportsEquipment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentLrn;
    private String equipmentName;
    private Integer quantity;
    private LocalDateTime borrowDate;
    private String status;

    public SportsEquipment() {}

    // --- GETTERS AND SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStudentLrn() { return studentLrn; }
    public void setStudentLrn(String studentLrn) { this.studentLrn = studentLrn; }

    public String getEquipmentName() { return equipmentName; }
    public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public LocalDateTime getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDateTime borrowDate) { this.borrowDate = borrowDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}