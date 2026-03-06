package com.geohire.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter @Setter
public class SeekerProfileRequest {
    private String phone;
    private String introduction;
    private String portfolioUrl;
    private Integer experienceYears;
    private Set<Long> skillIds;
}
