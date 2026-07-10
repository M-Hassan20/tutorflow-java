package com.tutorflow.tutorservice.dto;

import com.tutorflow.tutorservice.enums.SubmissionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OverrideStatusRequest {
    @NotNull
    private SubmissionStatus status;
    private String teacherNote;
}