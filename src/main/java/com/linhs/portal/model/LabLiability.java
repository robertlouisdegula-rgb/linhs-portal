package com.linhs.portal.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "lab_liabilities")
public class LabLiability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String studentLrn;
    private String studentName;
    private String description;
    private String status; // "UNPAID" or "CLEARED"
    private String dateLogged;

    @PrePersist
    protected void onCreate() {
        if (this.dateLogged == null) {
            this.dateLogged = LocalDate.now().toString();
        }
        if (this.status == null) {
            this.status = "UNPAID";
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStudentLrn() { return studentLrn; }
    public void setStudentLrn(String studentLrn) { this.studentLrn = studentLrn; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDateLogged() { return dateLogged; }
    public void setDateLogged(String dateLogged) { this.dateLogged = dateLogged; }
}