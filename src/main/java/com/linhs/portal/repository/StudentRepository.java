package com.linhs.portal.repository;

import com.linhs.portal.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    
    // Retained for the Adviser Dashboard to fetch class lists safely
    List<Student> findBySection(String section);
    
    // Cross-checks the library live query against both LRN or student names automatically
    List<Student> findByLrnContainingOrNameContainingIgnoreCase(String lrn, String name);

    // 🔥 FIX 1: Added to resolve the .findByLrn() redline
    // Note: If your PageController expects an Optional, keep it as Optional<Student>.
    // If your PageController assigns it directly (e.g., Student s = ...), change this to: Student findByLrn(String lrn);
    Optional<Student> findByLrn(String lrn);
    
    // 🔥 FIX 2: Added to resolve the .findAllByOrderByNameAsc() redline
    List<Student> findAllByOrderByNameAsc();
}