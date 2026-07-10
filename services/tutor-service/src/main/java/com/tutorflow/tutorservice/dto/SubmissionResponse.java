package com.tutorflow.tutorservice.dto;

import com.tutorflow.tutorservice.enums.SubmissionStatus;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResponse {
    private Long id;
    private Long assignmentId;
    private Long studentId;
    private String code;
    private String stdout;
    private String stderr;
    private Integer exitCode;
    private Long executionTimeMs;
    private SubmissionStatus status;
    private int attemptNumber;
    private boolean flagged;
    private String teacherNote;
    private LocalDateTime submittedAt;
}