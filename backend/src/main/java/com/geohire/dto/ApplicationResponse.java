package com.geohire.dto;

import com.geohire.entity.ApplicationStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ApplicationResponse {
    private Long id;
    private Long jobPostingId;
    private String jobTitle;
    private String companyName;
    private String coverLetter;
    private String experience;
    private String resumeUrl;
    private String portfolioUrl;
    private Integer expectedSalary;
    private LocalDate availableDate;
    private String additionalNote;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;

    // 지원자 정보 (기업이 볼 때)
    private String seekerName;
    private String seekerEmail;
    private String seekerPhone;
}
