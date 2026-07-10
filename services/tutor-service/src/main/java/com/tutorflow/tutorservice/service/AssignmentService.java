package com.tutorflow.tutorservice.service;

import com.tutorflow.tutorservice.dto.*;
import com.tutorflow.tutorservice.entity.Assignment;
import com.tutorflow.tutorservice.entity.TestCase;
import com.tutorflow.tutorservice.enums.GradingMode;
import com.tutorflow.tutorservice.enums.TargetType;
import com.tutorflow.tutorservice.repository.AssignmentRepository;
import com.tutorflow.tutorservice.repository.ClassroomRepository;
import com.tutorflow.tutorservice.repository.TestCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final TestCaseRepository testCaseRepository;
    private final TestCaseService testCaseService;
    private final ClassroomRepository classroomRepository;

    @Transactional
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
                .expectedOutput(request.getExpectedOutput())
                .gradingMode(request.getGradingMode() != null
                        ? request.getGradingMode() : GradingMode.MANUAL)
                .maxAttempts(request.getMaxAttempts())
                .published(request.isPublished())
                .build();

        Assignment saved = assignmentRepository.save(assignment);

        if (request.getTestCases() != null) {
            request.getTestCases().forEach(tc ->
                    testCaseService.add(saved.getId(), tc));
        }

        return toResponse(saved);
    }

    public List<AssignmentResponse> getAll() {
        return assignmentRepository.findAll()
                .stream()
                .filter(a -> !a.isDeleted())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<AssignmentResponse> getByClassroom(Long classroomId) {
        return assignmentRepository.findByClassroomIdAndDeletedFalse(classroomId)
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

    @Transactional
    public AssignmentResponse update(Long id, CreateAssignmentRequest request) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        assignment.setTitle(request.getTitle());
        assignment.setDescription(request.getDescription());
        assignment.setStarterCode(request.getStarterCode());
        assignment.setDueDate(request.getDueDate());
        assignment.setReleaseDate(request.getReleaseDate());
        assignment.setExpectedOutput(request.getExpectedOutput());
        assignment.setPublished(request.isPublished());
        if (request.getGradingMode() != null)
            assignment.setGradingMode(request.getGradingMode());
        if (request.getTargetType() != null)
            assignment.setTargetType(request.getTargetType());
        if (request.getTargetStudentIds() != null)
            assignment.setTargetStudentIds(request.getTargetStudentIds());
        if (request.getMaxAttempts() != null)
            assignment.setMaxAttempts(request.getMaxAttempts());
        if (request.getTestCases() != null) {
            testCaseService.deleteByAssignment(id);
            request.getTestCases().forEach(tc -> testCaseService.add(id, tc));
        }
        return toResponse(assignmentRepository.save(assignment));
    }

    public void softDelete(Long id) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        assignment.setDeleted(true);
        assignment.setDeletedAt(LocalDateTime.now());
        assignmentRepository.save(assignment);
    }

    public AssignmentResponse restore(Long id) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        assignment.setDeleted(false);
        assignment.setDeletedAt(null);
        return toResponse(assignmentRepository.save(assignment));
    }

    public AssignmentResponse togglePublish(Long id) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        assignment.setPublished(!assignment.isPublished());
        return toResponse(assignmentRepository.save(assignment));
    }

    private AssignmentResponse toResponse(Assignment a) {
        List<TestCaseDto> testCases = testCaseRepository
                .findByAssignmentIdOrderByOrderIndex(a.getId())
                .stream().map(testCaseService::toDto).collect(Collectors.toList());
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
                .expectedOutput(a.getExpectedOutput())
                .gradingMode(a.getGradingMode())
                .maxAttempts(a.getMaxAttempts())
                .published(a.isPublished())
                .testCases(testCases)
                .createdAt(a.getCreatedAt())
                .build();
    }
}