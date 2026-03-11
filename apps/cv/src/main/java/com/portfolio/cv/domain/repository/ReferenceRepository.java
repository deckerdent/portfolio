package com.portfolio.cv.domain.repository;

import com.portfolio.cv.domain.model.Reference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReferenceRepository extends JpaRepository<Reference, UUID> {
}
