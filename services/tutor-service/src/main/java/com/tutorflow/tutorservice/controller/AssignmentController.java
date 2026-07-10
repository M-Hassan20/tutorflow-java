package com.tutorflow.tutorservice.controller;

import com.tutorflow.tutorservice.dto.*;
import com.tutorflow.tutorservice.security.RequestContext;
import com.tutorflow.tutorservice.service.AssignmentService;
import com.tutorflow.tutorservice.service.TestCaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tutor/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final TestCaseService testCaseService;
    private final RequestContext requestContext;

    @PostMapping
    public ResponseEntity<AssignmentResponse> create(
            @Valid @RequestBody CreateAssignmentRequest request) {
        if (!requestContext.isTutor()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(assignmentService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<AssignmentResponse>> getAll() {
        if (!requestContext.isTutor()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(assignmentService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssignmentResponse> getById(@PathVariable Long id) {
        if (!requestContext.isStudent() || !requestContext.isTutor() || !requestContext.isParent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(assignmentService.getById(id));
    }

    @GetMapping("/classroom/{classroomId}")
    public ResponseEntity<List<AssignmentResponse>> getByClassroom(
            @PathVariable Long classroomId) {
        if (!requestContext.isTutor()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(assignmentService.getByClassroom(classroomId));
    }

    @GetMapping("/classroom/{classroomId}/student/{studentId}")
    public ResponseEntity<List<AssignmentResponse>> getVisibleForStudent(
            @PathVariable Long classroomId,
            @PathVariable Long studentId) {
        if (!requestContext.isStudent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(
                assignmentService.getVisibleForStudent(classroomId, studentId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssignmentResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CreateAssignmentRequest request) {
        if (!requestContext.isTutor()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(assignmentService.update(id, request));
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<AssignmentResponse> togglePublish(@PathVariable Long id) {
        if (!requestContext.isTutor()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(assignmentService.togglePublish(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!requestContext.isTutor()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        assignmentService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<AssignmentResponse> restore(@PathVariable Long id) {
        if (!requestContext.isTutor()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(assignmentService.restore(id));
    }

    // Test case endpoints
    @GetMapping("/{id}/test-cases")
    public ResponseEntity<List<TestCaseDto>> getTestCases(@PathVariable Long id) {
        if (!requestContext.isTutor()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(testCaseService.getByAssignment(id));
    }

    @PostMapping("/{id}/test-cases")
    public ResponseEntity<TestCaseDto> addTestCase(
            @PathVariable Long id,
            @Valid @RequestBody CreateTestCaseRequest request) {
        if (!requestContext.isTutor()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(testCaseService.add(id, request));
    }

    @PutMapping("/test-cases/{testCaseId}")
    public ResponseEntity<TestCaseDto> updateTestCase(
            @PathVariable Long testCaseId,
            @Valid @RequestBody CreateTestCaseRequest request) {
        if (!requestContext.isTutor()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(testCaseService.update(testCaseId, request));
    }

    @DeleteMapping("/test-cases/{testCaseId}")
    public ResponseEntity<Void> deleteTestCase(@PathVariable Long testCaseId) {
        if (!requestContext.isTutor()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        testCaseService.delete(testCaseId);
        return ResponseEntity.noContent().build();
    }
}