package com.geohire.service;

import com.geohire.dto.ApplicationRequest;
import com.geohire.dto.ApplicationResponse;
import com.geohire.entity.*;
import com.geohire.repository.ApplicationRepository;
import com.geohire.repository.JobPostingRepository;
import com.geohire.repository.SeekerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobPostingRepository jobPostingRepository;
    private final SeekerProfileRepository seekerProfileRepository;
    private final NotificationService notificationService;

    @Transactional
    public ApplicationResponse apply(Long userId, Long jobPostingId, ApplicationRequest request) {
        SeekerProfile seeker = seekerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("구직자 프로필이 존재하지 않습니다."));

        JobPosting posting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new IllegalArgumentException("채용공고를 찾을 수 없습니다."));

        if (posting.getStatus() != PostingStatus.OPEN) {
            throw new IllegalArgumentException("마감된 채용공고에는 지원할 수 없습니다.");
        }

        if (applicationRepository.existsByJobPostingIdAndSeekerId(jobPostingId, seeker.getId())) {
            throw new IllegalArgumentException("이미 지원한 공고입니다.");
        }

        Application application = Application.builder()
                .jobPosting(posting)
                .seeker(seeker)
                .coverLetter(request.getCoverLetter())
                .experience(request.getExperience())
                .resumeUrl(request.getResumeUrl())
                .portfolioUrl(request.getPortfolioUrl())
                .expectedSalary(request.getExpectedSalary())
                .availableDate(request.getAvailableDate())
                .additionalNote(request.getAdditionalNote())
                .build();

        applicationRepository.save(application);

        // 기업에게 알림
        notificationService.create(
                posting.getCompany().getUser().getId(),
                "새로운 지원자",
                seeker.getUser().getName() + "님이 '" + posting.getTitle() + "' 공고에 지원했습니다.",
                "APPLICATION_RECEIVED",
                posting.getId()
        );

        return toResponse(application);
    }

    @Transactional(readOnly = true)
    public Page<ApplicationResponse> getMyApplications(Long userId, Pageable pageable) {
        SeekerProfile seeker = seekerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("구직자 프로필이 존재하지 않습니다."));
        return applicationRepository.findBySeekerId(seeker.getId(), pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ApplicationResponse> getApplicationsForJob(Long userId, Long jobPostingId, Pageable pageable) {
        JobPosting posting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new IllegalArgumentException("채용공고를 찾을 수 없습니다."));

        if (!posting.getCompany().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("조회 권한이 없습니다.");
        }

        return applicationRepository.findByJobPostingId(jobPostingId, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ApplicationResponse getApplication(Long userId, Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("지원 내역을 찾을 수 없습니다."));

        boolean isCompanyOwner = application.getJobPosting().getCompany().getUser().getId().equals(userId);
        boolean isSeekerOwner = application.getSeeker().getUser().getId().equals(userId);

        if (!isCompanyOwner && !isSeekerOwner) {
            throw new IllegalArgumentException("조회 권한이 없습니다.");
        }

        return toResponse(application);
    }

    @Transactional(readOnly = true)
    public boolean hasApplied(Long userId, Long jobPostingId) {
        return seekerProfileRepository.findByUserId(userId)
                .map(seeker -> applicationRepository.existsByJobPostingIdAndSeekerId(jobPostingId, seeker.getId()))
                .orElse(false);
    }

    @Transactional
    public ApplicationResponse updateStatus(Long userId, Long applicationId, ApplicationStatus status) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("지원 내역을 찾을 수 없습니다."));

        if (!application.getJobPosting().getCompany().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("상태 변경 권한이 없습니다.");
        }

        application.setStatus(status);

        // 구직자에게 알림
        String statusMsg = switch (status) {
            case REVIEWED -> "서류가 검토 중입니다.";
            case ACCEPTED -> "축하합니다! 합격하셨습니다. 🎉";
            case REJECTED -> "아쉽지만 불합격입니다.";
            default -> "지원 상태가 변경되었습니다.";
        };
        notificationService.create(
                application.getSeeker().getUser().getId(),
                "지원 상태 변경",
                "'" + application.getJobPosting().getTitle() + "' - " + statusMsg,
                "APPLICATION_STATUS",
                application.getId()
        );

        return toResponse(application);
    }

    private ApplicationResponse toResponse(Application app) {
        return ApplicationResponse.builder()
                .id(app.getId())
                .jobPostingId(app.getJobPosting().getId())
                .jobTitle(app.getJobPosting().getTitle())
                .companyName(app.getJobPosting().getCompany().getCompanyName())
                .coverLetter(app.getCoverLetter())
                .experience(app.getExperience())
                .resumeUrl(app.getResumeUrl())
                .portfolioUrl(app.getPortfolioUrl())
                .expectedSalary(app.getExpectedSalary())
                .availableDate(app.getAvailableDate())
                .additionalNote(app.getAdditionalNote())
                .status(app.getStatus())
                .appliedAt(app.getAppliedAt())
                .seekerName(app.getSeeker().getUser().getName())
                .seekerEmail(app.getSeeker().getUser().getEmail())
                .seekerPhone(app.getSeeker().getPhone())
                .build();
    }
}
