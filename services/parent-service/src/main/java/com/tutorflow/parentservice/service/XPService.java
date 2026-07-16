package com.tutorflow.parentservice.service;

import com.tutorflow.parentservice.dto.AwardXPRequest;
import com.tutorflow.parentservice.dto.LeaderboardEntry;
import com.tutorflow.parentservice.dto.XPRecordResponse;
import com.tutorflow.parentservice.dto.XPSummaryResponse;
import com.tutorflow.parentservice.entity.XPRecord;
import com.tutorflow.parentservice.repository.XPRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class XPService {
    private final XPRepository xpRepository;

    @Value("${xp.first-attempt}")
    private int firstAttemptXP;

    @Value("${xp.second-attempt}")
    private int secondAttemptXP;

    @Value("${xp.third-attempt-plus}")
    private int thirdAttemptPlusXP;

    public XPRecordResponse awardXP(AwardXPRequest request) {
        if (xpRepository.existsByStudentIdAndAssignmentId(
                request.getStudentId(), request.getAssignmentId())) {
            return toResponse(xpRepository
                    .findByStudentIdAndAssignmentId(
                            request.getStudentId(), request.getAssignmentId())
                    .get());
        }
        int xp = calculateXP(request.getAttemptNumber());

        XPRecord record = XPRecord.builder()
                .studentId(request.getStudentId())
                .classroomId(request.getClassroomId())
                .assignmentId(request.getAssignmentId())
                .xpAwarded(xp)
                .attemptNumber(request.getAttemptNumber())
                .build();

        return toResponse(xpRepository.save(record));

    }

    public XPSummaryResponse getStudentXPInClassroom(
            Long studentId, Long classroomId) {
        int totalXP = xpRepository
                .getTotalXPForStudentInClassroom(studentId, classroomId);
        int rank = calculateRank(studentId, classroomId);
        return XPSummaryResponse.builder()
                .studentId(studentId)
                .classroomId(classroomId)
                .totalXP(totalXP)
                .rank(rank)
                .build();
    }

    public List<LeaderboardEntry> getLeaderboard(Long classroomId) {
        List<Object[]> results = xpRepository
                .findLeaderboardByClassroomId(classroomId);
        List<LeaderboardEntry> leaderboard = new ArrayList<>();
        AtomicInteger rank = new AtomicInteger(1);
        for (Object[] row : results) {
            leaderboard.add(LeaderboardEntry.builder()
                    .studentId(((Number) row[0]).longValue())
                    .totalXP(((Number) row[1]).intValue())
                    .rank(rank.getAndIncrement())
                    .build());
        }
        return leaderboard;
    }

    public List<XPRecordResponse> getStudentXPHistory(Long studentId) {
        return xpRepository.findByStudentId(studentId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }


    private int calculateXP(int attemptNumber) {
        if (attemptNumber == 1) return firstAttemptXP;
        if (attemptNumber == 2) return secondAttemptXP;
        return thirdAttemptPlusXP;
    }

    private int calculateRank(Long studentId, Long classroomId) {
        List<Object[]> leaderboard = xpRepository.findLeaderboardByClassroomId(classroomId);

        for (int i = 0; i < leaderboard.size(); i ++) {
            Long id = ((Number) leaderboard.get(i)[0]).longValue();

            if (id.equals(studentId)) return i + 1;
        }

        return leaderboard.size() + 1;
    }

    private XPRecordResponse toResponse(XPRecord r) {
        return XPRecordResponse.builder()
                .id(r.getId())
                .studentId(r.getStudentId())
                .classroomId(r.getClassroomId())
                .assignmentId(r.getAssignmentId())
                .xpAwarded(r.getXpAwarded())
                .attemptNumber(r.getAttemptNumber())
                .awardedAt(r.getAwardedAt())
                .build();


    }
}
