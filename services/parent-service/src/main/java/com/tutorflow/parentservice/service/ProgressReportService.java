package com.tutorflow.parentservice.service;

import com.tutorflow.parentservice.dto.ProgressReportData;
import com.tutorflow.parentservice.repository.ParentStudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProgressReportService {

    private final ParentStudentRepository parentStudentRepository;
    private final TutorServiceClient tutorServiceClient;
    private final XPService xpService;
    private final EmailService emailService;
    private final ReportBuilderService reportBuilderService;

    public void sendReportForStudent(Long studentId, String parentEmail) {
        log.info("Building report for student {}", studentId);
        List<Map<String, Object>> classrooms =
                tutorServiceClient.getStudentClassrooms(studentId);

        log.info("Found {} classrooms for student {}", classrooms.size(), studentId);

        List<ProgressReportData.ClassroomReport> classroomReports = new ArrayList<>();

        for (Map<String, Object> classroom : classrooms) {
            Long classroomId = ((Number) classroom.get("id")).longValue();
            String classroomName = (String) classroom.get("name");

            log.info("Processing classroom {} - {}", classroomId, classroomName);

            Map<String, Object> progress = tutorServiceClient
                    .getStudentProgress(classroomId, studentId);

            log.info("Progress data: {}", progress);

            if (progress == null) continue;

            int totalXP = xpService
                    .getStudentXPInClassroom(studentId, classroomId)
                    .getTotalXP();
            int rank = xpService
                    .getStudentXPInClassroom(studentId, classroomId)
                    .getRank();

            classroomReports.add(ProgressReportData.ClassroomReport.builder()
                    .classroomId(classroomId)
                    .classroomName(classroomName)
                    .totalAssignments(
                            (Integer) progress.get("totalAssignments"))
                    .submitted((Integer) progress.get("submitted"))
                    .passed((Integer) progress.get("passed"))
                    .totalXP(totalXP)
                    .rank(rank)
                    .build());
        }

        ProgressReportData reportData = ProgressReportData.builder()
                .studentId(studentId)
                .classrooms(classroomReports)
                .build();

        log.info("Sending email to {}", parentEmail);

        String html = reportBuilderService.buildHtml(reportData);
        emailService.sendProgressReport(parentEmail,
                "Student #" + studentId, html);

        log.info("Email sent successfully");
    }

    public void sendReportsForAllParents() {
        parentStudentRepository.findAll().forEach(link -> {
            if (link.getParentEmail() != null) {
                try {
                    sendReportForStudent(link.getStudentId(), link.getParentEmail());
                } catch (Exception e) {
                    log.error("Failed to send report for student {}: {}",
                            link.getStudentId(), e.getMessage());
                }
            }
        });
    }
}