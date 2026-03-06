package com.geohire.dto;

import com.geohire.entity.EmploymentType;
import com.geohire.entity.PostingStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class JobPostingResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private EmploymentType employmentType;
    private Integer salaryMin;
    private Integer salaryMax;
    private PostingStatus status;
    private LocalDate deadline;
    private LocalDateTime createdAt;
    private CompanyInfo company;
    private Set<SkillInfo> skills;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class CompanyInfo {
        private Long id;
        private String companyName;
        private String industry;
        private String location;
        private String logoUrl;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class SkillInfo {
        private Long id;
        private String name;
        private String category;
    }
}
