package com.linhs.portal.model;

import jakarta.persistence.*;

@Entity
public class StudentGrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String studentLrn;
    private Long subjectId;
    private String subjectName;
    private String gradeValue;
    private Double finalGrade;
    private String remarks;

    public StudentGrade() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStudentLrn() { return studentLrn; }
    public void setStudentLrn(String studentLrn) { this.studentLrn = studentLrn; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getGradeValue() { return gradeValue; }
    public void setGradeValue(String gradeValue) { this.gradeValue = gradeValue; }

    // 🛡️ THE FIX: Added this getter so the HTML Thymeleaf ${grade.grade} doesn't crash!
    public String getGrade() { return gradeValue; }

    public Double getFinalGrade() { 
        return finalGrade; 
    }

    public void setFinalGrade(Double finalGrade) { 
        this.finalGrade = finalGrade; 
    }

    // This alias method allows your Controller to pass a String grade, 
    // cleanly parsing it to a Double before saving!
    public void setGrade(String gradeStr) {
        this.gradeValue = gradeStr;
        if (gradeStr == null || gradeStr.trim().isEmpty()) {
            this.finalGrade = null;
        } else {
            try {
                this.finalGrade = Double.parseDouble(gradeStr.trim());
            } catch (NumberFormatException e) {
                this.finalGrade = 0.0; // Fallback value if text is unparseable
            }
        }
    }

    public String getRemarks() { 
        return remarks; 
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}