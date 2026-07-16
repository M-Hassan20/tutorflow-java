package com.tutorflow.parentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class XPSummaryResponse {
    private Long studentId;
    private Long classroomId;
    private int totalXP;
    private int rank;
}
