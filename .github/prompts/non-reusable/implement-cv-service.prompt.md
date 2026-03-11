---
agent: agent
---

# Implement CV Service — Backend API + Frontend Module

## Context

- **Project**: `apps/cv/` — already scaffolded as a Gradle subproject with Spring Boot 3.3.6 + Spring WebFlux, Vite + Module Federation frontend (see `setup-cv-service.prompt.md` for scaffold details)
- **Ports**: backend `8081`, Vite dev `4203`
- **Reference implementation**: `apps/portal/` — follow its coding conventions (reactive JPA wrapped in `Schedulers.boundedElastic()`, OpenAPI-first, MapStruct mappers, Lombok, controller implements generated interface)
- **OpenAPI Generator**: configured in `build.gradle`, generates interfaces into `com.portfolio.cv.api`, models into `com.portfolio.cv.model`. **After writing the spec, run `.\gradlew :apps:cv:openApiGenerate` and read the generated interface files before writing controllers** to avoid mismatched method signatures
- **Frontend**: Vue 3, TypeScript, `@awesome.me/webawesome` custom elements, Module Federation remote (no host bootstrap). Vite alias `@portfolio/core` → `libs/browser/core/src/index.ts`
- **Static override mechanism**: Spring Boot serves `file:./static/` before `classpath:/static/` — a `static/` directory next to the running JAR (or the project root in dev) overrides bundled resources. The default `cv.json` lives at `src/main/resources/static/public/cv.json`; an operator can override it by placing a file at `static/public/cv.json` beside the JAR

## Objective

Implement the full CV service: define all domain models, write an OpenAPI spec, generate and implement CRUD controllers, build a demo-data loader, and create a Vue micro-frontend module with a general info panel and tabbed views for Experience, Education, and Skills & Competences.

---

## Requirements

### 1 — Domain Models (JPA entities)

#### Base entity
- [ ] `BaseEntity` abstract class with:
  - `@Id @GeneratedValue(strategy = GenerationType.UUID) UUID id`
  - `@CreationTimestamp LocalDateTime createdAt`
  - `@UpdateTimestamp LocalDateTime updatedAt`
  - Annotated with `@MappedSuperclass`

#### Timeline entry superclass
- [ ] `TimelineEntry extends BaseEntity` abstract class for the two time-ranged types, containing:
  - `String title` (not null)
  - `LocalDate startDate` (not null)
  - `LocalDate endDate` (nullable — ongoing entries)
  - `String location` (nullable)
  - `String description` (nullable, `@Column(columnDefinition = "TEXT")`)

#### Concrete entities

| Entity | Table | Extra fields vs superclass |
|---|---|---|
| `GeneralInfo` | `general_info` | `firstName`, `lastName`, `maritalStatus` (String), `numberOfChildren` (Integer), `dateOfBirth` (LocalDate), `placeOfBirth`, `nationality`, `imageUrl`, `summary` (TEXT) — **single row** (use `@Table` + a unique constraint or just rely on the service ensuring only one row exists) |
| `ProfessionalExperience extends TimelineEntry` | `professional_experience` | `companyName` (not null) |
| `Education extends TimelineEntry` | `education` | `schoolName` (not null) |
| `Skill` extends `BaseEntity` | `skill` | `title` (not null), `level` (Integer 1–10, not null) |
| `Competence` extends `BaseEntity` | `competence` | `description` (not null) |
| `Language` extends `BaseEntity` | `language` | `name` (not null), `level` (Integer 1–10, not null) |
| `Hobby` extends `BaseEntity` | `hobby` | `name` (not null) |

- [ ] Use Lombok (`@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor`) on all entities
- [ ] All entities in package `com.portfolio.cv.domain.model`

### 2 — Repositories

- [ ] One `JpaRepository<Entity, UUID>` per entity in package `com.portfolio.cv.domain.repository`
- [ ] `GeneralInfoRepository` — add `Optional<GeneralInfo> findFirst()` for the single-row pattern

### 3 — OpenAPI Spec (`src/main/resources/openapi/api.yml`)

Write a full OpenAPI 3.0 spec under `servers: [{url: /api}]`. Use tag-per-resource grouping. All request/response schemas defined in `components/schemas`.

**Endpoints per resource:**

