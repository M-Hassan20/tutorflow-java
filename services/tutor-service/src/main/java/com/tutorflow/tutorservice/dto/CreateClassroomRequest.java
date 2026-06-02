package com.tutorflow.tutorservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateClassroomRequest {
    @NotBlank
    private String name;
    private Long tutorId;
}