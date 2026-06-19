package com.linhs.portal.repository;

import com.linhs.portal.model.LibraryBorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LibraryBorrowRecordRepository extends JpaRepository<LibraryBorrowRecord, Long> {
    
    // Gets active list or history logs ordered by chronological checkout date
    List<LibraryBorrowRecord> findByStatusOrderByBorrowedAtDesc(String status);
    
    // Used inside the dashboard return method
    List<LibraryBorrowRecord> findByStudentLrnAndStatus(String studentLrn, String status);
    
    // Supporting alternative name sequence used in the student clearance tracking tip
    List<LibraryBorrowRecord> findByStatusAndStudentLrn(String status, String studentLrn);
    
    // Wipes completed transactions history on request
    void deleteByStatus(String status);
}