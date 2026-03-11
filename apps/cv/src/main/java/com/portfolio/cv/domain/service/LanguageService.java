package com.portfolio.cv.domain.service;

import com.portfolio.cv.domain.model.Language;
import com.portfolio.cv.domain.repository.LanguageRepository;
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
public class LanguageService implements CrudService<Language> {

    private final LanguageRepository repository;

    @Override
    public Mono<List<Language>> findAll() {
        return Mono.fromCallable(repository::findAll)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Language> findById(UUID id) {
        return Mono.fromCallable(() -> repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Language not found: " + id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Language> create(Language entity) {
        return Mono.fromCallable(() -> repository.save(entity))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Language> update(UUID id, Language incoming) {
        return Mono.fromCallable(() -> {
            Language entity = repository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Language not found: " + id));
            entity.setName(incoming.getName());
            entity.setLevel(incoming.getLevel());
            return repository.save(entity);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> delete(UUID id) {
        return Mono.fromCallable(() -> {
            if (!repository.existsById(id)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Language not found: " + id);
            }
            repository.deleteById(id);
            return null;
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
