package com.tutorflow.tutorservice.service;

import com.tutorflow.tutorservice.entity.Assignment;
import com.tutorflow.tutorservice.entity.TestCase;
import com.tutorflow.tutorservice.enums.GradingMode;
import com.tutorflow.tutorservice.enums.SubmissionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
public class AutoGradingService {

    public SubmissionStatus grade(Assignment assignment,
                                  String stdout,
                                  List<TestCase> testCases) {
        if (assignment.getGradingMode() == GradingMode.MANUAL) {
            return SubmissionStatus.PENDING;
        }

        if (stdout == null) stdout = "";

        if (assignment.getGradingMode() == GradingMode.EXPECTED_OUTPUT) {
            return gradeByExpectedOutput(assignment.getExpectedOutput(), stdout);
        }

        if (assignment.getGradingMode() == GradingMode.TEST_CASES) {
            return gradeByTestCases(testCases, stdout);
        }

        return SubmissionStatus.PENDING;
    }

    private SubmissionStatus gradeByExpectedOutput(String expected, String actual) {
        if (expected == null) return SubmissionStatus.PENDING;
        String normalizedExpected = expected.trim().replaceAll("\\r\\n", "\n");
        String normalizedActual = actual.trim().replaceAll("\\r\\n", "\n");
        return normalizedExpected.equals(normalizedActual)
                ? SubmissionStatus.PASSED : SubmissionStatus.FAILED;
    }

    private SubmissionStatus gradeByTestCases(List<TestCase> testCases, String stdout) {
        if (testCases == null || testCases.isEmpty()) return SubmissionStatus.PENDING;
        // For now grade against first visible test case output
        // Full per-test-case grading will run in execution-service later
        TestCase first = testCases.stream()
                .filter(tc -> !tc.isHidden())
                .findFirst()
                .orElse(testCases.get(0));
        return gradeByExpectedOutput(first.getExpectedOutput(), stdout);
    }
}