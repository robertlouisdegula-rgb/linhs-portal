package com.linhs.portal.repository;

import com.linhs.portal.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, String> {
    List<Student> findBySection(String section);

    List<Student> findAllByOrderByNameAsc();

    // --- BRIDGE METHOD ---
    // Redirects studentRepository.findByLrn(lrn) calls to the built-in findById()
    @SuppressWarnings("null")
    default Optional<Student> findByLrn(String lrn) {
        return findById(lrn);
    }
}