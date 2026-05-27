package com.tutorflow.executionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionResponse {
    public String stdout;
    private String stderr;
    private int exitCode;
    private long executionTimeMs;
    private boolean timedOut;
}
