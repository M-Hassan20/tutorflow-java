package com.tutorflow.dryrunservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DryRunResponse {
    private List<Map<String, Object>> steps;
    private int totalSteps;
    private boolean truncated;
    private boolean timedOut;
    private String error;
}