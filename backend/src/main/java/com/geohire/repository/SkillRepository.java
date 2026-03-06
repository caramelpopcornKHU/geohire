package com.geohire.repository;

import com.geohire.entity.Skill;
import com.geohire.entity.SkillCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findByCategory(SkillCategory category);
    Optional<Skill> findByName(String name);
}
