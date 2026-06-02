package com.tutorflow.tutorservice.service;

import com.tutorflow.tutorservice.dto.*;
import com.tutorflow.tutorservice.entity.Assignment;
import com.tutorflow.tutorservice.enums.TargetType;
import com.tutorflow.tutorservice.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;

    public AssignmentResponse create(CreateAssignmentRequest request) {
        Assignment assignment = Assignment.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .starterCode(request.getStarterCode())
                .classroomId(request.getClassroomId())
                .createdBy(request.getCreatedBy())
                .dueDate(request.getDueDate())
                .releaseDate(request.getReleaseDate())
                .targetType(request.getTargetType() != null
                        ? request.getTargetType() : TargetType.ALL)
                .targetStudentIds(request.getTargetStudentIds() != null
                        ? request.getTargetStudentIds() : new HashSet<>())
                .build();
        return toResponse(assignmentRepository.save(assignment));
    }

    public List<AssignmentResponse> getByClassroom(Long classroomId) {
        return assignmentRepository.findByClassroomId(classroomId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<AssignmentResponse> getVisibleForStudent(
            Long classroomId, Long studentId) {
        return assignmentRepository.findVisibleForStudent(
                        classroomId, studentId,
                        LocalDateTime.now(), TargetType.ALL)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public AssignmentResponse getById(Long id) {
        return toResponse(assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found")));
    }

    public List<AssignmentResponse> getAll() {
        return assignmentRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public AssignmentResponse update(Long id, CreateAssignmentRequest request) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        assignment.setTitle(request.getTitle());
        assignment.setDescription(request.getDescription());
        assignment.setStarterCode(request.getStarterCode());
        assignment.setDueDate(request.getDueDate());
        assignment.setReleaseDate(request.getReleaseDate());
        assignment.setTargetType(request.getTargetType() != null
                ? request.getTargetType() : assignment.getTargetType());
        if (request.getTargetStudentIds() != null) {
            assignment.setTargetStudentIds(request.getTargetStudentIds());
        }
        return toResponse(assignmentRepository.save(assignment));
    }

    private AssignmentResponse toResponse(Assignment a) {
        return AssignmentResponse.builder()
                .id(a.getId())
                .title(a.getTitle())
                .description(a.getDescription())
                .starterCode(a.getStarterCode())
                .classroomId(a.getClassroomId())
                .createdBy(a.getCreatedBy())
                .dueDate(a.getDueDate())
                .releaseDate(a.getReleaseDate())
                .targetType(a.getTargetType())
                .targetStudentIds(a.getTargetStudentIds())
                .createdAt(a.getCreatedAt())
                .build();
    }
}