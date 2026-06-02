package com.tutorflow.tutorservice.controller;

import com.tutorflow.tutorservice.dto.*;
import com.tutorflow.tutorservice.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tutor/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping
    public ResponseEntity<SubmissionResponse> submit(
            @Valid @RequestBody SubmitCodeRequest request) {
        return ResponseEntity.ok(submissionService.submit(request));
    }

    @GetMapping("/assignment/{assignmentId}")
    public ResponseEntity<List<SubmissionResponse>> getByAssignment(
            @PathVariable Long assignmentId) {
        return ResponseEntity.ok(submissionService.getByAssignment(assignmentId));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<SubmissionResponse>> getByStudent(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(submissionService.getByStudent(studentId));
    }

    @GetMapping("/assignment/{assignmentId}/student/{studentId}")
    public ResponseEntity<SubmissionResponse> getLatest(
            @PathVariable Long assignmentId,
            @PathVariable Long studentId) {
        return ResponseEntity.ok(
                submissionService.getLatest(assignmentId, studentId));
    }
}