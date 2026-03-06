package com.geohire.controller;

import com.geohire.entity.Skill;
import com.geohire.entity.SkillCategory;
import com.geohire.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillRepository skillRepository;

    @GetMapping
    public ResponseEntity<List<Skill>> getAll() {
        return ResponseEntity.ok(skillRepository.findAll());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Skill>> getByCategory(@PathVariable SkillCategory category) {
        return ResponseEntity.ok(skillRepository.findByCategory(category));
    }
}
