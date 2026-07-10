package com.tutorflow.tutorservice.controller;

import com.tutorflow.tutorservice.dto.ProgressOverviewResponse;
import com.tutorflow.tutorservice.dto.StudentProgressResponse;
import com.tutorflow.tutorservice.security.RequestContext;
import com.tutorflow.tutorservice.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tutor/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;
    private final RequestContext requestContext;

    @GetMapping("/classroom/{classroomId}")
    public ResponseEntity<List<ProgressOverviewResponse>> getClassroomProgress(
            @PathVariable Long classroomId) {
        if (!requestContext.isTutor()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(progressService.getClassroomProgress(classroomId));
    }

    @GetMapping("/classroom/{classroomId}/student/{studentId}")
    public ResponseEntity<StudentProgressResponse> getStudentProgress(
            @PathVariable Long classroomId,
            @PathVariable Long studentId) {
        if (!requestContext.isTutor() && !requestContext.isStudent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(
                progressService.getStudentProgress(classroomId, studentId));
    }
}