package com.tutorflow.parentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParentStudentResponse {
    private Long id;
    private Long studentId;
    private Long parentId;
    private LocalDateTime linkedAt;
}
