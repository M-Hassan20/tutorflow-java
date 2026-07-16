package com.tutorflow.parentservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AwardXPRequest {
    @NotNull
    private Long studentId;

    @NotNull
    private Long classroomId;

    @NotNull
    private Long assignmentId;

    @NotNull
    private Integer attemptNumber;

}
