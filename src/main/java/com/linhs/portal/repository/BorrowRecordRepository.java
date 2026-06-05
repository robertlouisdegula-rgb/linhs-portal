package com.linhs.portal.repository;

import com.linhs.portal.model.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    List<BorrowRecord> findByStatus(String status);
    List<BorrowRecord> findByStudentLrn(String studentLrn);
    List<BorrowRecord> findByStudentLrnAndStatus(String studentLrn, String status);
}