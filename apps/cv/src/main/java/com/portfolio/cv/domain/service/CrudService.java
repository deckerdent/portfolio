package com.portfolio.cv.domain.service;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface CrudService<T> {

    Mono<List<T>> findAll();

    Mono<T> findById(UUID id);

    Mono<T> create(T entity);

    Mono<T> update(UUID id, T entity);

    Mono<Void> delete(UUID id);
}
