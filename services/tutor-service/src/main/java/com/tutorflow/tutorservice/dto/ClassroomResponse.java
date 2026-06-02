package com.tutorflow.tutorservice.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomResponse {
    private Long id;
    private String name;
    private String joinCode;
    private Long tutorId;
    private Set<Long> studentIds;
    private LocalDateTime createdAt;
}