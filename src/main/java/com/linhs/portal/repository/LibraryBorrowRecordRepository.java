package com.linhs.portal.repository;

import com.linhs.portal.model.LibraryBorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibraryBorrowRecordRepository extends JpaRepository<LibraryBorrowRecord, Long> {
    
    // Existing methods you probably already have
    List<LibraryBorrowRecord> findByStatusOrderByBorrowedAtDesc(String status);
    void deleteByStatus(String status);

    // 🟢 ADD THIS NEW LINE HERE:
    List<LibraryBorrowRecord> findByStudentLrnAndStatus(String studentLrn, String status);
}