package com.portfolio.cv.domain.repository;

import com.portfolio.cv.domain.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SkillRepository extends JpaRepository<Skill, UUID> {
}
