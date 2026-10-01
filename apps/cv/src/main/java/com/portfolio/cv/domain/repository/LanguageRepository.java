package com.portfolio.cv.domain.repository;

import com.portfolio.cv.domain.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LanguageRepository extends JpaRepository<Language, UUID> {
}
