package com.tutorflow.tutorservice.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseDto {
    private Long id;
    private String input;
    private String expectedOutput;
    private boolean hidden;
    private int orderIndex;
}