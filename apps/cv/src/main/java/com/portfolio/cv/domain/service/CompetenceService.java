package com.portfolio.cv.domain.service;

import com.portfolio.cv.domain.model.Competence;
import com.portfolio.cv.domain.repository.CompetenceRepository;
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
public class CompetenceService implements CrudService<Competence> {

    private final CompetenceRepository repository;

    @Override
    public Mono<List<Competence>> findAll() {
        return Mono.fromCallable(repository::findAll)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Competence> findById(UUID id) {
        return Mono.fromCallable(() -> repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Competence not found: " + id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Competence> create(Competence entity) {
        return Mono.fromCallable(() -> repository.save(entity))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Competence> update(UUID id, Competence incoming) {
        return Mono.fromCallable(() -> {
            Competence entity = repository.findById(id)
                    .orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Competence not found: " + id));
            entity.setDescription(incoming.getDescription());
            return repository.save(entity);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> delete(UUID id) {
        return Mono.fromCallable(() -> {
            if (!repository.existsById(id)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Competence not found: " + id);
            }
            repository.deleteById(id);
            return null;
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
