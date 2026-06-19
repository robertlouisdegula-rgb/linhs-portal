package com.linhs.portal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "library_borrow_records")
public class LibraryBorrowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_lrn", nullable = false)
    private String studentLrn;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(name = "book_title", nullable = false)
    private String bookTitle;

    @Column(name = "status", nullable = false)
    private String status; // Values: "ACTIVE" or "RETURNED"

    @Column(name = "borrowed_at", nullable = false)
    private LocalDateTime borrowedAt;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    // Default constructor required by JPA
    public LibraryBorrowRecord() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentLrn() {
        return studentLrn;
    }

    public void setStudentLrn(String studentLrn) {
        this.studentLrn = studentLrn;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getBorrowedAt() {
        return borrowedAt;
    }

    public void setBorrowedAt(LocalDateTime borrowedAt) {
        this.borrowedAt = borrowedAt;
    }

    public LocalDateTime getReturnedAt() {
        return returnedAt;
    }

    public void setReturnedAt(LocalDateTime returnedAt) {
        this.returnedAt = returnedAt;
    }

    // Bridge method to maintain backward compatibility with old dashboard fragments
    public LocalDateTime getBorrowDate() {
        return this.borrowedAt;
    }
}