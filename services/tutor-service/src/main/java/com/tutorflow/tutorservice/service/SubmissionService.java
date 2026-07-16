package com.tutorflow.tutorservice.service;

import com.tutorflow.tutorservice.dto.*;
import com.tutorflow.tutorservice.entity.Assignment;
import com.tutorflow.tutorservice.entity.Submission;
import com.tutorflow.tutorservice.entity.TestCase;
import com.tutorflow.tutorservice.enums.SubmissionStatus;
import com.tutorflow.tutorservice.repository.AssignmentRepository;
import com.tutorflow.tutorservice.repository.SubmissionRepository;
import com.tutorflow.tutorservice.repository.TestCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final TestCaseRepository testCaseRepository;
    private final AutoGradingService autoGradingService;
    private final ParentServiceClient parentServiceClient;

    public SubmissionResponse submit(SubmitCodeRequest request) {
        Assignment assignment = assignmentRepository
                .findById(request.getAssignmentId())
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        // Check max attempts
        if (assignment.getMaxAttempts() != null) {
            int attempts = submissionRepository
                    .findByAssignmentIdAndStudentIdOrderByAttemptNumberDesc(
                            request.getAssignmentId(), request.getStudentId())
                    .size();
            if (attempts >= assignment.getMaxAttempts()) {
                throw new RuntimeException("Max attempts reached");
            }
        }

        // Calculate attempt number
        int attemptNumber = submissionRepository
                .findMaxAttemptNumber(request.getAssignmentId(), request.getStudentId())
                .map(max -> max + 1)
                .orElse(1);

        // Auto-grade
        List<TestCase> testCases = testCaseRepository
                .findByAssignmentIdOrderByOrderIndex(request.getAssignmentId());
        SubmissionStatus status = autoGradingService.grade(
                assignment, request.getStdout(), testCases);

        Submission submission = Submission.builder()
                .assignmentId(request.getAssignmentId())
                .studentId(request.getStudentId())
                .code(request.getCode())
                .stdout(request.getStdout())
                .stderr(request.getStderr())
                .exitCode(request.getExitCode())
                .executionTimeMs(request.getExecutionTimeMs())
                .status(status)
                .attemptNumber(attemptNumber)
                .build();

        Submission saved = submissionRepository.save(submission);

        if(saved.getStatus() == SubmissionStatus.PASSED) {
            parentServiceClient.awardXP(
                    saved.getStudentId(),
                    assignment.getClassroomId(),
                    saved.getAssignmentId(),
                    saved.getAttemptNumber()
            );
        }
        return toResponse(saved);
    }

    public List<SubmissionResponse> getByAssignment(Long assignmentId) {
        return submissionRepository
                .findByAssignmentIdOrderBySubmittedAtDesc(assignmentId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<SubmissionResponse> getByStudent(Long studentId) {
        return submissionRepository.findByStudentId(studentId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<SubmissionResponse> getHistory(Long assignmentId, Long studentId) {
        return submissionRepository
                .findByAssignmentIdAndStudentIdOrderByAttemptNumberDesc(
                        assignmentId, studentId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public SubmissionResponse overrideStatus(Long id, OverrideStatusRequest request) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
        submission.setStatus(request.getStatus());
        if (request.getTeacherNote() != null)
            submission.setTeacherNote(request.getTeacherNote());
        return toResponse(submissionRepository.save(submission));
    }

    public SubmissionResponse flag(Long id, FlagSubmissionRequest request) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
        submission.setFlagged(request.isFlagged());
        if (request.getTeacherNote() != null)
            submission.setTeacherNote(request.getTeacherNote());
        return toResponse(submissionRepository.save(submission));
    }

    public List<SubmissionResponse> getFlagged(Long assignmentId) {
        return submissionRepository
                .findByAssignmentIdAndFlaggedTrue(assignmentId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public SubmissionResponse getLatest(Long assignmentId, Long studentId) {
        return submissionRepository
                .findByAssignmentIdAndStudentIdOrderByAttemptNumberDesc(
                        assignmentId, studentId)
                .stream()
                .findFirst()
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("No submission found"));
    }

    private SubmissionResponse toResponse(Submission s) {
        return SubmissionResponse.builder()
                .id(s.getId())
                .assignmentId(s.getAssignmentId())
                .studentId(s.getStudentId())
                .code(s.getCode())
                .stdout(s.getStdout())
                .stderr(s.getStderr())
                .exitCode(s.getExitCode())
                .executionTimeMs(s.getExecutionTimeMs())
                .status(s.getStatus())
                .attemptNumber(s.getAttemptNumber())
                .flagged(s.isFlagged())
                .teacherNote(s.getTeacherNote())
                .submittedAt(s.getSubmittedAt())
                .build();
    }
}