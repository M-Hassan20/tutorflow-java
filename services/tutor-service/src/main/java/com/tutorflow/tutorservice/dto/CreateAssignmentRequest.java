package com.tutorflow.tutorservice.dto;

import com.tutorflow.tutorservice.enums.GradingMode;
import com.tutorflow.tutorservice.enums.TargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class CreateAssignmentRequest {
    @NotBlank
    private String title;
    private String description;
    private String starterCode;
    @NotNull
    private Long classroomId;
    private Long createdBy;
    private LocalDateTime dueDate;
    private LocalDateTime releaseDate;
    private TargetType targetType = TargetType.ALL;
    private Set<Long> targetStudentIds;
    private String expectedOutput;
    private GradingMode gradingMode = GradingMode.MANUAL;
    private Integer maxAttempts;
    private boolean published = false;
    private List<CreateTestCaseRequest> testCases;
}