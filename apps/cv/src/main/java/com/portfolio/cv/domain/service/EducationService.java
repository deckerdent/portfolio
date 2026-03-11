package com.portfolio.cv.domain.service;

import com.portfolio.cv.domain.model.Education;
import com.portfolio.cv.domain.repository.EducationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EducationService implements CrudService<Education> {

    private final EducationRepository repository;

    @Override
    public Mono<List<Education>> findAll() {
        return Mono.fromCallable(repository::findAll)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Education> findById(UUID id) {
        return Mono.fromCallable(() -> repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Education not found: " + id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Education> create(Education entity) {
        return Mono.fromCallable(() -> repository.save(entity))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Education> update(UUID id, Education incoming) {
        return Mono.fromCallable(() -> {
            Education entity = repository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Education not found: " + id));
            entity.setTitle(incoming.getTitle());
            entity.setSchoolName(incoming.getSchoolName());
            entity.setStartDate(incoming.getStartDate());
            entity.setEndDate(incoming.getEndDate());
            entity.setLocation(incoming.getLocation());
            entity.setDescription(incoming.getDescription());
            return repository.save(entity);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> delete(UUID id) {
        return Mono.fromCallable(() -> {
            if (!repository.existsById(id)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Education not found: " + id);
            }
            repository.deleteById(id);
            return null;
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
