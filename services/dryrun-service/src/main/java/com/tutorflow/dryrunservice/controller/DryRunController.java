package com.tutorflow.dryrunservice.controller;

import com.tutorflow.dryrunservice.dto.DryRunRequest;
import com.tutorflow.dryrunservice.dto.DryRunResponse;
import com.tutorflow.dryrunservice.service.DryRunService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dryrun")
@RequiredArgsConstructor
public class DryRunController {

    private final DryRunService dryRunService;

    @PostMapping("/run")
    public ResponseEntity<DryRunResponse> run(@RequestBody DryRunRequest request) {
        return ResponseEntity.ok(dryRunService.dryRun(request));
    }
}