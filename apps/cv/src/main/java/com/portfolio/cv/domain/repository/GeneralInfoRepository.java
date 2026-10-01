package com.portfolio.cv.domain.repository;

import com.portfolio.cv.domain.model.GeneralInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GeneralInfoRepository extends JpaRepository<GeneralInfo, UUID> {

    /** Returns the one (and only) GeneralInfo row, if it exists. */
    Optional<GeneralInfo> findTopBy();
}
