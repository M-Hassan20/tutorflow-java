package com.tutorflow.tutorservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTestCaseRequest {
    private String input;
    @NotBlank
    private String expectedOutput;
    private boolean hidden;
    private int orderIndex;
}