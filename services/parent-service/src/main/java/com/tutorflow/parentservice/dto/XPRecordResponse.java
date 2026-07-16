package com.tutorflow.parentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XPRecordResponse {
    private Long id;
    private Long studentId;
    private Long classroomId;
    private Long assignmentId;
    private int xpAwarded;
    private int attemptNumber;
    private LocalDateTime awardedAt;
}
