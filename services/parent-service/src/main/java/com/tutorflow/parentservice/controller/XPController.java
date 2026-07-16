package com.tutorflow.parentservice.controller;

import com.tutorflow.parentservice.dto.*;
import com.tutorflow.parentservice.service.XPService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/parent/xp")
@RequiredArgsConstructor
public class XPController {

    private final XPService xpService;

    @PostMapping("/award")
    public ResponseEntity<XPRecordResponse> award(
            @Valid @RequestBody AwardXPRequest request) {
        return ResponseEntity.ok(xpService.awardXP(request));
    }

    @GetMapping("/student/{studentId}/classroom/{classroomId}")
    public ResponseEntity<XPSummaryResponse> getStudentXP(
            @PathVariable Long studentId,
            @PathVariable Long classroomId) {
        return ResponseEntity.ok(
                xpService.getStudentXPInClassroom(studentId, classroomId));
    }

    @GetMapping("/classroom/{classroomId}/leaderboard")
    public ResponseEntity<List<LeaderboardEntry>> getLeaderboard(
            @PathVariable Long classroomId) {
        return ResponseEntity.ok(xpService.getLeaderboard(classroomId));
    }

    @GetMapping("/student/{studentId}/history")
    public ResponseEntity<List<XPRecordResponse>> getHistory(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(xpService.getStudentXPHistory(studentId));
    }
}