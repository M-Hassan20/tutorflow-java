package com.tutorflow.tutorservice.repository;

import com.tutorflow.tutorservice.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    Optional<Classroom> findByJoinCode(String joinCode);
    List<Classroom> findByTutorId(Long tutorId);
    List<Classroom> findByStudentIdsContaining(Long studentId);
}