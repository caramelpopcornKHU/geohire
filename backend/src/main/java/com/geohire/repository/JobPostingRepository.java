package com.geohire.repository;

import com.geohire.entity.JobPosting;
import com.geohire.entity.PostingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long>, JpaSpecificationExecutor<JobPosting> {
    Page<JobPosting> findByCompanyId(Long companyId, Pageable pageable);
    Page<JobPosting> findByStatus(PostingStatus status, Pageable pageable);
}
