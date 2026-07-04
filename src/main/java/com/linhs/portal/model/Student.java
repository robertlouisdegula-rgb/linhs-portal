package com.linhs.portal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @Column(name = "lrn", nullable = false, unique = true)
    private String lrn;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "section")
    private String section;

    // --- Clearance Status Fields NOW DEFAULT TO "CLEARED" ---
    @Column(name = "adviser_clearance")
    private String adviserClearance = "CLEARED";

    @Column(name = "lab_clearance")
    private String labClearance = "CLEARED";

    @Column(name = "sports_clearance")
    private String sportsClearance = "CLEARED";

    @Column(name = "guidance_clearance")
    private String guidanceClearance = "CLEARED";

    @Column(name = "facilities_clearance")
    private String facilitiesClearance = "CLEARED";

    @Column(name = "library_clearance")
    private String libraryClearance = "CLEARED";

    // 1. Default constructor (Strictly required by Spring Data JPA)
    public Student() {
    }

    // 2. Custom constructor (Required by PageController.java for the Edit Student feature)
    public Student(String lrn, String name, String section) {
        this.lrn = lrn;
        this.name = name;
        this.section = section;
    }

    // --- Basic Getters and Setters ---
    public String getLrn() {
        return lrn;
    }

    public void setLrn(String lrn) {
        this.lrn = lrn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    // --- Clearance Getters and Setters ---
    public String getAdviserClearance() {
        return adviserClearance;
    }

    public void setAdviserClearance(String adviserClearance) {
        this.adviserClearance = adviserClearance;
    }

    public String getLabClearance() {
        return labClearance;
    }

    public void setLabClearance(String labClearance) {
        this.labClearance = labClearance;
    }

    public String getSportsClearance() {
        return sportsClearance;
    }

    public void setSportsClearance(String sportsClearance) {
        this.sportsClearance = sportsClearance;
    }

    public String getGuidanceClearance() {
        return guidanceClearance;
    }

    public void setGuidanceClearance(String guidanceClearance) {
        this.guidanceClearance = guidanceClearance;
    }

    public String getFacilitiesClearance() {
        return facilitiesClearance;
    }

    public void setFacilitiesClearance(String facilitiesClearance) {
        this.facilitiesClearance = facilitiesClearance;
    }

    public String getLibraryClearance() {
        return libraryClearance;
    }

    public void setLibraryClearance(String libraryClearance) {
        this.libraryClearance = libraryClearance;
    }
}