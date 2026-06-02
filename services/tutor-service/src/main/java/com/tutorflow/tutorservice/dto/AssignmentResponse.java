package com.tutorflow.tutorservice.dto;

import com.tutorflow.tutorservice.enums.TargetType;
import lombok.*;
import java.time.LocalDateTime;
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
    private LocalDateTime createdAt;
}