| Resource | Endpoints |
|---|---|
| `GeneralInfo` | `GET /cv/general-info`, `PUT /cv/general-info` (upsert — body: all fields) |
| `ProfessionalExperience` | `GET /cv/experiences`, `POST /cv/experiences`, `GET /cv/experiences/{id}`, `PUT /cv/experiences/{id}`, `DELETE /cv/experiences/{id}` |
| `Education` | `GET /cv/education`, `POST /cv/education`, `GET /cv/education/{id}`, `PUT /cv/education/{id}`, `DELETE /cv/education/{id}` |
| `Skill` | `GET /cv/skills`, `POST /cv/skills`, `GET /cv/skills/{id}`, `PUT /cv/skills/{id}`, `DELETE /cv/skills/{id}` |
| `Competence` | `GET /cv/competences`, `POST /cv/competences`, `GET /cv/competences/{id}`, `PUT /cv/competences/{id}`, `DELETE /cv/competences/{id}` |
| `Language` | `GET /cv/languages`, `POST /cv/languages`, `GET /cv/languages/{id}`, `PUT /cv/languages/{id}`, `DELETE /cv/languages/{id}` |
| `Hobby` | `GET /cv/hobbies`, `POST /cv/hobbies`, `GET /cv/hobbies/{id}`, `PUT /cv/hobbies/{id}`, `DELETE /cv/hobbies/{id}` |

**Schema conventions:**
- Response DTOs include `id` (UUID string), `createdAt`, `updatedAt` (ISO datetime strings)
- Request bodies (`*Request` schemas) omit `id`, `createdAt`, `updatedAt`
- `level` fields: `type: integer, minimum: 1, maximum: 10`
- List endpoints return `type: array`
- `DELETE` returns `204 No Content`
- All `{id}` params are `format: uuid`

- [ ] **After writing the spec, run `.\gradlew :apps:cv:openApiGenerate` and read every generated `*Api` interface in `build/generated/openapi/src/main/java/com/portfolio/cv/api/` before writing any controller** — use exact method signatures and parameter types as generated

### 4 — MapStruct Mappers

- [ ] Package `com.portfolio.cv.domain.mapper`, one mapper per entity annotated `@Mapper(componentModel = "spring")`
- [ ] Each mapper: `toDto(Entity) → ResponseDto`, `toEntity(RequestDto) → Entity`, `toDtoList(List<Entity>) → List<ResponseDto>`
- [ ] `GeneralInfoMapper`: `toDto(GeneralInfo)`, `toEntity(GeneralInfoRequest)`

### 5 — Services

- [ ] Package `com.portfolio.cv.domain.service`, interface + `Impl` class per resource
- [ ] All JPA calls wrapped: `Mono.fromCallable(() -> repo.method()).subscribeOn(Schedulers.boundedElastic())`
- [ ] `GeneralInfoService`: `findOrEmpty(): Mono<Optional<GeneralInfo>>`, `upsert(GeneralInfo): Mono<GeneralInfo>`
- [ ] Collection services (`ExperienceService`, `EducationService`, etc.): `findAll()`, `findById(UUID)`, `create(Entity)`, `update(UUID, Entity)`, `delete(UUID)` — `findById` / `update` / `delete` throw `ResponseStatusException(404)` when not found

### 6 — Controllers

- [ ] Package `com.portfolio.cv.controller`, one `@RestController` per tag, implements the generated `*Api` interface
- [ ] Use `@RequiredArgsConstructor`, inject service + mapper
- [ ] Match portal controller style: return `Mono<ResponseEntity<T>>` (or `Flux` where generated interface dictates)
- [ ] `GeneralInfoController`: `GET` returns `200` or `404` if no row exists; `PUT` upserts and returns `200`
- [ ] Delete endpoints: return `ResponseEntity.noContent().build()` (`204`)

### 7 — Flyway Migration (`src/main/resources/db/migration/V1__init.sql`)

- [ ] Create all tables matching entity definitions
- [ ] Use `UUID` primary keys (PostgreSQL `uuid` type, default via `gen_random_uuid()`)
- [ ] `general_info`: include a check constraint ensuring at most one row, or rely on application logic (document the choice)
- [ ] `skill.level` and `language.level`: add `CHECK (level BETWEEN 1 AND 10)` constraints
- [ ] All tables include `created_at TIMESTAMPTZ NOT NULL DEFAULT now()` and `updated_at TIMESTAMPTZ NOT NULL DEFAULT now()`

### 8 — Demo Data Loader

- [ ] Class `CvDataLoader` in package `com.portfolio.cv.loader`, annotated `@Component`
- [ ] Activated only when `cv.demo-data.enabled=true` (use `@ConditionalOnProperty(name = "cv.demo-data.enabled", havingValue = "true", matchIfMissing = true)`)
- [ ] Implements `ApplicationListener<ApplicationReadyEvent>` (or `@EventListener(ApplicationReadyEvent.class)` method)
- [ ] **Load order**: first attempt to read `static/public/cv.json` from the file system (external override); fall back to `classpath:/static/public/cv.json` (bundled default). Use `ResourceLoader` or `new ClassPathResource` / `new FileSystemResource`
- [ ] Parse `cv.json` with Jackson `ObjectMapper` into a `CvDataDto` POJO that mirrors the JSON structure
- [ ] **Idempotent**: skip loading if any data already exists (check `generalInfoRepository.count() > 0`)
- [ ] Populate all seven domain tables from the parsed JSON
- [ ] Log clearly: `[CvDataLoader] Loading demo data from <source>` / `[CvDataLoader] Demo data already present, skipping`

