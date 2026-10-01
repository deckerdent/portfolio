package com.portfolio.portal.host;

import com.portfolio.portal.host.model.ConfigProp;
import com.portfolio.portal.host.model.PortalHost;
import com.portfolio.portal.host.repository.ConfigPropRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * JPA-backed implementation of {@link HostConfigService}.
 *
 * <p>
 * All JPA calls are wrapped in
 * {@code Mono.fromCallable(...).subscribeOn(Schedulers.boundedElastic())}
 * to avoid blocking the Netty event-loop threads used by Spring WebFlux.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HostConfigServiceImpl implements HostConfigService {

    private final ConfigPropRepository repository;
    private final PortalHost host;

    /**
     * Load all persisted config props into the {@link PortalHost} bean at startup.
     * Runs asynchronously so the application context is never blocked during boot.
     * Failures are caught and logged — the host falls back to defaults if the DB is
     * not yet reachable or the table is empty.
     */
    @PostConstruct
    public void init() {
        refresh().subscribe(
                null,
                e -> log.warn("Could not pre-load host configuration from database: {}", e.getMessage()));
    }

    /** Return all stored config props. */
    @Override
    public Mono<List<ConfigProp>> findAll() {
        return Mono.fromCallable(repository::findAll)
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Create a new prop if the key is absent, or update its value if it already
     * exists.
     * Triggers a {@link #refresh()} so the {@link PortalHost} bean reflects the
     * change.
     */
    @Override
    public Mono<ConfigProp> upsert(String key, String value) {
        return Mono.fromCallable(() -> {
            ConfigProp prop = repository.findByKey(key)
                    .orElse(ConfigProp.builder().key(key).build());
            prop.setValue(value);
            return repository.save(prop);
        })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(saved -> refresh().thenReturn(saved));
    }

    /**
     * Update the value of an existing prop by key.
     * Emits a {@code 404 ResponseStatusException} if the key does not exist.
     * Triggers a {@link #refresh()} on success.
     */
    @Override
    public Mono<ConfigProp> update(String key, String value) {
        return Mono.fromCallable(() -> repository.findByKey(key)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Config prop not found: " + key)))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(prop -> {
                    prop.setValue(value);
                    return Mono.fromCallable(() -> repository.save(prop))
                            .subscribeOn(Schedulers.boundedElastic());
                })
                .flatMap(saved -> refresh().thenReturn(saved));
    }

    /**
     * Delete a prop by key.
     * Emits a {@code 404 ResponseStatusException} if the key does not exist.
     * Triggers a {@link #refresh()} on success.
     */
    @Override
    public Mono<Void> delete(String key) {
        return Mono.<Void>fromRunnable(() -> {
            if (!repository.existsByKey(key)) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Config prop not found: " + key);
            }
            repository.deleteByKey(key);
        })
                .subscribeOn(Schedulers.boundedElastic())
                .then(refresh());
    }

    /**
     * Return {@code true} if both {@code title} and {@code basePath} props exist
     * and are non-blank; {@code false} otherwise.
     */
    @Override
    public Mono<Boolean> isInitialized() {
        return findAll().map(props -> {
            Map<String, String> map = props.stream()
                    .collect(Collectors.toMap(ConfigProp::getKey, ConfigProp::getValue));
            return map.containsKey("title") && !map.get("title").isBlank()
                    && map.containsKey("basePath") && !map.get("basePath").isBlank();
        });
    }

    /**
     * Re-read all props from the database and apply them to the {@link PortalHost}
     * bean.
     * Called automatically after every write operation.
     */
    @Override
    public Mono<Void> refresh() {
        return findAll()
                .doOnNext(props -> {
                    Map<String, String> map = props.stream()
                            .collect(Collectors.toMap(ConfigProp::getKey, ConfigProp::getValue));
                    host.applyProps(map);
                })
                .then();
    }
}
