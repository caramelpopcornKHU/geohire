package com.geohire.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "company_profiles")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CompanyProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String companyName;

    private String businessNumber;

    private String industry;

    private String location;

    private String website;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String logoUrl;
}
