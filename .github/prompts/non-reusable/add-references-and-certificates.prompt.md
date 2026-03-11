---
agent: agent
---

# Add References and Certificates to the CV Backend

## Context

- The `apps/cv` module is a Spring Boot (WebFlux) application backed by PostgreSQL.
- All domain slices follow the same layered pattern: **OpenAPI spec → generated model → JPA Entity → Repository → Service (`CrudService<T>`) → MapStruct Mapper → Controller (`CvApi` impl)**.
- The OpenAPI spec lives at `apps/cv/src/main/resources/openapi/api.yml` and is the single source of truth for request/response DTOs (code-generated via Gradle).
- Timeline-based entities (e.g., `Education`, `ProfessionalExperience`) extend `TimelineEntry`, which already provides `title`, `startDate`, `endDate`, `location`, and `description`.
- Non-timeline entities (e.g., `Skill`, `Language`, `Hobby`, `Competence`) extend `BaseEntity` directly, which provides `id`, `createdAt`, and `updatedAt`.
- `CvDataLoader` seeds the database on startup by deserializing `apps/cv/src/main/resources/static/public/cv.json` into domain entities via Jackson.
- Existing enums (e.g., `MaritalStatus`) are declared in the OpenAPI spec and generated as Java enums.

## Objective

Implement full CRUD support for two new domain concepts — **References** and **Certificates** — following the exact same architectural patterns used by all existing domain slices in the `apps/cv` module, and seed both with realistic dummy data in `cv.json`.

## Requirements

### References

- [ ] Create a JPA entity `Reference` extending `BaseEntity` with the following fields:
  - `firstName` (`String`, required)
  - `lastName` (`String`, required)
  - `description` (`String`, `TEXT`, required)
  - `relation` (`ReferenceRelation` enum, **optional/nullable**)
- [ ] Declare a new enum `ReferenceRelation` in the OpenAPI spec with values `COWORKER` and `MANAGER`.
- [ ] Add OpenAPI schemas `ReferenceResponse` (extends `BaseResponse`) and `ReferenceRequest` with all fields; `relation` must be marked `nullable: true`.
- [ ] Add CRUD paths `/cv/references` and `/cv/references/{id}` to the OpenAPI spec (GET list, POST, GET by id, PUT, DELETE) with tag `References`.
- [ ] Create `ReferenceRepository extends JpaRepository<Reference, UUID>`.
- [ ] Create `ReferenceService implements CrudService<Reference>` using `Mono.fromCallable(...).subscribeOn(Schedulers.boundedElastic())` pattern.
- [ ] Create `ReferenceMapper` (MapStruct) mapping between `Reference` entity and `ReferenceResponse`/`ReferenceRequest`.
- [ ] Implement all generated `CvApi` operations for references in `CvController`.
- [ ] Add `references` field to `CvDataLoader.CvData` and wire the repository save in `onApplicationReady`.

### Certificates

- [ ] Create a JPA entity `Certificate` extending `TimelineEntry` (inheriting `title`, `startDate`, `endDate`, `location`, `description`) with one additional field:
  - `issuingOrganization` (`String`, required)
- [ ] Add OpenAPI schemas `CertificateResponse` (extends `BaseResponse`) and `CertificateRequest` including all `TimelineEntry` fields plus `issuingOrganization`.
- [ ] Add CRUD paths `/cv/certificates` and `/cv/certificates/{id}` to the OpenAPI spec (GET list, POST, GET by id, PUT, DELETE) with tag `Certificates`.
- [ ] Create `CertificateRepository extends JpaRepository<Certificate, UUID>`.
- [ ] Create `CertificateService implements CrudService<Certificate>` using the same reactive wrapper pattern.
- [ ] Create `CertificateMapper` (MapStruct) using `DateMapper` as in existing timeline mappers.
- [ ] Implement all generated `CvApi` operations for certificates in `CvController`.
- [ ] Add `certificates` field to `CvDataLoader.CvData` and wire the repository save in `onApplicationReady`.

### Dummy Data

- [ ] Add a `"references"` array to `cv.json` with **at least 3** realistic entries — include a mix of `COWORKER` and `MANAGER` relations, and at least one entry without a `relation` (null/omitted).
- [ ] Add a `"certificates"` array to `cv.json` with **at least 3** realistic entries using plausible IT/professional certifications (e.g., AWS, Scrum, Java) with real-looking dates and issuing organizations.

## Technical Specifications

- **Language / Runtime**: Java 21, Spring Boot (WebFlux), Gradle (Kotlin DSL)
- **Persistence**: Spring Data JPA + Hibernate, PostgreSQL
- **DTO generation**: OpenAPI Generator via Gradle — only edit `api.yml`, never hand-write DTOs
- **Mapping**: MapStruct (`@Mapper(componentModel = "spring", uses = DateMapper.class)` for timeline mappers)
- **Reactive pattern**: Wrap blocking JPA calls in `Mono.fromCallable(() -> ...).subscribeOn(Schedulers.boundedElastic())`
- **Naming conventions**: Follow existing package structure `com.portfolio.cv.domain.{model,repository,service,mapper}` and `com.portfolio.cv.controller`
- **Database schema**: Hibernate `ddl-auto` manages schema; no manual SQL migration needed
- **Lombok**: Use `@Entity`, `@Table`, `@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor` on entities, consistent with existing models
- **OpenAPI tag registration**: Add `References` and `Certificates` to the top-level `tags` list in `api.yml`

