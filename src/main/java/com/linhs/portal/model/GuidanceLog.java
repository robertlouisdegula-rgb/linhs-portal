package com.linhs.portal.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class GuidanceLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lrn;
    private String studentName;
    private String incident;
    private String actionTaken;
    private String status = "UNSOLVED";
    private String dateLogged = LocalDate.now().toString();

    public GuidanceLog() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLrn() { return lrn; }
    public void setLrn(String lrn) { this.lrn = lrn; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getIncident() { return incident; }
    public void setIncident(String incident) { this.incident = incident; }

    public String getActionTaken() { return actionTaken; }
    public void setActionTaken(String actionTaken) { this.actionTaken = actionTaken; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDateLogged() { return dateLogged; }
    public void setDateLogged(String dateLogged) { this.dateLogged = dateLogged; }
}