package com.tutorflow.tutorservice.repository;

import com.tutorflow.tutorservice.entity.Assignment;
import com.tutorflow.tutorservice.enums.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByClassroomId(Long classroomId);

    @Query("SELECT a FROM Assignment a WHERE a.classroomId = :classroomId " +
            "AND (a.releaseDate IS NULL OR a.releaseDate <= :now) " +
            "AND (a.targetType = :allType OR :studentId MEMBER OF a.targetStudentIds)")
    List<Assignment> findVisibleForStudent(
            @Param("classroomId") Long classroomId,
            @Param("studentId") Long studentId,
            @Param("now") LocalDateTime now,
            @Param("allType") TargetType allType);

    List<Assignment> findByClassroomIdAndDeletedFalse(Long classroomId);
}