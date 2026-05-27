package com.tutorflow.executionservice.controller;

import com.tutorflow.executionservice.dto.ExecutionRequest;
import com.tutorflow.executionservice.dto.ExecutionResponse;
import com.tutorflow.executionservice.service.ExecutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/execution")
@RequiredArgsConstructor
public class ExecutionController {

    private final ExecutionService codeExecutionService;

    @PostMapping("/run")
    public ResponseEntity<ExecutionResponse> run(@RequestBody ExecutionRequest request) {
        return ResponseEntity.ok(codeExecutionService.execute(request));
    }
}