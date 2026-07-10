package com.tutorflow.tutorservice.dto;

import lombok.*;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressOverviewResponse {
    private Long assignmentId;
    private String assignmentTitle;
    private int totalTargeted;
    private int submitted;
    private int passed;
    private int failed;
    private int notStarted;
    private Map<Long, String> studentStatuses;
}