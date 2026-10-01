package com.portfolio.cv.domain.repository;

import com.portfolio.cv.domain.model.Education;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EducationRepository extends JpaRepository<Education, UUID> {
}
