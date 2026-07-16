package com.tutorflow.parentservice.service;

import com.tutorflow.parentservice.dto.*;
import com.tutorflow.parentservice.entity.ParentStudent;
import com.tutorflow.parentservice.repository.ParentStudentRepository;
import com.tutorflow.parentservice.repository.XPRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParentService {

    private final ParentStudentRepository parentStudentRepository;
    private final XPRepository xpRepository;
    private final XPService xpService;

    public ParentStudentResponse linkStudent(LinkParentStudentRequest request) {
        if (parentStudentRepository.existsByParentIdAndStudentId(
                request.getParentId(), request.getStudentId())) {
            throw new RuntimeException("Student already linked to this parent");
        }
        ParentStudent link = ParentStudent.builder()
                .parentId(request.getParentId())
                .studentId(request.getStudentId())
                .parentEmail(request.getParentEmail())
                .build();
        return toResponse(parentStudentRepository.save(link));
    }

    public void unlinkStudent(Long parentId, Long studentId) {
        ParentStudent link = parentStudentRepository
                .findByParentIdAndStudentId(parentId, studentId)
                .orElseThrow(() -> new RuntimeException("Link not found"));
        parentStudentRepository.delete(link);
    }

    public List<Long> getStudentsByParent(Long parentId) {
        return parentStudentRepository.findByParentId(parentId)
                .stream()
                .map(ParentStudent::getStudentId)
                .collect(Collectors.toList());
    }

    public List<Long> getParentsByStudent(Long studentId) {
        return parentStudentRepository.findByStudentId(studentId)
                .stream()
                .map(ParentStudent::getParentId)
                .collect(Collectors.toList());
    }

    private ParentStudentResponse toResponse(ParentStudent ps) {
        return ParentStudentResponse.builder()
                .id(ps.getId())
                .parentId(ps.getParentId())
                .studentId(ps.getStudentId())
                .linkedAt(ps.getLinkedAt())
                .build();
    }
}