package com.linhs.portal.repository;

import com.linhs.portal.model.StudentGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentGradeRepository extends JpaRepository<StudentGrade, Long> {
    // As long as this method is here, the new controller logic will work perfectly!
    List<StudentGrade> findByStudentLrn(String studentLrn);
}