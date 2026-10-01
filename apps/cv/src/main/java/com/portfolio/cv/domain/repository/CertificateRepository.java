package com.portfolio.cv.domain.repository;

import com.portfolio.cv.domain.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CertificateRepository extends JpaRepository<Certificate, UUID> {
}
