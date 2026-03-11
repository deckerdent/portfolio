package com.portfolio.cv.domain.service;

import com.portfolio.cv.domain.model.Certificate;
import com.portfolio.cv.domain.repository.CertificateRepository;
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
public class CertificateService implements CrudService<Certificate> {

    private final CertificateRepository repository;

    @Override
    public Mono<List<Certificate>> findAll() {
        return Mono.fromCallable(repository::findAll)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Certificate> findById(UUID id) {
        return Mono.fromCallable(() -> repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate not found: " + id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Certificate> create(Certificate entity) {
        return Mono.fromCallable(() -> repository.save(entity))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Certificate> update(UUID id, Certificate incoming) {
        return Mono.fromCallable(() -> {
            Certificate entity = repository.findById(id)
                    .orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate not found: " + id));
            entity.setTitle(incoming.getTitle());
            entity.setIssuingOrganization(incoming.getIssuingOrganization());
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
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate not found: " + id);
            }
            repository.deleteById(id);
            return null;
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
