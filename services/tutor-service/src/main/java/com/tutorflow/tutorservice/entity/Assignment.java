package com.tutorflow.tutorservice.entity;

import com.tutorflow.tutorservice.enums.GradingMode;
import com.tutorflow.tutorservice.enums.TargetType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "assignments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String starterCode;

    @Column(nullable = false)
    private Long classroomId;

    @Column(nullable = false)
    private Long createdBy;

    private LocalDateTime dueDate;

    private LocalDateTime releaseDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TargetType targetType = TargetType.ALL;

    @ElementCollection
    @CollectionTable(name = "assignment_targets",
            joinColumns = @JoinColumn(name = "assignment_id"))
    @Column(name = "student_id")
    @Builder.Default
    private Set<Long> targetStudentIds = new HashSet<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Column(columnDefinition = "TEXT")
    private String expectedOutput;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private GradingMode gradingMode = GradingMode.MANUAL;

    private Integer maxAttempts;

    @Column(nullable = false)
    @Builder.Default
    private boolean published = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean deleted = false;

    private LocalDateTime deletedAt;
}