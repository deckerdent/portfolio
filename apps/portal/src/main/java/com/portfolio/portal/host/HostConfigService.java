package com.portfolio.portal.host;

import com.portfolio.portal.host.model.ConfigProp;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Contract for managing host configuration properties.
 *
 * <p>
 * All operations return reactive types so implementations may choose
 * between blocking (JPA/JDBC wrapped on a bounded-elastic scheduler) or
 * fully non-blocking (R2DBC) persistence without changing callers.
 * </p>
 */
public interface HostConfigService {

    /** Return all stored config props. */
    Mono<List<ConfigProp>> findAll();

    /**
     * Create a new prop if the key is absent, or update its value if it
     * already exists. Triggers a {@link #refresh()} so the host bean reflects
     * the change.
     */
    Mono<ConfigProp> upsert(String key, String value);

    /**
     * Update the value of an existing prop by key.
     * Emits a {@code 404 ResponseStatusException} if the key does not exist.
     * Triggers a {@link #refresh()} on success.
     */
    Mono<ConfigProp> update(String key, String value);

    /**
     * Delete a prop by key.
     * Emits a {@code 404 ResponseStatusException} if the key does not exist.
     * Triggers a {@link #refresh()} on success.
     */
    Mono<Void> delete(String key);

    /**
     * Return {@code true} if both {@code title} and {@code basePath} props
     * exist and are non-blank; {@code false} otherwise.
     */
    Mono<Boolean> isInitialized();

    /**
     * Re-read all props from the database and apply them to the host bean.
     * Called automatically after every write operation.
     */
    Mono<Void> refresh();
}
