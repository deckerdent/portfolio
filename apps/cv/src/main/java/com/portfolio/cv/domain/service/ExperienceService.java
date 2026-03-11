package com.portfolio.cv.domain.service;

import com.portfolio.cv.domain.model.ProfessionalExperience;
import com.portfolio.cv.domain.repository.ProfessionalExperienceRepository;
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
public class ExperienceService implements CrudService<ProfessionalExperience> {

    private final ProfessionalExperienceRepository repository;

    @Override
    public Mono<List<ProfessionalExperience>> findAll() {
        return Mono.fromCallable(repository::findAll)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<ProfessionalExperience> findById(UUID id) {
        return Mono.fromCallable(() -> repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Experience not found: " + id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<ProfessionalExperience> create(ProfessionalExperience entity) {
        return Mono.fromCallable(() -> repository.save(entity))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<ProfessionalExperience> update(UUID id, ProfessionalExperience incoming) {
        return Mono.fromCallable(() -> {
            ProfessionalExperience entity = repository.findById(id)
                    .orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Experience not found: " + id));
            entity.setTitle(incoming.getTitle());
            entity.setCompanyName(incoming.getCompanyName());
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
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Experience not found: " + id);
            }
            repository.deleteById(id);
            return null;
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
