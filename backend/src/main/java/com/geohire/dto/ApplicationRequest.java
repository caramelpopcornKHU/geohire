package com.geohire.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class ApplicationRequest {
    private String coverLetter;
    private String experience;
    private String resumeUrl;
    private String portfolioUrl;
    private Integer expectedSalary;
    private LocalDate availableDate;
    private String additionalNote;
}
