package com.tutorflow.parentservice.repository;

import com.tutorflow.parentservice.entity.XPRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface XPRepository extends JpaRepository<XPRecord, Long> {
    List<XPRecord> findByStudentId(Long studentId);
    List<XPRecord> findByStudentIdAndClassroomId(Long studentId, Long classroomId);
    List<XPRecord> findByClassroomId(Long classroomId);
    Optional<XPRecord> findByStudentIdAndAssignmentId(
            Long studentId, Long assignmentId);
    boolean existsByStudentIdAndAssignmentId(
            Long studentId, Long assignmentId);

    @Query("SELECT x.studentId, SUM(x.xpAwarded) as totalXP " +
            "FROM XPRecord x WHERE x.classroomId = :classroomId " +
            "GROUP BY x.studentId ORDER BY totalXP DESC")
    List<Object[]> findLeaderboardByClassroomId(
            @Param("classroomId") Long classroomId);

    @Query("SELECT COALESCE(SUM(x.xpAwarded), 0) FROM XPRecord x " +
            "WHERE x.studentId = :studentId AND x.classroomId = :classroomId")
    int getTotalXPForStudentInClassroom(
            @Param("studentId") Long studentId,
            @Param("classroomId") Long classroomId);
}