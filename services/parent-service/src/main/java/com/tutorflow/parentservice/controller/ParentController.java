package com.tutorflow.parentservice.controller;

import com.tutorflow.parentservice.dto.*;
import com.tutorflow.parentservice.service.ParentService;
import com.tutorflow.parentservice.service.ProgressReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/parent")
@RequiredArgsConstructor
@Slf4j
public class ParentController {

    private final ParentService parentService;
    private final ProgressReportService progressReportService;

    @PostMapping("/link")
    public ResponseEntity<ParentStudentResponse> link(
            @Valid @RequestBody LinkParentStudentRequest request) {
        return ResponseEntity.ok(parentService.linkStudent(request));
    }

    @DeleteMapping("/{parentId}/student/{studentId}")
    public ResponseEntity<Void> unlink(
            @PathVariable Long parentId,
            @PathVariable Long studentId) {
        parentService.unlinkStudent(parentId, studentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{parentId}/students")
    public ResponseEntity<List<Long>> getStudents(
            @PathVariable Long parentId) {
        return ResponseEntity.ok(parentService.getStudentsByParent(parentId));
    }

    @GetMapping("/student/{studentId}/parents")
    public ResponseEntity<List<Long>> getParents(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(parentService.getParentsByStudent(studentId));
    }

    @PostMapping("/{parentId}/report")
    public ResponseEntity<Void> sendReport(
            @PathVariable Long parentId,
            @RequestParam String email) {
        List<Long> students = parentService.getStudentsByParent(parentId);
        log.info("Found {} students for parent {}", students.size(), parentId);
        students.forEach(studentId -> {
            log.info("Sending report for student {}", studentId);
            progressReportService.sendReportForStudent(studentId, email);
        });
        return ResponseEntity.ok().build();
    }
}