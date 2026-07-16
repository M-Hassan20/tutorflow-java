package com.tutorflow.parentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardEntry {
    private Long studentId;
    private int totalXP;
    private int rank;
}
