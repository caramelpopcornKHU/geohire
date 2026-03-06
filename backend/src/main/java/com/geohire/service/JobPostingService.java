package com.geohire.service;

import com.geohire.dto.JobPostingRequest;
import com.geohire.dto.JobPostingResponse;
import com.geohire.entity.*;
import com.geohire.repository.CompanyProfileRepository;
import com.geohire.repository.JobPostingRepository;
import com.geohire.repository.SkillRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final SkillRepository skillRepository;

    @Transactional
    public JobPostingResponse create(Long userId, JobPostingRequest request) {
        CompanyProfile company = companyProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("기업 프로필이 존재하지 않습니다."));

        Set<Skill> skills = resolveSkills(request.getSkillIds());

        JobPosting posting = JobPosting.builder()
                .company(company)
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .employmentType(request.getEmploymentType())
                .salaryMin(request.getSalaryMin())
                .salaryMax(request.getSalaryMax())
                .status(request.getStatus() != null ? request.getStatus() : PostingStatus.OPEN)
                .deadline(request.getDeadline())
                .skills(skills)
                .build();

        jobPostingRepository.save(posting);
        return toResponse(posting);
    }

    @Transactional(readOnly = true)
    public Page<JobPostingResponse> search(String keyword, String location,
                                            EmploymentType employmentType, List<Long> skillIds,
                                            Pageable pageable) {
        Specification<JobPosting> spec = buildSpecification(keyword, location, employmentType, skillIds);
        return jobPostingRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public JobPostingResponse getById(Long id) {
        JobPosting posting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("채용공고를 찾을 수 없습니다."));
        return toResponse(posting);
    }

    @Transactional
    public JobPostingResponse update(Long userId, Long postingId, JobPostingRequest request) {
        JobPosting posting = jobPostingRepository.findById(postingId)
                .orElseThrow(() -> new IllegalArgumentException("채용공고를 찾을 수 없습니다."));

        if (!posting.getCompany().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        posting.setTitle(request.getTitle());
        posting.setDescription(request.getDescription());
        posting.setLocation(request.getLocation());
        posting.setEmploymentType(request.getEmploymentType());
        posting.setSalaryMin(request.getSalaryMin());
        posting.setSalaryMax(request.getSalaryMax());
        if (request.getStatus() != null) posting.setStatus(request.getStatus());
        posting.setDeadline(request.getDeadline());
        posting.setSkills(resolveSkills(request.getSkillIds()));

        return toResponse(posting);
    }

    @Transactional
    public void delete(Long userId, Long postingId) {
        JobPosting posting = jobPostingRepository.findById(postingId)
                .orElseThrow(() -> new IllegalArgumentException("채용공고를 찾을 수 없습니다."));

        if (!posting.getCompany().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        jobPostingRepository.delete(posting);
    }

    @Transactional(readOnly = true)
    public Page<JobPostingResponse> getByCompany(Long userId, Pageable pageable) {
        CompanyProfile company = companyProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("기업 프로필이 존재하지 않습니다."));
        return jobPostingRepository.findByCompanyId(company.getId(), pageable).map(this::toResponse);
    }

    // === Private helpers ===

    private Set<Skill> resolveSkills(Set<Long> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) return new HashSet<>();
        return new HashSet<>(skillRepository.findAllById(skillIds));
    }

    private Specification<JobPosting> buildSpecification(String keyword, String location,
                                                          EmploymentType employmentType, List<Long> skillIds) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 기본: OPEN 상태만
            predicates.add(cb.equal(root.get("status"), PostingStatus.OPEN));

            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
                ));
            }

            if (location != null && !location.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
            }

            if (employmentType != null) {
                predicates.add(cb.equal(root.get("employmentType"), employmentType));
            }

            if (skillIds != null && !skillIds.isEmpty()) {
                Join<JobPosting, Skill> skillJoin = root.join("skills");
                predicates.add(skillJoin.get("id").in(skillIds));
                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private JobPostingResponse toResponse(JobPosting posting) {
        CompanyProfile company = posting.getCompany();
        return JobPostingResponse.builder()
                .id(posting.getId())
                .title(posting.getTitle())
                .description(posting.getDescription())
                .location(posting.getLocation())
                .employmentType(posting.getEmploymentType())
                .salaryMin(posting.getSalaryMin())
                .salaryMax(posting.getSalaryMax())
                .status(posting.getStatus())
                .deadline(posting.getDeadline())
                .createdAt(posting.getCreatedAt())
                .company(JobPostingResponse.CompanyInfo.builder()
                        .id(company.getId())
                        .companyName(company.getCompanyName())
                        .industry(company.getIndustry())
                        .location(company.getLocation())
                        .logoUrl(company.getLogoUrl())
                        .build())
                .skills(posting.getSkills().stream()
                        .map(s -> JobPostingResponse.SkillInfo.builder()
                                .id(s.getId())
                                .name(s.getName())
                                .category(s.getCategory().name())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }
}
