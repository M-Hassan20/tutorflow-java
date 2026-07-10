package com.tutorflow.tutorservice.repository;

import com.tutorflow.tutorservice.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    Optional<Classroom> findByJoinCodeAndDeletedFalse(String joinCode);
    List<Classroom> findByTutorIdAndDeletedFalse(Long tutorId);
    List<Classroom> findByStudentIdsContainingAndDeletedFalse(Long studentId);
    boolean existsByNameAndTutorIdAndDeletedFalse(String name, Long tutorId);
}