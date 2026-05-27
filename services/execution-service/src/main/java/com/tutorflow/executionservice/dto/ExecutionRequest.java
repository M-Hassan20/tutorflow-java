package com.tutorflow.executionservice.dto;

import lombok.Data;

@Data
public class ExecutionRequest {
    private String code;
    private String stdin;
}