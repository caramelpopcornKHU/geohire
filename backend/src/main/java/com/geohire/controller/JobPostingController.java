package com.geohire.controller;

import com.geohire.dto.JobPostingRequest;
import com.geohire.dto.JobPostingResponse;
import com.geohire.entity.EmploymentType;
import com.geohire.entity.User;
import com.geohire.service.JobPostingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobPostingController {

    private final JobPostingService jobPostingService;

    @GetMapping
    public ResponseEntity<Page<JobPostingResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) EmploymentType employmentType,
            @RequestParam(required = false) List<Long> skillIds,
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(jobPostingService.search(keyword, location, employmentType, skillIds, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobPostingResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(jobPostingService.getById(id));
    }

    @PostMapping
    public ResponseEntity<JobPostingResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody JobPostingRequest request) {
        return ResponseEntity.ok(jobPostingService.create(user.getId(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobPostingResponse> update(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody JobPostingRequest request) {
        return ResponseEntity.ok(jobPostingService.update(user.getId(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        jobPostingService.delete(user.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    public ResponseEntity<Page<JobPostingResponse>> getMyPostings(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(jobPostingService.getByCompany(user.getId(), pageable));
    }
}
