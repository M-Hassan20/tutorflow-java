package com.tutorflow.parentservice.service;

import com.tutorflow.parentservice.dto.ProgressReportData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.List;

@Service
@Slf4j
public class TutorServiceClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${tutor-service.url}")
    private String tutorServiceUrl;

    public Map<String, Object> getStudentProgress(Long classroomId, Long studentId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-User-Role", "TUTOR");
            headers.set("X-User-Email", "internal@tutorflow.com");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    tutorServiceUrl + "/api/tutor/progress/classroom/"
                            + classroomId + "/student/" + studentId,
                    HttpMethod.GET,
                    entity,
                    Map.class);
            return response.getBody();
        } catch (Exception e) {
            log.warn("Failed to fetch progress for student {}: {}",
                    studentId, e.getMessage());
            return null;
        }
    }

    public Map<String, Object> getClassroom(Long classroomId) {
        try {
            return restTemplate.getForObject(
                    tutorServiceUrl + "/api/tutor/classrooms/" + classroomId,
                    Map.class);
        } catch (Exception e) {
            log.warn("Failed to fetch classroom {}: {}",
                    classroomId, e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getStudentClassrooms(Long studentId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-User-Role", "STUDENT");
            headers.set("X-User-Email", "internal@tutorflow.com");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<List> response = restTemplate.exchange(
                    tutorServiceUrl + "/api/tutor/classrooms/student/" + studentId,
                    HttpMethod.GET,
                    entity,
                    List.class);
            return response.getBody();
        } catch (Exception e) {
            log.warn("Failed to fetch classrooms for student {}: {}",
                    studentId, e.getMessage());
            return List.of();
        }
    }
}