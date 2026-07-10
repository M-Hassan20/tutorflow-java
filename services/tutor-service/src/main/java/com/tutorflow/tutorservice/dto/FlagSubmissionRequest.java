package com.tutorflow.tutorservice.dto;

import lombok.Data;

@Data
public class FlagSubmissionRequest {
    private boolean flagged;
    private String teacherNote;
}