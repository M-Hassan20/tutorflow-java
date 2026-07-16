package com.tutorflow.parentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgressReportData {
    private Long studentId;
    private List<ClassroomReport> classrooms;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClassroomReport {
        private Long classroomId;
        private String classroomName;
        private int totalAssignments;
        private int submitted;
        private int passed;
        private int totalXP;
        private int rank;
    }
}
