package com.tutorflow.tutorservice.dto;

import com.tutorflow.tutorservice.enums.SubmissionStatus;
import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProgressResponse {
    private Long studentId;
    private Long classroomId;
    private int totalAssignments;
    private int submitted;
    private int passed;
    private int pending;
    private List<AssignmentSubmissionSummary> assignments;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignmentSubmissionSummary {
        private Long assignmentId;
        private String assignmentTitle;
        private SubmissionStatus latestStatus;
        private int attemptCount;
    }
}