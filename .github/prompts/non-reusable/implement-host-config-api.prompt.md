---
agent: agent
---

# Implement Host Configuration API (Backend)

## Context

- **Project**: `apps/portal` — a Spring Boot 3.3.6 / WebFlux (reactive, non-blocking) application backed by PostgreSQL.
- **Base package**: `com.portfolio.portal`; all REST controllers are automatically prefixed `/api` via `WebConfig.configurePathMatching`.
- **Build**: Gradle with `openApiGenerate` (generator: `spring`, reactive: `true`, interfaceOnly: `true`). All API interfaces and model POJOs are generated from `src/main/resources/openapi/api.yml` into `com.portfolio.portal.api` / `com.portfolio.portal.model`. Controllers implement generated interfaces.
- **Frontend counterpart**: `libs/browser/host/src/model/Host.ts` and `libs/browser/host/src/types.d.ts` define the shape already consumed by the frontend:
  ```typescript
  interface HostConfig {
      title: string;
      basePath?: string;       // default "/"
      slots: string[];         // runtime-only, NOT persisted
      sourceUrls?: string[];   // default []
      appsUrl?: string;        // default "/apps.json"
  }
  ```
- **Existing stack**: Lombok, MapStruct, Spring Data JPA (with R2DBC excluded — JPA is used with a blocking scheduler), Spring WebFlux, Testcontainers + PostgreSQL already on the test classpath, `reactor-test` available.
- **Test patterns**: Unit tests use `@SpringBootTest` + `WebTestClient` + `@TestPropertySource` (H2 in-mem). E2E tests use `@Testcontainers` + `@Container` with `PostgreSQLContainer`. See `HelloControllerTest.java` for the existing pattern.
- **OpenAPI spec** lives at `src/main/resources/openapi/api.yml`; new paths and schemas must be appended there.

## Objective

Implement a backend API for persisting and retrieving the host shell configuration. The storage model separates `Host` (the domain aggregate) from `ConfigProp` (a flexible key-value table), so arbitrary config properties can be added without schema migrations.

## Requirements

### Data Model
- [ ] Create a `ConfigProp` JPA entity (`@Entity`, `@Table(name = "config_props")`) with:
  - `id` — `UUID`, auto-generated primary key
  - `key` — `String`, `@Column(unique = true, nullable = false)`
  - `value` — `String`, `@Column(nullable = false)`
  - `createdAt` / `updatedAt` — `Instant`, managed automatically
  - Lombok `@Data` / `@Builder` / `@NoArgsConstructor` / `@AllArgsConstructor`
- [ ] Create a `Host` Spring `@Component` (or `@Service`) bean that:
  - Holds typed properties mirroring the frontend model: `title`, `basePath`, `sourceUrls`, `appsUrl`
  - At construction/init time, reads all `ConfigProp` rows and populates the typed properties with safe defaults (`basePath` → `"/"`, `sourceUrls` → `[]`, `appsUrl` → `"/apps.json"`)
  - Exposes a `Map<String, String> configProps` field containing the **full raw set** of persisted key-value pairs
  - Known keys: `"title"`, `"basePath"`, `"sourceUrls"` (comma-separated list), `"appsUrl"`

### Repository
- [ ] Create `ConfigPropRepository extends ReactiveCrudRepository<ConfigProp, UUID>` (R2DBC) **or** `JpaRepository<ConfigProp, UUID>` wrapped in `Mono.fromCallable(...).subscribeOn(Schedulers.boundedElastic())` to stay non-blocking in WebFlux context — follow whichever pattern the existing datasource config implies (JPA is already enabled)
- [ ] Add a derived query `findByKey(String key): Optional<ConfigProp>` (or reactive equivalent)

### OpenAPI Spec (`api.yml`)
- [ ] Add schemas:
  - `ConfigProp` — `{ id: string (uuid), key: string, value: string, createdAt: string (date-time), updatedAt: string (date-time) }`
  - `HostResponse` — `{ title: string, basePath: string, sourceUrls: string[], appsUrl: string, configProps: object (additionalProperties: string) }`
  - `ConfigPropRequest` — `{ key: string (required), value: string (required) }`
