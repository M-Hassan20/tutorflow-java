package com.tutorflow.tutorservice.service;

import com.tutorflow.tutorservice.dto.*;
import com.tutorflow.tutorservice.entity.Assignment;
import com.tutorflow.tutorservice.entity.Submission;
import com.tutorflow.tutorservice.enums.SubmissionStatus;
import com.tutorflow.tutorservice.enums.TargetType;
import com.tutorflow.tutorservice.repository.AssignmentRepository;
import com.tutorflow.tutorservice.repository.ClassroomRepository;
import com.tutorflow.tutorservice.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final ClassroomRepository classroomRepository;

    public List<ProgressOverviewResponse> getClassroomProgress(Long classroomId) {
        List<Assignment> assignments = assignmentRepository
                .findByClassroomIdAndDeletedFalse(classroomId);

        var classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found"));

        Set<Long> allStudents = classroom.getStudentIds();

        return assignments.stream().map(assignment -> {
            // Determine targeted students
            Set<Long> targeted = assignment.getTargetType() == TargetType.ALL
                    ? allStudents
                    : assignment.getTargetStudentIds();

            List<Submission> submissions = submissionRepository
                    .findByAssignmentIdOrderBySubmittedAtDesc(assignment.getId());

            // Get latest submission per student
            Map<Long, Submission> latestPerStudent = new HashMap<>();
            for (Submission s : submissions) {
                latestPerStudent.putIfAbsent(s.getStudentId(), s);
            }

            int submitted = latestPerStudent.size();
            int passed = (int) latestPerStudent.values().stream()
                    .filter(s -> s.getStatus() == SubmissionStatus.PASSED)
                    .count();
            int failed = (int) latestPerStudent.values().stream()
                    .filter(s -> s.getStatus() == SubmissionStatus.FAILED)
                    .count();
            int notStarted = (int) targeted.stream()
                    .filter(id -> !latestPerStudent.containsKey(id))
                    .count();

            // Per student status map
            Map<Long, String> studentStatuses = new HashMap<>();
            for (Long studentId : targeted) {
                if (latestPerStudent.containsKey(studentId)) {
                    studentStatuses.put(studentId,
                            latestPerStudent.get(studentId).getStatus().name());
                } else {
                    studentStatuses.put(studentId, "NOT_STARTED");
                }
            }

            return ProgressOverviewResponse.builder()
                    .assignmentId(assignment.getId())
                    .assignmentTitle(assignment.getTitle())
                    .totalTargeted(targeted.size())
                    .submitted(submitted)
                    .passed(passed)
                    .failed(failed)
                    .notStarted(notStarted)
                    .studentStatuses(studentStatuses)
                    .build();

        }).collect(Collectors.toList());
    }

    public StudentProgressResponse getStudentProgress(
            Long classroomId, Long studentId) {
        var classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found"));

        List<Assignment> assignments = assignmentRepository
                .findVisibleForStudent(classroomId, studentId,
                        LocalDateTime.now(), TargetType.ALL);

        List<Submission> allSubmissions = submissionRepository
                .findByStudentId(studentId);

        Map<Long, List<Submission>> submissionsByAssignment = allSubmissions
                .stream()
                .collect(Collectors.groupingBy(Submission::getAssignmentId));

        List<StudentProgressResponse.AssignmentSubmissionSummary> summaries =
                assignments.stream().map(assignment -> {
                    List<Submission> subs = submissionsByAssignment
                            .getOrDefault(assignment.getId(), List.of());

                    SubmissionStatus latestStatus = subs.stream()
                            .max(Comparator.comparingInt(Submission::getAttemptNumber))
                            .map(Submission::getStatus)
                            .orElse(null);

                    return StudentProgressResponse.AssignmentSubmissionSummary.builder()
                            .assignmentId(assignment.getId())
                            .assignmentTitle(assignment.getTitle())
                            .latestStatus(latestStatus)
                            .attemptCount(subs.size())
                            .build();
                }).collect(Collectors.toList());

        int submitted = (int) summaries.stream()
                .filter(s -> s.getLatestStatus() != null).count();
        int passed = (int) summaries.stream()
                .filter(s -> s.getLatestStatus() == SubmissionStatus.PASSED).count();
        int pending = (int) summaries.stream()
                .filter(s -> s.getLatestStatus() == SubmissionStatus.PENDING).count();

        return StudentProgressResponse.builder()
                .studentId(studentId)
                .classroomId(classroomId)
                .totalAssignments(assignments.size())
                .submitted(submitted)
                .passed(passed)
                .pending(pending)
                .assignments(summaries)
                .build();
    }
}