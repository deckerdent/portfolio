package com.portfolio.cv.domain.repository;

import com.portfolio.cv.domain.model.ProfessionalExperience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProfessionalExperienceRepository extends JpaRepository<ProfessionalExperience, UUID> {
}
