package com.linhs.portal.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class FacilityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lrn;
    private String studentName;
    private String facility;
    private String description;
    private String status = "UNSOLVED";
    private String dateLogged = LocalDate.now().toString();

    public FacilityLog() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLrn() { return lrn; }
    public void setLrn(String lrn) { this.lrn = lrn; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getFacility() { return facility; }
    public void setFacility(String facility) { this.facility = facility; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDateLogged() { return dateLogged; }
    public void setDateLogged(String dateLogged) { this.dateLogged = dateLogged; }
}