package com.tutorflow.tutorservice.controller;

import com.tutorflow.tutorservice.dto.*;
import com.tutorflow.tutorservice.security.RequestContext;
import com.tutorflow.tutorservice.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tutor/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;
    private final RequestContext requestContext;

    @PostMapping
    public ResponseEntity<SubmissionResponse> submit(
            @Valid @RequestBody SubmitCodeRequest request) {
        if (!requestContext.isStudent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(submissionService.submit(request));
    }

    @GetMapping("/assignment/{assignmentId}")
    public ResponseEntity<List<SubmissionResponse>> getByAssignment(
            @PathVariable Long assignmentId) {
        if (!requestContext.isTutor()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(submissionService.getByAssignment(assignmentId));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<SubmissionResponse>> getByStudent(
            @PathVariable Long studentId) {
        if (!requestContext.isTutor() || !requestContext.isStudent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(submissionService.getByStudent(studentId));
    }

    @GetMapping("/assignment/{assignmentId}/student/{studentId}")
    public ResponseEntity<SubmissionResponse> getLatest(
            @PathVariable Long assignmentId,
            @PathVariable Long studentId) {
        if (!requestContext.isTutor() || !requestContext.isStudent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(
                submissionService.getLatest(assignmentId, studentId));
    }

    @GetMapping("/assignment/{assignmentId}/student/{studentId}/history")
    public ResponseEntity<List<SubmissionResponse>> getHistory(
            @PathVariable Long assignmentId,
            @PathVariable Long studentId) {
        if (!requestContext.isTutor() || !requestContext.isStudent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(
                submissionService.getHistory(assignmentId, studentId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<SubmissionResponse> overrideStatus(
            @PathVariable Long id,
            @Valid @RequestBody OverrideStatusRequest request) {
        if (!requestContext.isTutor()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(submissionService.overrideStatus(id, request));
    }

    @PatchMapping("/{id}/flag")
    public ResponseEntity<SubmissionResponse> flag(
            @PathVariable Long id,
            @RequestBody FlagSubmissionRequest request) {
        if (!requestContext.isTutor()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(submissionService.flag(id, request));
    }

    @GetMapping("/assignment/{assignmentId}/flagged")
    public ResponseEntity<List<SubmissionResponse>> getFlagged(
            @PathVariable Long assignmentId) {
        if (!requestContext.isTutor()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(submissionService.getFlagged(assignmentId));
    }
}