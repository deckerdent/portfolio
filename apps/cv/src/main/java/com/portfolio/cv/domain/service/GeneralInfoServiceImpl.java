package com.portfolio.cv.domain.service;

import com.portfolio.cv.domain.model.GeneralInfo;
import com.portfolio.cv.domain.repository.GeneralInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GeneralInfoServiceImpl implements GeneralInfoService {

    private final GeneralInfoRepository repository;

    @Override
    public Mono<Optional<GeneralInfo>> findOrEmpty() {
        return Mono.fromCallable(() -> repository.findTopBy())
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<GeneralInfo> upsert(GeneralInfo incoming) {
        return Mono.fromCallable(() -> {
            GeneralInfo entity = repository.findTopBy().orElse(new GeneralInfo());
            entity.setFirstName(incoming.getFirstName());
            entity.setLastName(incoming.getLastName());
            entity.setMaritalStatus(incoming.getMaritalStatus());
            entity.setNumberOfChildren(incoming.getNumberOfChildren());
            entity.setDateOfBirth(incoming.getDateOfBirth());
            entity.setPlaceOfBirth(incoming.getPlaceOfBirth());
            entity.setNationality(incoming.getNationality());
            entity.setImageUrl(incoming.getImageUrl());
            entity.setSummary(incoming.getSummary());
            return repository.save(entity);
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