- [ ] Add paths:
  - `GET  /host`              → `HostResponse` (200)
  - `GET  /host/config`       → `ConfigProp[]` (200)
  - `POST /host/config`       → `ConfigProp` (201) — create or upsert a single prop
  - `PUT  /host/config/{key}` → `ConfigProp` (200) — update value by key
  - `DELETE /host/config/{key}` → 204 — delete by key
  - `GET  /host/initialized`  → `{ initialized: boolean }` (200)

### Controller
- [ ] Create `HostController implements HostApi` (generated interface):
  - `GET /api/host` — builds and returns a `HostResponse` from the `Host` bean
  - `GET /api/host/config` — returns all `ConfigProp` rows as a list
  - `POST /api/host/config` — saves a new `ConfigProp`; refreshes the `Host` bean state
  - `PUT /api/host/config/{key}` — updates an existing prop by key; refreshes `Host` bean
  - `DELETE /api/host/config/{key}` — deletes by key; refreshes `Host` bean
  - `GET /api/host/initialized` — returns `{ initialized: false }` when the `config_props` table is empty **or** when either `"title"` or `"basePath"` key is absent/blank; otherwise `{ initialized: true }`

### Service
- [ ] Create `HostConfigService` that encapsulates all business logic:
  - `Mono<List<ConfigProp>> findAll()`
  - `Mono<ConfigProp> upsert(String key, String value)`
  - `Mono<ConfigProp> update(String key, String value)` — throws / returns 404 if key not found
  - `Mono<Void> delete(String key)`
  - `Mono<Boolean> isInitialized()` — true only if `"title"` and `"basePath"` props exist and are non-blank
  - `Mono<Void> refresh()` — re-reads all props and updates the `Host` bean

### Database Migration
- [ ] Add a Flyway or Liquibase migration (whichever is already on the classpath, or plain SQL init script) creating the `config_props` table with all columns and a unique index on `key`

### Unit Tests (JUnit 5 + Mockito)
- [ ] `HostConfigServiceTest` — mock `ConfigPropRepository`; test:
  - `isInitialized()` returns `false` when table is empty
  - `isInitialized()` returns `false` when only one required key is present
  - `isInitialized()` returns `true` when both `title` and `basePath` are set
  - `upsert()` creates a new prop if key is absent
  - `upsert()` updates an existing prop if key is present
  - `update()` returns error signal when key not found
- [ ] `HostControllerTest` — `@SpringBootTest` + `WebTestClient` (H2 in-mem, DDL from test resources):
  - `GET /api/host/initialized` returns `{ initialized: false }` on empty DB
  - `POST /api/host/config` with `{ key: "title", value: "My Portal" }` returns 201
  - `GET /api/host` returns correct `HostResponse` after seeding `title` and `basePath`
  - `DELETE /api/host/config/title` returns 204 and subsequent `/initialized` returns false

### End-to-End Tests (Testcontainers + PostgreSQL)
- [ ] `HostConfigE2ETest` annotated with `@Testcontainers`, `@SpringBootTest(webEnvironment = RANDOM_PORT)`:
  - Spin up `PostgreSQLContainer` and wire `spring.datasource.url/username/password`
  - Full happy-path: seed → read → update → delete → check initialized
  - Verify that `GET /api/host` reflects updated values after a `PUT /api/host/config/basePath`
  - Verify that `GET /api/host/initialized` transitions correctly

## Technical Specifications

