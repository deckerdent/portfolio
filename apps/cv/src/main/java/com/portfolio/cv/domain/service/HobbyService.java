package com.portfolio.cv.domain.service;

import com.portfolio.cv.domain.model.Hobby;
import com.portfolio.cv.domain.repository.HobbyRepository;
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
public class HobbyService implements CrudService<Hobby> {

    private final HobbyRepository repository;

    @Override
    public Mono<List<Hobby>> findAll() {
        return Mono.fromCallable(repository::findAll)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Hobby> findById(UUID id) {
        return Mono.fromCallable(() -> repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hobby not found: " + id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Hobby> create(Hobby entity) {
        return Mono.fromCallable(() -> repository.save(entity))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Hobby> update(UUID id, Hobby incoming) {
        return Mono.fromCallable(() -> {
            Hobby entity = repository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hobby not found: " + id));
            entity.setName(incoming.getName());
            return repository.save(entity);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> delete(UUID id) {
        return Mono.fromCallable(() -> {
            if (!repository.existsById(id)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Hobby not found: " + id);
            }
            repository.deleteById(id);
            return null;
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
