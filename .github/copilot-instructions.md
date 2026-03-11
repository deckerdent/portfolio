# Copilot instructions for this repository

## Architecture snapshot
- This repo is a monorepo of **self-contained systems (SCS)**; each app owns backend + frontend and is deployable on its own.
- Primary services: `apps/portal` (host shell, config + composition) and `apps/cv` (remote CV module).
- Each app is Spring Boot WebFlux (`spring-boot-starter-webflux`) with frontend assets bundled into the JAR during Gradle `processResources`.
- Frontend micro-app composition uses Vite Module Federation (`@module-federation/vite`) and runtime APIs (`@module-federation/enhanced/runtime`).
- Shared browser runtime logic lives in `libs/browser/core` and `libs/browser/host` and is consumed through aliases in each app's `vite.config.mts`.

## Request/data flow to keep in mind
- Host bootstrap entry is `apps/portal/src/main/resources/static/src/host/main.ts`.
- Startup sequence: `GET /api/host/initialized` -> load host config (`/api/host` or `/host.json`) -> load apps (`appsUrl`) -> load sources (`sourceUrls`) -> `registerRemotes(...)` -> route/mount apps.
- Host config comes from DB-backed props (`ConfigProp`) and is materialized into singleton `PortalHost` (`apps/portal/.../host/model/PortalHost.java`).
- `sourceUrls` endpoints must return arrays with `{ name, entry }`; entries are normalized to `.../mf-manifest.json` in `SourcesService`.
- Frontend state shared across remotes is `PortalStore` (RxJS BehaviorSubjects). Write access is intentionally restricted to `@portfolio/core/writer`.

## Backend conventions
- REST controllers are auto-prefixed with `/api` via `WebConfig.configurePathMatching(...)` (both portal and cv).
- Portal deep-link SPA fallback is implemented by `SpaWebFilter`; do not break `spa.passthrough-pattern` behavior.
- OpenAPI-first: contracts in `apps/*/src/main/resources/openapi/api.yml`; Java interfaces/models are generated into `build/generated/openapi` at compile time.
- Keep JPA calls off Netty event-loop threads: existing pattern uses `Mono.fromCallable(...).subscribeOn(Schedulers.boundedElastic())`.

## Frontend conventions
- Each federated module exposes lifecycle functions `mount(container, basename)` and optional `unmount()` (see `src/apps/*/Module.ts`).
- Slots are DOM ids prefixed with `slot:` and are validated before mount in `AppService`.
- Routing in host uses Navigo (`RouterService`), with activation by longest-prefix match on `activationUrl`.
- In CV dev mode, `vite.config.mts` intentionally forces Vue compiler `isProduction: true` and stubs `__VUE_HMR_RUNTIME__` for host compatibility.

## AI implementation rules (required)
- For every new feature, create tests immediately for both layers impacted:
	- backend: JUnit/WebFlux tests in `apps/*/src/test/java`
	- frontend: Vitest tests near feature code (e.g. `src/**/__tests__` or `*.spec.ts`)
- Use **package-by-feature first**, then **package-by-layer inside each feature**.
	- Example shape: `.../<feature>/<Service>.java`, `.../<feature>/model/...`, `.../<feature>/dto/...`, `.../<feature>/repository/...`
	- Keep service classes directly in the feature root package.
- Code against interfaces:
	- define service/repository ports as interfaces in the feature package
	- inject interfaces, not concrete implementations
	- keep implementation classes explicit (e.g. `...Impl`) behind those interfaces
- When a request is ambiguous, ask targeted backquestions before implementing irreversible behavior.
- Proactively recommend extraction to `libs/browser/*` (or shared backend modules when added) when logic is reused across apps, generic, framework-agnostic, or cross-cutting.

## Build/test/dev workflows
- Full repo build (backend + bundled frontends): `./gradlew build`
- Backend-only iteration (skip frontend bundling): `./gradlew build -PskipFrontend`
- Run services: `./gradlew :apps:portal:bootRun` and `./gradlew :apps:cv:bootRun`
- Rebuild just one frontend for jar packaging: `./gradlew :apps:portal:buildFrontend --rerun-tasks` (or `:apps:cv:buildFrontend`)
- Frontend HMR dev servers: `pnpm --filter portal dev` (4202), `pnpm --filter cv dev` (4203)
- Frontend tests are Vitest-based per app (`vite.config.mts` test block), Java tests are JUnit/Testcontainers via Gradle `test`.

## Integration expectations
- Default local ports: portal `8080`, cv `8081`; portal frontend proxies `/api` to 8080, cv frontend proxies `/api` to 8081.
- `apps/portal/src/main/resources/static/public/apps.json` controls module placement (`slot`) and route activation (`activationUrl`).
- `apps/portal/src/main/resources/static/public/sources.json` declares remote source origins for module federation.
- Static override mechanism is intentional: Spring serves `file:./static/` before `classpath:/static/` for runtime file replacement.
