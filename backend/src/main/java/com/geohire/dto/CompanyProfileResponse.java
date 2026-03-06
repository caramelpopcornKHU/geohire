package com.geohire.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CompanyProfileResponse {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String companyName;
    private String businessNumber;
    private String industry;
    private String location;
    private String website;
    private String description;
    private String logoUrl;
}
