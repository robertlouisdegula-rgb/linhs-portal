package com.linhs.portal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "document_requests")
public class DocumentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_lrn")
    private String studentLrn;

    @Column(name = "student_name")
    private String studentName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    // CHANGED: Replaced contactNumber with emailAddress
    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "academic_year")
    private String academicYear;

    @Column(name = "grade_section")
    private String gradeSection;

    @Column(name = "document_type", nullable = false)
    private String documentType;

    @Column(name = "purpose")
    private String purpose;

    @Column(name = "status", nullable = false)
    private String status = "PENDING";

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    public DocumentRequest() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStudentLrn() { return studentLrn; }
    public void setStudentLrn(String studentLrn) { this.studentLrn = studentLrn; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public String getGradeSection() { return gradeSection; }
    public void setGradeSection(String gradeSection) { this.gradeSection = gradeSection; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }

    // --- ALIAS METHODS TO SATISFY THE CONTROLLER ---
    public LocalDateTime getLoggedAt() {
        return requestedAt;
    }

    public void setLoggedAt(LocalDateTime loggedAt) {
        this.requestedAt = loggedAt;
    }

    public String getDocumentDetails() {
        return documentType;
    }

    public void setDocumentDetails(String documentDetails) {
        this.documentType = documentDetails;
    }
}