package com.tutorflow.tutorservice.service;

import com.tutorflow.tutorservice.dto.*;
import com.tutorflow.tutorservice.entity.Submission;
import com.tutorflow.tutorservice.enums.SubmissionStatus;
import com.tutorflow.tutorservice.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;

    public SubmissionResponse submit(SubmitCodeRequest request) {
        Submission submission = Submission.builder()
                .assignmentId(request.getAssignmentId())
                .studentId(request.getStudentId())
                .code(request.getCode())
                .stdout(request.getStdout())
                .stderr(request.getStderr())
                .exitCode(request.getExitCode())
                .executionTimeMs(request.getExecutionTimeMs())
                .status(request.getExitCode() != null && request.getExitCode() == 0
                        ? SubmissionStatus.PASSED : SubmissionStatus.FAILED)
                .build();
        return toResponse(submissionRepository.save(submission));
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

    public SubmissionResponse getLatest(Long assignmentId, Long studentId) {
        return toResponse(submissionRepository
                .findByAssignmentIdAndStudentId(assignmentId, studentId)
                .orElseThrow(() -> new RuntimeException("No submission found")));
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
                .submittedAt(s.getSubmittedAt())
                .build();
    }
}