## Constraints

- Do **not** modify any existing entity, service, mapper, or controller method — only add new code and extend `CvController` with new method implementations.
- The `CvDataLoader` guard (`if (generalInfoRepository.count() > 0)`) means dummy data is only loaded on a fresh database; no changes to this logic are needed.
- `relation` on `Reference` must be stored as a string enum in the database (`@Enumerated(EnumType.STRING)`).
- `CvData` inner class uses `@JsonIgnoreProperties(ignoreUnknown = true)` — new fields just need to be added as list properties.
- Maintain the existing OpenAPI response wrapper pattern: list endpoints return `Flux<XResponse>` wrapped in `Mono<ResponseEntity<Flux<XResponse>>>`.

## Success Criteria

- [ ] Application starts without errors with the new entities present.
- [ ] `GET /api/cv/references` returns a non-empty JSON array after a fresh startup with `cv.json` dummy data.
- [ ] `GET /api/cv/certificates` returns a non-empty JSON array after a fresh startup with `cv.json` dummy data.
- [ ] All CRUD operations (GET list, GET by id, POST, PUT, DELETE) work correctly for both `/cv/references` and `/cv/certificates`.
- [ ] `GET /api/cv/references/{id}` returns `404` for an unknown UUID.
- [ ] A `Reference` entry with no `relation` field in `cv.json` is persisted with `null` relation and returned as `null` in the API response.
- [ ] `CertificateResponse` includes all `TimelineEntry` fields (`title`, `startDate`, `endDate`, `location`, `description`) plus `issuingOrganization`.
- [ ] The generated OpenAPI client (Gradle build) compiles without errors after the spec changes.

## Examples

### `cv.json` — references array

```json
"references": [
  {
    "firstName": "Anna",
    "lastName": "Müller",
    "description": "Marcel consistently delivered high-quality features and was a reliable team member throughout our joint project.",
    "relation": "MANAGER"
  },
  {
    "firstName": "Jonas",
    "lastName": "Weber",
    "description": "A highly motivated developer with a strong sense of ownership and excellent communication skills.",
    "relation": "COWORKER"
  },
  {
    "firstName": "Sophie",
    "lastName": "Braun",
    "description": "Marcel's architectural decisions significantly improved our system's scalability.",
    "relation": null
  }
]
```

### `cv.json` — certificates array

```json
"certificates": [
  {
    "title": "AWS Certified Developer – Associate",
    "issuingOrganization": "Amazon Web Services",
    "startDate": "2023-04-01",
    "endDate": "2026-04-01",
    "location": null,
    "description": "Validates proficiency in developing and maintaining applications on the AWS platform."
  },
  {
    "title": "Professional Scrum Master I (PSM I)",
    "issuingOrganization": "Scrum.org",
    "startDate": "2021-09-15",
    "endDate": null,
    "location": null,
    "description": "Demonstrates understanding of Scrum theory and its application."
  },
  {
    "title": "Oracle Certified Professional: Java SE 11 Developer",
    "issuingOrganization": "Oracle",
    "startDate": "2022-06-01",
    "endDate": "2025-06-01",
    "location": null,
    "description": "Certifies advanced knowledge of Java SE 11 programming."
  }
]
```

### OpenAPI — Reference schemas (excerpt)

```yaml
ReferenceRelation:
  type: string
  enum:
    - COWORKER
    - MANAGER

ReferenceResponse:
  allOf:
    - $ref: "#/components/schemas/BaseResponse"
    - type: object
      required: [firstName, lastName, description]
      properties:
        firstName:
          type: string
        lastName:
          type: string
        description:
          type: string
        relation:
          $ref: "#/components/schemas/ReferenceRelation"
          nullable: true

ReferenceRequest:
  type: object
  required: [firstName, lastName, description]
  properties:
    firstName:
      type: string
    lastName:
      type: string
    description:
      type: string
    relation:
      $ref: "#/components/schemas/ReferenceRelation"
      nullable: true
```

### OpenAPI — Certificate schemas (excerpt)

```yaml
CertificateResponse:
  allOf:
    - $ref: "#/components/schemas/BaseResponse"
    - type: object
      required: [title, issuingOrganization, startDate]
      properties:
        title:
          type: string
        issuingOrganization:
          type: string
        startDate:
          type: string
          format: date
        endDate:
          type: string
          format: date
          nullable: true
        location:
          type: string
        description:
          type: string

CertificateRequest:
  type: object
  required: [title, issuingOrganization, startDate]
  properties:
    title:
      type: string
    issuingOrganization:
      type: string
    startDate:
      type: string
      format: date
    endDate:
      type: string
      format: date
      nullable: true
    location:
      type: string
    description:
      type: string
```

## Additional Notes

- The **order of implementation** matters for compilation: (1) update `api.yml` and run Gradle code-gen, (2) create entity, (3) create repository, (4) create service, (5) create mapper, (6) wire controller, (7) update `CvDataLoader`, (8) update `cv.json`.
- `@Enumerated(EnumType.STRING)` is required on the `relation` field of `Reference` so the DB stores `"COWORKER"` / `"MANAGER"` instead of ordinal integers.
- For `Certificate`, the `update` method in the service must copy all inherited `TimelineEntry` fields (title, startDate, endDate, location, description) as well as `issuingOrganization` — refer to `EducationService.update` for the exact pattern.
- `endDate` on certificates is nullable (a certificate may not expire); handle this consistently in both the spec and the entity.
