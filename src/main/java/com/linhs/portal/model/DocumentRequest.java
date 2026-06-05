package com.linhs.portal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "document_requests")
public class DocumentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_lrn", nullable = false)
    private String studentLrn;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(name = "document_type", nullable = false)
    private String documentType; // e.g., "FORM_137", "GOOD_MORAL", "DIPLOMA"

    @Column(name = "status", nullable = false)
    private String status = "PENDING"; // PENDING, READY, CLAIMED

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    public DocumentRequest() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStudentLrn() { return studentLrn; }
    public void setStudentLrn(String studentLrn) { this.studentLrn = studentLrn; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

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

    public void setContactNumber(String contactNumber) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setContactNumber'");
    }

    public void setAcademicYear(String academicYear) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setAcademicYear'");
    }

    public void setGradeSection(String gradeSection) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setGradeSection'");
    }

    public void setPurpose(String purpose) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setPurpose'");
    }
}