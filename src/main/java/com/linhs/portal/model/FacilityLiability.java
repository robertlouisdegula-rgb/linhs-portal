package com.linhs.portal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class FacilityLiability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentLrn;
    private String studentName;
    private String facilityOrItem;
    private String description;
    private String status;
    private LocalDateTime reportedAt;

    public FacilityLiability() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStudentLrn() { return studentLrn; }
    public void setStudentLrn(String studentLrn) { this.studentLrn = studentLrn; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getFacilityOrItem() { return facilityOrItem; }
    public void setFacilityOrItem(String facilityOrItem) { this.facilityOrItem = facilityOrItem; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getReportedAt() { return reportedAt; }
    public void setReportedAt(LocalDateTime reportedAt) { this.reportedAt = reportedAt; }

    // =========================================================
    // --- BRIDGE METHODS TO MATCH CONTROLLER EXPECTATIONS ---
    // =========================================================
    public String getFacilityName() { return facilityOrItem; }
    public void setFacilityName(String facilityName) { this.facilityOrItem = facilityName; }

    public String getDamageDescription() { return description; }
    public void setDamageDescription(String damageDescription) { this.description = damageDescription; }

    public LocalDateTime getReportedDate() { return reportedAt; }
    public void setReportedDate(LocalDateTime reportedDate) { this.reportedAt = reportedDate; }
}