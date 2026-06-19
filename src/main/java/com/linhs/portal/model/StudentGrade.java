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
    
    // New Semester Structure
    private Double sem1;
    private Double sem2;
    private Double sem3;
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

    public Double getSem1() { return sem1; }
    public void setSem1(Double sem1) { this.sem1 = sem1; }

    public Double getSem2() { return sem2; }
    public void setSem2(Double sem2) { this.sem2 = sem2; }

    public Double getSem3() { return sem3; }
    public void setSem3(Double sem3) { this.sem3 = sem3; }

    public Double getFinalGrade() { return finalGrade; }
    public void setFinalGrade(Double finalGrade) { this.finalGrade = finalGrade; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    // 🛡️ Helper methods to safely display N/A in HTML if the grade hasn't been encoded yet
    public String getSem1Display() { return sem1 != null ? String.valueOf(sem1) : "N/A"; }
    public String getSem2Display() { return sem2 != null ? String.valueOf(sem2) : "N/A"; }
    public String getSem3Display() { return sem3 != null ? String.valueOf(sem3) : "N/A"; }
    public String getFinalGradeDisplay() { return finalGrade != null ? String.format("%.2f", finalGrade) : "N/A"; }
}