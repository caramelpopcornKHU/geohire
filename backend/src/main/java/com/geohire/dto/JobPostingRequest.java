package com.geohire.dto;

import com.geohire.entity.EmploymentType;
import com.geohire.entity.PostingStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter @Setter
public class JobPostingRequest {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "상세 내용은 필수입니다.")
    private String description;

    private String location;

    @NotNull(message = "고용 형태는 필수입니다.")
    private EmploymentType employmentType;

    private Integer salaryMin;
    private Integer salaryMax;

    private PostingStatus status;

    private LocalDate deadline;

    private Set<Long> skillIds;
}
