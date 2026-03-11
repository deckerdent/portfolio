package com.portfolio.cv.domain.service;

import com.portfolio.cv.domain.model.GeneralInfo;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface GeneralInfoService {

    Mono<Optional<GeneralInfo>> findOrEmpty();

    Mono<GeneralInfo> upsert(GeneralInfo generalInfo);
}
