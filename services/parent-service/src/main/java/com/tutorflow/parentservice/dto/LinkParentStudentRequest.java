package com.tutorflow.parentservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LinkParentStudentRequest {
    @NotNull
    private Long parentId;

    @NotNull
    private Long studentId;

    private String parentEmail;
}