#### `cv.json` structure (place at `src/main/resources/static/public/cv.json`):
```json
{
  "generalInfo": {
    "firstName": "Jane",
    "lastName": "Doe",
    "maritalStatus": "Single",
    "numberOfChildren": 0,
    "dateOfBirth": "1990-06-15",
    "placeOfBirth": "Berlin",
    "nationality": "German",
    "imageUrl": null,
    "summary": "Passionate full-stack developer with 8 years of experience..."
  },
  "experiences": [
    {
      "title": "Senior Software Engineer",
      "companyName": "Acme Corp",
      "startDate": "2020-01-01",
      "endDate": null,
      "location": "Berlin, Germany",
      "description": "Leading frontend architecture using micro-frontends..."
    }
  ],
  "education": [
    {
      "title": "B.Sc. Computer Science",
      "schoolName": "TU Berlin",
      "startDate": "2009-10-01",
      "endDate": "2013-09-30",
      "location": "Berlin, Germany",
      "description": "Focus on distributed systems and web technologies."
    }
  ],
  "skills": [
    { "title": "TypeScript", "level": 9 },
    { "title": "Java", "level": 8 },
    { "title": "Vue.js", "level": 9 }
  ],
  "competences": [
    { "description": "Micro-frontend architecture" },
    { "description": "Agile / Scrum" }
  ],
  "languages": [
    { "name": "German", "level": 10 },
    { "name": "English", "level": 9 }
  ],
  "hobbies": [
    { "name": "Photography" },
    { "name": "Hiking" }
  ]
}
```

### 9 — `application.yml` additions

- [ ] Add under `cv:` namespace:
  ```yaml
  cv:
    demo-data:
      enabled: true
  ```

### 10 — Frontend Module (`src/main/resources/static/src/apps/cv/`)

#### Module wiring
- [ ] `Module.ts`: same `ModuleLifecycle` pattern as portal's `setup/Module.ts`
  - Import `@awesome.me/webawesome/dist/webawesome.js` and call `setBasePath(...)` (same technique as portal setup module)
  - Router with `createWebHistory(basename)`, routes:
    - `{ path: '/', redirect: '/experience' }`
    - `{ path: '/experience', name: 'experience', component: ExperienceView }`
    - `{ path: '/education', name: 'education', component: EducationView }`
    - `{ path: '/skills', name: 'skills', component: SkillsView }`
- [ ] `app/App.vue`:
  - Always renders `<GeneralInfoPanel />` at the top
  - Below it, renders a `<wa-tab-group>` with three `<wa-tab slot="nav">` tabs: "Experience", "Education", "Skills & Competences"
  - Tab panels swap via `<RouterView />` (clicking a tab navigates to the corresponding route using `router.push`)
  - On mount, set the active tab based on current route; on tab change (`wa-tab-show` event) push the matching route

#### Views
- [ ] `views/ExperienceView.vue`:
  - Fetches `GET /api/cv/experiences` on mount
  - Renders each entry as a `wa-card` with: title as heading, company + location as subheading, date range (format: `MMM yyyy – present / MMM yyyy`), description as body text
  - Timeline-style layout: vertical list, entries ordered by `startDate` descending
- [ ] `views/EducationView.vue`:
  - Same structure as ExperienceView but fetches `/api/cv/education`, shows school name instead of company
- [ ] `views/SkillsView.vue`:
  - Two sections side by side (or stacked on narrow): **Skills** and **Competences**
  - Skills: each skill rendered as a row with name + a visual level indicator (e.g. `<wa-progress-bar>` or a row of filled/empty circles using `<wa-icon>`) showing `level/10`
  - Competences: simple list of `<wa-badge>` or `<wa-tag>` elements with the description text

#### `GeneralInfoPanel.vue` (always visible, `src/apps/cv/components/`)
- [ ] Fetches `GET /api/cv/general-info` on mount; renders nothing (or a skeleton) while loading
- [ ] **Layout**: two-column on wide screens, single column on narrow — left column: avatar/image, right column: info
- [ ] **Avatar**: if `imageUrl` is set, show `<img>` with it; otherwise show a placeholder (e.g. `<wa-icon name="person" style="font-size: 4rem">` or a styled div with initials)
- [ ] **Info displayed**: full name (large), nationality + date of birth on one line, marital status + children on one line, summary as a paragraph
- [ ] **Hobbies**: fetches `GET /api/cv/hobbies` — renders them as `<wa-tag>` chips in a wrapping row beneath the summary
- [ ] **Languages**: fetches `GET /api/cv/languages` — rendered as a compact row: name + star/dot rating (similar to skills level indicator)

