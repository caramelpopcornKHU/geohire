package com.geohire.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CompanyProfileRequest {
    private String companyName;
    private String businessNumber;
    private String industry;
    private String location;
    private String website;
    private String description;
    private String logoUrl;
}
