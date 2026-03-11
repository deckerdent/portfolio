package com.portfolio.cv.domain.repository;

import com.portfolio.cv.domain.model.Competence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CompetenceRepository extends JpaRepository<Competence, UUID> {
}