#### Design guidance
- Use `wa-card`, `wa-badge`, `wa-tag`, `wa-progress-bar`, `wa-icon`, `wa-tab-group`, `wa-tab`, `wa-tab-panel`, `wa-spinner` (for loading states) from WebAwesome
- **No custom CSS classes for colors/typography** — use WebAwesome CSS custom properties (`--wa-color-*`, `--wa-font-size-*`, `--wa-space-*`) inline or via `style` attributes where layout adjustments are needed
- Be creative with layout: consider using CSS grid for the general info panel, flexbox gap for skills rows, and a clean card-based timeline for experience/education
- All data fetching via `axiosInstance` from `@portfolio/core`
- Show `<wa-spinner>` while loading, `<wa-alert variant="danger">` on error

#### Federation expose
- [ ] Add to `vite.config.mts` federation exposes: `'./CvApp': './src/apps/cv/Module.ts'`

---

## Technical Specifications

- **Language**: Java 17 + TypeScript 5 / Vue 3 SFC
- **Backend**: Spring Boot 3.3.6, WebFlux, blocking JPA wrapped in `Schedulers.boundedElastic()`, Lombok, MapStruct
- **OpenAPI**: spec-first, generator produces interfaces — controllers implement generated interfaces exactly
- **Frontend**: Vue 3 Composition API (`<script setup>`), `@awesome.me/webawesome` custom elements, no Vue `v-model` on WA inputs (use `:value` + `@wa-input`)
- **HTTP client (frontend)**: `axiosInstance` from `@portfolio/core`
- **Package**: `com.portfolio.cv.*`
- **No authentication** on any endpoint (same as portal)

## Constraints

- **No custom theme CSS** — do not create a `styles.css` with color/font overrides; use WA tokens inline
- **Reactive correctness**: never block Netty threads — all JPA calls must use `Schedulers.boundedElastic()`
- **Idempotent dataloader** — must be safe to call multiple times without duplicating data
- **OpenAPI generator must run before controllers are written** — generated interfaces are the source of truth for method signatures
- **`GeneralInfo` is a singleton** — the `PUT` endpoint upserts the one row; no `POST` or `DELETE`
- Level fields (`skill.level`, `language.level`) must be validated `1–10` both at DB level (CHECK constraint) and API level (OpenAPI `minimum/maximum`)

## Success Criteria

- [ ] `.\gradlew :apps:cv:openApiGenerate` succeeds and generates interfaces in `build/generated/openapi/`
- [ ] `.\gradlew :apps:cv:build -x test` completes without errors (Java compiled, frontend built)
- [ ] `$env:DATABASE_PASSWORD="postgres"; .\gradlew :apps:cv:bootRun -x buildFrontend` starts on port `8081`
- [ ] `GET http://localhost:8081/actuator/health` returns `{"status":"UP"}`
- [ ] `GET http://localhost:8081/api/cv/general-info` returns Jane Doe's data (demo data loaded)
- [ ] `GET http://localhost:8081/api/cv/experiences` returns at least one entry
- [ ] Re-starting the app does NOT duplicate data (idempotency check)
- [ ] Setting `cv.demo-data.enabled=false` prevents the loader from running
- [ ] Vite dev server starts on `4203` with `pnpm run dev`
- [ ] `http://localhost:4203/remoteEntry.js` is served
- [ ] The frontend module renders the `GeneralInfoPanel` with name, summary, hobbies, and languages
- [ ] Clicking the three tabs navigates between Experience, Education, and Skills & Competences views

## Additional Notes

- **Singleton `GeneralInfo`**: the simplest approach is to use a fixed UUID primary key (e.g. `UUID.fromString("00000000-0000-0000-0000-000000000001")`) or a `findFirst()` + save pattern; document whichever you choose
- **Date formatting in frontend**: use JavaScript `Intl.DateTimeFormat` — no extra date library needed
- **`endDate: null` = "present"**: ExperienceView and EducationView should display `present` (or localised equivalent) when `endDate` is null
- **`wa-tab-group` routing integration**: the tab component uses `wa-tab-show` custom event; listen with `@wa-tab-show="onTabShow"` and call `router.push(routeForTab)`; conversely, watch `$route` to set the active tab attribute programmatically
- **MapStruct + Lombok**: add `annotationProcessor 'org.projectlombok:lombok-mapstruct-binding:0.2.0'` if MapStruct can't see Lombok-generated getters — this is already handled in portal's `build.gradle` if present; check and replicate
- **WebFlux + JPA**: the project uses `spring-boot-starter-data-jpa` alongside WebFlux — JPA is synchronous; always offload to `boundedElastic` scheduler as shown in portal's `HostConfigServiceImpl`
- **cv.json date fields**: use `"yyyy-MM-dd"` strings; configure `ObjectMapper` with `JavaTimeModule` if not already auto-configured by Spring Boot
