package com.geohire.dto;

import lombok.*;

import java.util.Set;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SeekerProfileResponse {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String introduction;
    private String portfolioUrl;
    private Integer experienceYears;
    private Set<SkillInfo> skills;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class SkillInfo {
        private Long id;
        private String name;
        private String category;
    }
}
