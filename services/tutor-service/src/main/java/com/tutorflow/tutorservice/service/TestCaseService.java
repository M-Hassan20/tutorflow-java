package com.tutorflow.tutorservice.service;

import com.tutorflow.tutorservice.dto.CreateTestCaseRequest;
import com.tutorflow.tutorservice.dto.TestCaseDto;
import com.tutorflow.tutorservice.entity.TestCase;
import com.tutorflow.tutorservice.repository.TestCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestCaseService {

    private final TestCaseRepository testCaseRepository;

    public List<TestCaseDto> getByAssignment(Long assignmentId) {
        return testCaseRepository
                .findByAssignmentIdOrderByOrderIndex(assignmentId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public TestCaseDto add(Long assignmentId, CreateTestCaseRequest request) {
        TestCase testCase = TestCase.builder()
                .assignmentId(assignmentId)
                .input(request.getInput())
                .expectedOutput(request.getExpectedOutput())
                .hidden(request.isHidden())
                .orderIndex(request.getOrderIndex())
                .build();
        return toDto(testCaseRepository.save(testCase));
    }

    public TestCaseDto update(Long id, CreateTestCaseRequest request) {
        TestCase testCase = testCaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test case not found"));
        testCase.setInput(request.getInput());
        testCase.setExpectedOutput(request.getExpectedOutput());
        testCase.setHidden(request.isHidden());
        testCase.setOrderIndex(request.getOrderIndex());
        return toDto(testCaseRepository.save(testCase));
    }

    public void delete(Long id) {
        testCaseRepository.deleteById(id);
    }

    @Transactional
    public void deleteByAssignment(Long assignmentId) {
        testCaseRepository.deleteByAssignmentId(assignmentId);
    }

    public TestCaseDto toDto(TestCase tc) {
        return TestCaseDto.builder()
                .id(tc.getId())
                .input(tc.getInput())
                .expectedOutput(tc.getExpectedOutput())
                .hidden(tc.isHidden())
                .orderIndex(tc.getOrderIndex())
                .build();
    }
}