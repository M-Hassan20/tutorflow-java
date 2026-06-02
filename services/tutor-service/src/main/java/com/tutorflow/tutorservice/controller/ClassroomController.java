package com.tutorflow.tutorservice.controller;

import com.tutorflow.tutorservice.dto.*;
import com.tutorflow.tutorservice.service.ClassroomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tutor/classrooms")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;

    @PostMapping
    public ResponseEntity<ClassroomResponse> create(
            @Valid @RequestBody CreateClassroomRequest request) {
        return ResponseEntity.ok(classroomService.create(request));
    }

    @PostMapping("/join")
    public ResponseEntity<ClassroomResponse> join(
            @RequestParam String joinCode,
            @RequestParam Long studentId) {
        return ResponseEntity.ok(classroomService.join(joinCode, studentId));
    }

    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<List<ClassroomResponse>> getByTutor(
            @PathVariable Long tutorId) {
        return ResponseEntity.ok(classroomService.getByTutor(tutorId));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ClassroomResponse>> getByStudent(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(classroomService.getByStudent(studentId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassroomResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(classroomService.getById(id));
    }
}