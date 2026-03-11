package com.portfolio.cv.domain.repository;

import com.portfolio.cv.domain.model.Hobby;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HobbyRepository extends JpaRepository<Hobby, UUID> {
}
