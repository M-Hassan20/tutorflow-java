package com.tutorflow.tutorservice.controller;

import com.tutorflow.tutorservice.dto.*;
import com.tutorflow.tutorservice.security.RequestContext;
import com.tutorflow.tutorservice.service.ClassroomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tutor/classrooms")
@RequiredArgsConstructor
@Slf4j
public class ClassroomController {

    private final ClassroomService classroomService;
    private final RequestContext requestContext;

    @PostMapping
    public ResponseEntity<ClassroomResponse> create(
            @Valid @RequestBody CreateClassroomRequest request) {
        if (!requestContext.isTutor()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(classroomService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassroomResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(classroomService.getById(id));
    }

    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<List<ClassroomResponse>> getByTutor(
            @PathVariable Long tutorId) {
        if (!requestContext.isTutor()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(classroomService.getByTutor(tutorId));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ClassroomResponse>> getByStudent(
            @PathVariable Long studentId) {
        if (!requestContext.isStudent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(classroomService.getByStudent(studentId));
    }

    @PostMapping("/join")
    public ResponseEntity<ClassroomResponse> join(
            @RequestParam String joinCode,
            @RequestParam Long studentId) {
        if (!requestContext.isStudent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(classroomService.join(joinCode, studentId));
    }

    @DeleteMapping("/{id}/students/{studentId}")
    public ResponseEntity<ClassroomResponse> removeStudent(
            @PathVariable Long id,
            @PathVariable Long studentId) {
        if (!requestContext.isTutor()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(classroomService.removeStudent(id, studentId));
    }

    @PatchMapping("/{id}/rename")
    public ResponseEntity<ClassroomResponse> rename(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam Long tutorId) {
        if (!requestContext.isTutor()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(classroomService.rename(id, name, tutorId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!requestContext.isTutor()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        classroomService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<ClassroomResponse> restore(@PathVariable Long id) {
        if (!requestContext.isTutor()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(classroomService.restore(id));
    }
}