package com.tutorflow.tutorservice.service;

import com.tutorflow.tutorservice.dto.*;
import com.tutorflow.tutorservice.entity.Classroom;
import com.tutorflow.tutorservice.repository.ClassroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomRepository classroomRepository;

    public ClassroomResponse create(CreateClassroomRequest request) {
        String joinCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Classroom classroom = Classroom.builder()
                .name(request.getName())
                .tutorId(request.getTutorId())
                .joinCode(joinCode)
                .build();
        return toResponse(classroomRepository.save(classroom));
    }

    public ClassroomResponse join(String joinCode, Long studentId) {
        Classroom classroom = classroomRepository.findByJoinCode(joinCode)
                .orElseThrow(() -> new RuntimeException("Invalid join code"));
        classroom.getStudentIds().add(studentId);
        return toResponse(classroomRepository.save(classroom));
    }

    public List<ClassroomResponse> getByTutor(Long tutorId) {
        return classroomRepository.findByTutorId(tutorId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<ClassroomResponse> getByStudent(Long studentId) {
        return classroomRepository.findByStudentIdsContaining(studentId)
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
                .build();
    }
}