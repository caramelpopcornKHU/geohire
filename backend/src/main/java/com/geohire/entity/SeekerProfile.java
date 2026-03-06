package com.geohire.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "seeker_profiles")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SeekerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String phone;

    @Column(columnDefinition = "TEXT")
    private String introduction;

    private String portfolioUrl;

    private Integer experienceYears;

    @ManyToMany
    @JoinTable(
        name = "seeker_skills",
        joinColumns = @JoinColumn(name = "seeker_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    @Builder.Default
    private Set<Skill> skills = new HashSet<>();
}
