package com.tutorflow.tutorservice.controller;

import com.tutorflow.tutorservice.dto.*;
import com.tutorflow.tutorservice.service.AssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tutor/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping
    public ResponseEntity<AssignmentResponse> create(
            @Valid @RequestBody CreateAssignmentRequest request) {
        return ResponseEntity.ok(assignmentService.create(request));
    }

    @GetMapping("/classroom/{classroomId}")
    public ResponseEntity<List<AssignmentResponse>> getByClassroom(
            @PathVariable Long classroomId) {
        return ResponseEntity.ok(assignmentService.getByClassroom(classroomId));
    }

    @GetMapping("/classroom/{classroomId}/student/{studentId}")
    public ResponseEntity<List<AssignmentResponse>> getVisibleForStudent(
            @PathVariable Long classroomId,
            @PathVariable Long studentId) {
        return ResponseEntity.ok(
                assignmentService.getVisibleForStudent(classroomId, studentId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssignmentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(assignmentService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<AssignmentResponse>> getAll() {
        return ResponseEntity.ok(assignmentService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssignmentResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CreateAssignmentRequest request) {
        return ResponseEntity.ok(assignmentService.update(id, request));
    }
}