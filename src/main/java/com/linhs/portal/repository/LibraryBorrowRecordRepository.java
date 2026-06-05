package com.linhs.portal.repository;

import com.linhs.portal.model.LibraryBorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LibraryBorrowRecordRepository extends JpaRepository<LibraryBorrowRecord, Long> {
    List<LibraryBorrowRecord> findByStatus(String status);

    List<LibraryBorrowRecord> findByStudentLrn(String studentLrn);

    List<com.linhs.portal.controller.PageController.LibraryBorrowRecord> findByStudentLrnAndStatus(String studentLrn, String status);
}