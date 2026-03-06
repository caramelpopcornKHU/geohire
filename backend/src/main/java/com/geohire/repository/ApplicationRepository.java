package com.geohire.repository;

import com.geohire.entity.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Page<Application> findBySeekerId(Long seekerId, Pageable pageable);
    Page<Application> findByJobPostingId(Long jobPostingId, Pageable pageable);
    boolean existsByJobPostingIdAndSeekerId(Long jobPostingId, Long seekerId);
}
