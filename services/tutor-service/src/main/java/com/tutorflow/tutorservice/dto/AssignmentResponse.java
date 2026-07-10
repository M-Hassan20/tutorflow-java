package com.tutorflow.tutorservice.dto;

import com.tutorflow.tutorservice.enums.GradingMode;
import com.tutorflow.tutorservice.enums.TargetType;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentResponse {
    private Long id;
    private String title;
    private String description;
    private String starterCode;
    private Long classroomId;
    private Long createdBy;
    private LocalDateTime dueDate;
    private LocalDateTime releaseDate;
    private TargetType targetType;
    private Set<Long> targetStudentIds;
    private String expectedOutput;
    private GradingMode gradingMode;
    private Integer maxAttempts;
    private boolean published;
    private List<TestCaseDto> testCases;
    private LocalDateTime createdAt;
}