package com.tutorflow.tutorservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.Map;

@Service
@Slf4j
public class ParentServiceClient {

    private final RestTemplate restTemplate;

    @Value("${parent-service.url}")
    private String parentServiceUrl;

    public ParentServiceClient() {
        this.restTemplate = new RestTemplate();
    }

    public void awardXP(Long studentId, Long classroomId,
                        Long assignmentId, int attemptNumber) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = Map.of(
                    "studentId", studentId,
                    "classroomId", classroomId,
                    "assignmentId", assignmentId,
                    "attemptNumber", attemptNumber
            );

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(body, headers);

            restTemplate.postForEntity(
                    parentServiceUrl + "/api/parent/xp/award",
                    entity,
                    Object.class
            );

            log.info("XP awarded to student {} for assignment {}",
                    studentId, assignmentId);

        } catch (Exception e) {
            log.warn("Failed to award XP to student {}: {}",
                    studentId, e.getMessage());
        }
    }
}