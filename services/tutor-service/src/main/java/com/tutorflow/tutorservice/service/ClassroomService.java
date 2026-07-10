package com.tutorflow.tutorservice.service;

import com.tutorflow.tutorservice.dto.*;
import com.tutorflow.tutorservice.entity.Classroom;
import com.tutorflow.tutorservice.repository.ClassroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomRepository classroomRepository;

    public ClassroomResponse create(CreateClassroomRequest request) {
        // Unique name per tutor
        if (classroomRepository.existsByNameAndTutorIdAndDeletedFalse(
                request.getName(), request.getTutorId())) {
            throw new RuntimeException(
                    "You already have a classroom named '" + request.getName() + "'");
        }
        String joinCode = UUID.randomUUID().toString()
                .substring(0, 8).toUpperCase();
        Classroom classroom = Classroom.builder()
                .name(request.getName())
                .tutorId(request.getTutorId())
                .joinCode(joinCode)
                .build();
        return toResponse(classroomRepository.save(classroom));
    }

    public ClassroomResponse join(String joinCode, Long studentId) {
        Classroom classroom = classroomRepository
                .findByJoinCodeAndDeletedFalse(joinCode)
                .orElseThrow(() -> new RuntimeException("Invalid join code"));
        if (classroom.getCapacity() != null &&
                classroom.getStudentIds().size() >= classroom.getCapacity()) {
            throw new RuntimeException("Classroom is full");
        }
        classroom.getStudentIds().add(studentId);
        return toResponse(classroomRepository.save(classroom));
    }

    public ClassroomResponse removeStudent(Long classroomId, Long studentId) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found"));
        classroom.getStudentIds().remove(studentId);
        return toResponse(classroomRepository.save(classroom));
    }

    public ClassroomResponse rename(Long id, String newName, Long tutorId) {
        if (classroomRepository.existsByNameAndTutorIdAndDeletedFalse(newName, tutorId)) {
            throw new RuntimeException(
                    "You already have a classroom named '" + newName + "'");
        }
        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Classroom not found"));
        classroom.setName(newName);
        return toResponse(classroomRepository.save(classroom));
    }

    public void softDelete(Long id) {
        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Classroom not found"));
        classroom.setDeleted(true);
        classroom.setDeletedAt(LocalDateTime.now());
        classroomRepository.save(classroom);
    }

    public ClassroomResponse restore(Long id) {
        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Classroom not found"));
        classroom.setDeleted(false);
        classroom.setDeletedAt(null);
        return toResponse(classroomRepository.save(classroom));
    }

    public List<ClassroomResponse> getByTutor(Long tutorId) {
        return classroomRepository.findByTutorIdAndDeletedFalse(tutorId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<ClassroomResponse> getByStudent(Long studentId) {
        return classroomRepository
                .findByStudentIdsContainingAndDeletedFalse(studentId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ClassroomResponse getById(Long id) {
        return toResponse(classroomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Classroom not found")));
    }

    private ClassroomResponse toResponse(Classroom c) {
        return ClassroomResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .joinCode(c.getJoinCode())
                .tutorId(c.getTutorId())
                .studentIds(c.getStudentIds())
                .createdAt(c.getCreatedAt())
                .capacity(c.getCapacity())
                .build();
    }
}