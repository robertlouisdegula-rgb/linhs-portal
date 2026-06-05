package com.linhs.portal.repository;

import com.linhs.portal.model.DocumentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DocumentRequestRepository extends JpaRepository<DocumentRequest, Long> {
    List<DocumentRequest> findAllByOrderByRequestedAtDesc();
    List<DocumentRequest> findByStudentLrn(String studentLrn);
    <S> void save(Iterable<S> newRequest);
}