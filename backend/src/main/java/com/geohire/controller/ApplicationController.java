package com.geohire.controller;

import com.geohire.dto.ApplicationRequest;
import com.geohire.dto.ApplicationResponse;
import com.geohire.entity.ApplicationStatus;
import com.geohire.entity.User;
import com.geohire.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    /** 구직자: 채용공고에 지원 */
    @PostMapping("/jobs/{jobId}")
    public ResponseEntity<ApplicationResponse> apply(
            @AuthenticationPrincipal User user,
            @PathVariable Long jobId,
            @RequestBody ApplicationRequest request) {
        return ResponseEntity.ok(applicationService.apply(user.getId(), jobId, request));
    }

    /** 구직자: 내 지원 내역 조회 */
    @GetMapping("/my")
    public ResponseEntity<Page<ApplicationResponse>> myApplications(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(applicationService.getMyApplications(user.getId(), pageable));
    }

    /** 구직자: 특정 공고 지원 여부 확인 */
    @GetMapping("/check/{jobId}")
    public ResponseEntity<Boolean> hasApplied(
            @AuthenticationPrincipal User user,
            @PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.hasApplied(user.getId(), jobId));
    }

    /** 지원서 단건 조회 (기업/구직자 모두 가능) */
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getApplication(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getApplication(user.getId(), id));
    }

    /** 기업: 특정 공고의 지원자 목록 */
    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<Page<ApplicationResponse>> applicationsForJob(
            @AuthenticationPrincipal User user,
            @PathVariable Long jobId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(applicationService.getApplicationsForJob(user.getId(), jobId, pageable));
    }

    /** 기업: 지원 상태 변경 */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApplicationResponse> updateStatus(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestParam ApplicationStatus status) {
        return ResponseEntity.ok(applicationService.updateStatus(user.getId(), id, status));
    }
}
