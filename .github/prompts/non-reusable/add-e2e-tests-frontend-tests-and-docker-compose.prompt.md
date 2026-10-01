---
agent: agent
---

# Add Backend E2E Tests, Frontend Basic Tests, and Dockerized Local Stack

## Context
- Current repository is an Nx + Gradle monorepo with two Spring Boot apps:
  - `apps/cv` (Java 17, Spring Boot 3.3.6, WebFlux + JPA + Flyway + PostgreSQL)
  - `apps/portal` (same stack)
- Both backend apps already include Testcontainers dependencies in Gradle:
  - `org.testcontainers:testcontainers`
  - `org.testcontainers:junit-jupiter`
  - `org.testcontainers:postgresql`
- Both apps include frontend code under `src/main/resources/static` built with Vite/Vue and pnpm during Gradle build.
- Root workspace has Vitest-related dependencies and Nx tooling.

## Objective
Implement end-to-end backend tests using Testcontainers for both Spring Boot applications, add minimal but meaningful frontend tests for both frontend apps, and provide containerization artifacts (Dockerfiles + root Docker Compose) to run PostgreSQL and both applications together.

## Requirements
- [ ] Add backend E2E test setup for `apps/cv` using PostgreSQL Testcontainers and Spring Boot test context.
- [ ] Add backend E2E test setup for `apps/portal` using PostgreSQL Testcontainers and Spring Boot test context.
- [ ] Ensure E2E tests validate at least one real API behavior per app (HTTP-level assertion, not only unit/service tests).
- [ ] Add basic frontend tests for CV frontend (`apps/cv/src/main/resources/static`) using existing Vitest setup.
- [ ] Add basic frontend tests for Portal frontend (`apps/portal/src/main/resources/static`) using existing Vitest setup.
- [ ] Create Dockerfile for CV app image build/run.
- [ ] Create Dockerfile for Portal app image build/run.
- [ ] Create root `docker-compose.yml` to start PostgreSQL + CV + Portal containers together.
- [ ] Configure Compose networking and environment variables so both apps connect to PostgreSQL container.
- [ ] Provide sensible default ports and health/dependency startup order in Compose.

## Technical Specifications
- Language/Framework:
  - Backend: Java 17, Spring Boot 3.3.x, JUnit 5, Testcontainers, Flyway
  - Frontend: Vue 3 + Vite + Vitest
  - Containerization: Docker + Docker Compose
- Testing approach:
  - Use `@SpringBootTest(webEnvironment = RANDOM_PORT)` (or equivalent reactive-friendly integration style).
  - Use Testcontainers PostgreSQL container for test DB; avoid H2 for these E2E tests.
  - Inject dynamic DB properties via `@DynamicPropertySource` (or equivalent).
  - Execute Flyway migrations against containerized DB during tests.
  - Use `WebTestClient` or `TestRestTemplate` to verify endpoint responses.
- Frontend test baseline:
  - At minimum, include one rendering/smoke test and one behavior/assertion test per frontend app.
  - Keep tests deterministic and CI-friendly (no external network calls).
- Dockerfiles:
  - Multi-stage builds preferred (build JAR, then slim runtime image).
  - Use a stable Java 17 runtime base image for final stage.
  - Expose app ports explicitly.
- Compose:
  - Services: `postgres`, `cv`, `portal`.
  - Use service-name DNS (e.g., `postgres`) in JDBC URLs.
  - Provide persistent volume for PostgreSQL data.

## Constraints
- Do not break existing Gradle build lifecycle and existing frontend build integration in each app.
- Keep changes aligned with existing project conventions and folder structure.
- Avoid introducing unnecessary new frameworks for testing.
- Ensure configuration works on local development environments without manual DB setup.
- Keep secrets out of source code; use Compose environment variables with safe local defaults.

## Success Criteria
- [ ] Running backend tests for both apps spins up PostgreSQL Testcontainers and passes consistently.
- [ ] Each app has at least one backend E2E test asserting real HTTP/API behavior with containerized DB.
- [ ] Both frontend projects have passing basic tests via Vitest.
- [ ] Building Docker images for CV and Portal succeeds from repository root instructions.
- [ ] `docker compose up` starts PostgreSQL, CV, and Portal successfully.
- [ ] CV and Portal containers can both connect to PostgreSQL using Compose network configuration.
- [ ] Root documentation/comments include minimal run/test instructions for local developers.

## Examples (Optional)
```yaml
# Example compose intent (illustrative only)
services:
  postgres:
    image: postgres:16
  cv:
    build:
      context: .
      dockerfile: apps/cv/Dockerfile
  portal:
    build:
      context: .
      dockerfile: apps/portal/Dockerfile
```

```java
// Example E2E pattern (illustrative only)
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HealthE2ETest {
  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");
}
```

## Additional Notes
- Prefer creating shared test utility patterns if both backend apps need identical Testcontainers wiring.
- If endpoint selection is unclear, choose stable low-coupling endpoints (e.g., health, config, or simple read endpoint) and document rationale.
- If critical details are missing (exact ports, specific endpoints, or desired image names/tags), proceed with sensible defaults and clearly document chosen assumptions.
- Validate that Flyway migration order and schema availability are stable in containerized test execution.
