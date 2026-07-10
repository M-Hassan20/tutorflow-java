package com.tutorflow.tutorservice.repository;

import com.tutorflow.tutorservice.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByAssignmentIdOrderBySubmittedAtDesc(Long assignmentId);
    List<Submission> findByStudentId(Long studentId);
    List<Submission> findByAssignmentIdAndStudentIdOrderByAttemptNumberDesc(
            Long assignmentId, Long studentId);

    @Query("SELECT MAX(s.attemptNumber) FROM Submission s " +
            "WHERE s.assignmentId = :assignmentId AND s.studentId = :studentId")
    Optional<Integer> findMaxAttemptNumber(
            @Param("assignmentId") Long assignmentId,
            @Param("studentId") Long studentId);

    List<Submission> findByFlaggedTrue();
    List<Submission> findByAssignmentIdAndFlaggedTrue(Long assignmentId);
}