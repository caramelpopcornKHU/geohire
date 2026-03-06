package com.geohire.service;

import com.geohire.dto.*;
import com.geohire.entity.*;
import com.geohire.repository.CompanyProfileRepository;
import com.geohire.repository.SeekerProfileRepository;
import com.geohire.repository.SkillRepository;
import com.geohire.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final SeekerProfileRepository seekerProfileRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final SkillRepository skillRepository;

    @Transactional(readOnly = true)
    public SeekerProfileResponse getSeekerProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        SeekerProfile profile = seekerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("구직자 프로필이 존재하지 않습니다."));
        return toSeekerResponse(user, profile);
    }

    @Transactional
    public SeekerProfileResponse updateSeekerProfile(Long userId, SeekerProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        SeekerProfile profile = seekerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("구직자 프로필이 존재하지 않습니다."));

        profile.setPhone(request.getPhone());
        profile.setIntroduction(request.getIntroduction());
        profile.setPortfolioUrl(request.getPortfolioUrl());
        profile.setExperienceYears(request.getExperienceYears());

        if (request.getSkillIds() != null) {
            Set<Skill> skills = new HashSet<>(skillRepository.findAllById(request.getSkillIds()));
            profile.setSkills(skills);
        }

        return toSeekerResponse(user, profile);
    }

    @Transactional(readOnly = true)
    public CompanyProfileResponse getCompanyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        CompanyProfile profile = companyProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("기업 프로필이 존재하지 않습니다."));
        return toCompanyResponse(user, profile);
    }

    @Transactional
    public CompanyProfileResponse updateCompanyProfile(Long userId, CompanyProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        CompanyProfile profile = companyProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("기업 프로필이 존재하지 않습니다."));

        profile.setCompanyName(request.getCompanyName());
        profile.setBusinessNumber(request.getBusinessNumber());
        profile.setIndustry(request.getIndustry());
        profile.setLocation(request.getLocation());
        profile.setWebsite(request.getWebsite());
        profile.setDescription(request.getDescription());
        profile.setLogoUrl(request.getLogoUrl());

        return toCompanyResponse(user, profile);
    }

    private SeekerProfileResponse toSeekerResponse(User user, SeekerProfile p) {
        return SeekerProfileResponse.builder()
                .id(p.getId())
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(p.getPhone())
                .introduction(p.getIntroduction())
                .portfolioUrl(p.getPortfolioUrl())
                .experienceYears(p.getExperienceYears())
                .skills(p.getSkills().stream()
                        .map(s -> SeekerProfileResponse.SkillInfo.builder()
                                .id(s.getId()).name(s.getName()).category(s.getCategory().name())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }

    private CompanyProfileResponse toCompanyResponse(User user, CompanyProfile p) {
        return CompanyProfileResponse.builder()
                .id(p.getId())
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .companyName(p.getCompanyName())
                .businessNumber(p.getBusinessNumber())
                .industry(p.getIndustry())
                .location(p.getLocation())
                .website(p.getWebsite())
                .description(p.getDescription())
                .logoUrl(p.getLogoUrl())
                .build();
    }
}
