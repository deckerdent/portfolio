package com.portfolio.portal.host.repository;

import com.portfolio.portal.host.model.ConfigProp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface ConfigPropRepository extends JpaRepository<ConfigProp, UUID> {

    Optional<ConfigProp> findByKey(String key);

    boolean existsByKey(String key);

    @Transactional
    @Modifying
    @Query("DELETE FROM ConfigProp c WHERE c.key = :key")
    void deleteByKey(@Param("key") String key);
}
