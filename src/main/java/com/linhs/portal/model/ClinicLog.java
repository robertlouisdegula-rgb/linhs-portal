package com.linhs.portal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clinic_logs")
public class ClinicLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_lrn", nullable = false)
    private String studentLrn;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "medicine_given")
    private String medicineGiven;

    @Column(name = "logged_at", nullable = false)
    private LocalDateTime loggedAt;

    // Constructors
    public ClinicLog() {}

    public ClinicLog(String studentLrn, String studentName, String reason, String medicineGiven, LocalDateTime loggedAt) {
        this.studentLrn = studentLrn;
        this.studentName = studentName;
        this.reason = reason;
        this.medicineGiven = medicineGiven;
        this.loggedAt = loggedAt;
    }

    // Standard Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStudentLrn() { return studentLrn; }
    public void setStudentLrn(String studentLrn) { this.studentLrn = studentLrn; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getMedicineGiven() { return medicineGiven; }
    public void setMedicineGiven(String medicineGiven) { this.medicineGiven = medicineGiven; }

    public LocalDateTime getLoggedAt() { return loggedAt; }
    public void setLoggedAt(LocalDateTime loggedAt) { this.loggedAt = loggedAt; }

    // =========================================================
    // --- BRIDGE ALIASES TO MATCH NURSE-DASHBOARD EXPECTATIONS ---
    // =========================================================

    public LocalDateTime getVisitDate() { 
        return this.loggedAt; 
    }
    
    public void setVisitDate(LocalDateTime visitDate) { 
        this.loggedAt = visitDate; 
    }

    public String getActionTaken() { 
        return this.medicineGiven; 
    }
    
    public void setActionTaken(String actionTaken) { 
        this.medicineGiven = actionTaken; 
    }
}