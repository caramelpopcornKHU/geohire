package com.geohire.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"job_posting_id", "seeker_id"})
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPosting jobPosting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seeker_id", nullable = false)
    private SeekerProfile seeker;

    @Column(columnDefinition = "TEXT")
    private String coverLetter;

    @Column(columnDefinition = "TEXT")
    private String experience;

    private String resumeUrl;
    private String portfolioUrl;
    private Integer expectedSalary;
    private LocalDate availableDate;

    @Column(columnDefinition = "TEXT")
    private String additionalNote;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime appliedAt;
}
