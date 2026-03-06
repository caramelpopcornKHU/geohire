package com.geohire.controller;

import com.geohire.dto.*;
import com.geohire.entity.User;
import com.geohire.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /** 구직자 프로필 조회 */
    @GetMapping("/seeker")
    public ResponseEntity<SeekerProfileResponse> getSeekerProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(profileService.getSeekerProfile(user.getId()));
    }

    /** 구직자 프로필 수정 */
    @PutMapping("/seeker")
    public ResponseEntity<SeekerProfileResponse> updateSeekerProfile(
            @AuthenticationPrincipal User user,
            @RequestBody SeekerProfileRequest request) {
        return ResponseEntity.ok(profileService.updateSeekerProfile(user.getId(), request));
    }

    /** 기업 프로필 조회 */
    @GetMapping("/company")
    public ResponseEntity<CompanyProfileResponse> getCompanyProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(profileService.getCompanyProfile(user.getId()));
    }

    /** 기업 프로필 수정 */
    @PutMapping("/company")
    public ResponseEntity<CompanyProfileResponse> updateCompanyProfile(
            @AuthenticationPrincipal User user,
            @RequestBody CompanyProfileRequest request) {
        return ResponseEntity.ok(profileService.updateCompanyProfile(user.getId(), request));
    }
}
