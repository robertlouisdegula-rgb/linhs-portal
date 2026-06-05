package com.linhs.portal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class GuidanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentLrn;
    private String studentName;
    private String incidentDetails;
    private String actionTaken;
    private LocalDateTime createdAt;

    public GuidanceRecord() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStudentLrn() { return studentLrn; }
    public void setStudentLrn(String studentLrn) { this.studentLrn = studentLrn; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getIncidentDetails() { return incidentDetails; }
    public void setIncidentDetails(String incidentDetails) { this.incidentDetails = incidentDetails; }

    public String getActionTaken() { return actionTaken; }
    public void setActionTaken(String actionTaken) { this.actionTaken = actionTaken; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // --- ALIAS METHODS TO SATISFY THE CONTROLLER ---
    
    public LocalDateTime getLoggedAt() { 
        return createdAt; 
    }
    
    public void setLoggedAt(LocalDateTime loggedAt) { 
        this.createdAt = loggedAt; 
    }

    public String getInfractionDescription() {
        return this.incidentDetails;
    }

    public LocalDateTime getLogDate() {
        return this.createdAt;
    }
}