- **Language**: Java 17
- **Framework**: Spring Boot 3.3.6 — WebFlux (reactive); JPA with `Schedulers.boundedElastic()` wrapping for blocking DB calls
- **ORM**: Spring Data JPA + Hibernate; entity must include `@EntityListeners(AuditingEntityListener.class)` or manual `@PrePersist` / `@PreUpdate` for timestamps
- **API contract**: Code-first via OpenAPI generator — always add to `api.yml` first, then implement the generated interface
- **Lombok**: Use `@Data`, `@Builder`, `@AllArgsConstructor`, `@NoArgsConstructor` on entities and DTOs; avoid on JPA entities where Lombok `equals`/`hashCode` causes issues — prefer `@Getter @Setter @Builder` on entities
- **MapStruct**: Use a `HostConfigMapper` to convert `ConfigProp` ↔ generated `com.portfolio.portal.model.ConfigProp` POJO if names differ
- **Error handling**: Return `404` with a structured error body when a key is not found on `PUT` or `DELETE`
- **Validation**: `@NotBlank` on `ConfigPropRequest.key` and `ConfigPropRequest.value`; enforce via `spring-boot-starter-validation`

## Constraints

- Must not introduce blocking calls on the WebFlux event loop — all JPA calls must be wrapped with `Mono.fromCallable(...).subscribeOn(Schedulers.boundedElastic())`
- `slots` is a **runtime-only** property discovered from the DOM and must NOT be persisted or exposed in `HostResponse`
- The `Host` bean must be re-hydrated after any write operation (upsert / update / delete) so subsequent `GET /api/host` calls return fresh data
- Testcontainers tests must be isolated — each test method should clean the table or use `@Transactional` / `@Sql` reset
- Do not modify `WebConfig.java` — the `/api` prefix is already applied globally

## Success Criteria

- [ ] `config_props` table is created by migration with `id`, `key` (unique), `value`, `created_at`, `updated_at`
- [ ] `GET /api/host/initialized` returns `{ "initialized": false }` on a fresh empty database
- [ ] `GET /api/host/initialized` returns `{ "initialized": true }` once both `title` and `basePath` props are stored
- [ ] `GET /api/host` returns a JSON object containing `title`, `basePath`, `sourceUrls` (array), `appsUrl` (string), and `configProps` (object)
- [ ] `sourceUrls` is serialized/deserialized as a JSON array from a comma-separated stored value
- [ ] All unit tests pass with `./gradlew test`
- [ ] All E2E Testcontainers tests pass against a real PostgreSQL container
- [ ] OpenAPI spec is valid and Swagger UI shows all new endpoints at `/swagger-ui`
- [ ] No blocking calls on the reactor thread (verify with `BlockHound` or code review)

## Examples

### Stored `config_props` rows after initialization
```
key          | value
-------------|-----------------------------
title        | My Portfolio
basePath     | /
sourceUrls   | /sources.json,/extra.json
appsUrl      | /apps.json
```

### `GET /api/host` response
```json
{
  "title": "My Portfolio",
  "basePath": "/",
  "sourceUrls": ["/sources.json", "/extra.json"],
  "appsUrl": "/apps.json",
  "configProps": {
    "title": "My Portfolio",
    "basePath": "/",
    "sourceUrls": "/sources.json,/extra.json",
    "appsUrl": "/apps.json"
  }
}
```

### `GET /api/host/initialized` — uninitialized
```json
{ "initialized": false }
```

### `POST /api/host/config` request body
```json
{ "key": "title", "value": "My Portfolio" }
```

## Additional Notes

- `sourceUrls` is stored as a single comma-separated string in the `value` column and split into a `List<String>` when building the `Host` bean — keep the conversion in `HostConfigService` or the `Host` bean itself.
- Consider adding a `@PostConstruct` on the `Host` bean that calls the service to load initial props; handle the case where the DB is empty gracefully (all fields fall back to defaults).
- The `Host` bean must be `@RefreshScope` or managed refresh must be triggered manually via `HostConfigService.refresh()` after writes — prefer explicit refresh over `@RefreshScope` to avoid the Spring Cloud dependency.
- For the E2E tests, reuse the `PostgreSQLContainer` as a `static` field shared across the test class to avoid re-spinning the container per test method.
- Flyway is preferred for migrations if not already present — add the `org.flywaydb:flyway-core` dependency and a `V1__create_config_props.sql` script under `src/main/resources/db/migration/`.
