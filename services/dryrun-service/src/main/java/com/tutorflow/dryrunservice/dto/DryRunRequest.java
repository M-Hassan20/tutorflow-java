package com.tutorflow.dryrunservice.dto;

import lombok.Data;

@Data
public class DryRunRequest {
    private String code;
    private String stdin;
}