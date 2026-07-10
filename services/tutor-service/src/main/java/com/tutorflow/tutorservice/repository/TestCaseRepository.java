package com.tutorflow.tutorservice.repository;

import com.tutorflow.tutorservice.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
    List<TestCase> findByAssignmentIdOrderByOrderIndex(Long assignmentId);
    void deleteByAssignmentId(Long assignmentId);
}