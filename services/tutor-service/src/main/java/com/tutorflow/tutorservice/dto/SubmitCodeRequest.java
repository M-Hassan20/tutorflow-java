package com.tutorflow.tutorservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitCodeRequest {
    @NotNull
    private Long assignmentId;
    @NotNull
    private Long studentId;
    @NotBlank
    private String code;
    private String stdout;
    private String stderr;
    private Integer exitCode;
    private Long executionTimeMs;
}