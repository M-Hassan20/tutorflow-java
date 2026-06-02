package com.tutorflow.tutorservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "classrooms")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String joinCode;

    @Column(nullable = false)
    private Long tutorId;

    @ElementCollection
    @CollectionTable(name = "classroom_students",
            joinColumns = @JoinColumn(name = "classroom_id"))
    @Column(name = "student_id")
    @Builder.Default
    private Set<Long> studentIds = new HashSet<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}