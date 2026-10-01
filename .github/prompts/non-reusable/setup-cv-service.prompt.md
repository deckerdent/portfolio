---
agent: agent
---

# Set Up CV Service — Backend + Frontend Scaffold

## Context

- **Monorepo root**: `portfolio/` — Gradle multi-project build managed via `settings.gradle`, pnpm workspaces via `pnpm-workspace.yaml`
- **Reference implementation**: `apps/portal/` — follow its structure exactly unless a requirement below says otherwise
- **Portal ports**: backend `8080`, Vite dev server `4202` — CV must use **different ports** so both run simultaneously
- **Shared libs** (resolved via Vite aliases in `vite.config.mts`):
  - `@portfolio/core` → `libs/browser/core/src/index.ts`
  - `@portfolio/host` → `libs/browser/host/src/index.ts`
  - `@portfolio/app` → `libs/browser/app/src/index.ts`
  - `@portfolio/core/writer` → `libs/browser/core/src/store/PortalStore.ts`
- **Module Federation**: portal is the `host`; CV is a **remote only** — it exposes modules but has no `main.ts` host bootstrap of its own
- **Frontend toolchain**: Vite + `@module-federation/vite`, Vue 3, TypeScript, WebAwesome (`@awesome.me/webawesome`), pnpm
- **Backend toolchain**: Java 17, Spring Boot 3.3.6, Spring WebFlux (Netty, no Tomcat), Spring Data JPA, Flyway, PostgreSQL, OpenAPI Generator, MapStruct, Lombok
- **Gradle version**: 9.3 with configuration cache enabled (`org.gradle.configuration-cache=true`)
- **`settings.gradle`**: currently includes only `apps:portal` — CV must be added

## Objective

Scaffold the `apps/cv` service from scratch: a Gradle subproject with Spring WebFlux backend serving a static folder that contains a Vite + Module Federation frontend. The frontend exposes Vue micro-frontend modules (no host bootstrap). Both backend and frontend must be able to run simultaneously alongside the portal service on separate ports.

## Requirements

### 1 — Gradle subproject (`apps/cv/`)

- [ ] Create `apps/cv/build.gradle` modelled on `apps/portal/build.gradle`:
  - Same plugins: `java`, `org.springframework.boot`, `io.spring.dependency-management`, `org.openapi.generator`
  - Same dependency set (WebFlux, JPA, Flyway, PostgreSQL, OpenAPI, MapStruct, Lombok, testing)
  - `group = 'com.portfolio'`, `version = '1.0.0'`
  - Java toolchain: `languageVersion = JavaLanguageVersion.of(17)`
  - OpenAPI generator config pointing to `src/main/resources/openapi/api.yml` with packages `com.portfolio.cv.api` and `com.portfolio.cv.model`
  - `buildFrontend` Exec task and `cleanFrontend` Delete task wired the same way as portal (Windows/Unix pnpm detection)
  - `processResources` copies `dist/` into `static/` and excludes source/node_modules from the JAR
- [ ] Create `apps/cv/gradle.properties` with `org.gradle.configuration-cache=true`
- [ ] Add `include('apps:cv')` to root `settings.gradle`

### 2 — Spring Boot application (`apps/cv/src/main/`)

- [ ] Create package structure `com.portfolio.cv`
- [ ] Create `CvApplication.java` — standard `@SpringBootApplication` main class
- [ ] Create `src/main/resources/application.yml`:
  - `server.port: 8081` (portal uses 8080)
  - `spring.application.name: cv`
  - Same datasource placeholders: `${DATABASE_URL:jdbc:postgresql://localhost:5432/cv}`, `${DATABASE_USER:postgres}`, `${DATABASE_PASSWORD:}`
  - Same JPA, Flyway, autoconfigure excludes (security, redis autoconfiguration), cache, actuator, and static resource path setup as portal
  - `spring.webflux.base-path: /`
  - Static locations: `file:./static/` and `classpath:/static/`
- [ ] Create a minimal `src/main/resources/openapi/api.yml` with at least one placeholder endpoint (e.g. `GET /api/cv/health` returning `{ status: string }`) so the OpenAPI generator task does not fail
- [ ] Create Flyway baseline migration `src/main/resources/db/migration/V1__init.sql` — can be an empty or minimal schema

### 3 — Frontend scaffold (`apps/cv/src/main/resources/static/`)

- [ ] Create `package.json`:
  - `"name": "cv"`, `"version": "0.0.1"`, `"type": "module"`
  - Scripts: `dev`, `build`, `preview` (same as portal)
  - Same `dependencies` and `devDependencies` catalog references as portal's `package.json`
- [ ] Create `tsconfig.json` and `tsconfig.app.json` modelled on portal's equivalents, adjusting `paths` to point up to the monorepo root libs
- [ ] Create `index.html` — minimal HTML shell (no host bootstrap `<script>` tag needed, but must include a `<div id="layout">` with slot structure for dev/preview purposes)
- [ ] Create `vite.config.mts`:
  - Dev server: **port `4203`**, `host: 'localhost'`, CORS headers, `origin: 'http://localhost:4203'`
  - Preview port: `4301`
  - Proxy `/api` → `http://localhost:8081`
  - `cacheDir` pointing to monorepo `node_modules/.vite/apps/cv/...`
  - `resolve.alias` for all four `@portfolio/*` libs (same as portal)
  - `vue()` plugin with `isCustomElement` for `wa-` and `portal-` prefixes
  - `federation()` config:
    - `name: 'cv'`
    - `filename: 'remoteEntry.js'`
    - `exposes`: at minimum `'./CvApp': './src/apps/cv/Module.ts'` as a placeholder
    - `shared`: `vue` and `vue-router` as singletons (same versions as portal)
    - `dts: false`, `manifest: true`
  - Build config: `outDir: './dist'`, `cssCodeSplit: false`, `minify: false`, `target: 'esnext'`

### 4 — Placeholder frontend module (`src/apps/cv/`)

- [ ] Create `Module.ts` following the `ModuleLifecycle` pattern:
  - `mount` and `unmount` exports
  - Creates a Vue app with `createWebHistory(basename)` router
  - Single route `{ path: '/', name: 'cv', component: CvView }`
- [ ] Create `app/App.vue` — `<RouterView />` wrapper
- [ ] Create `views/CvView.vue` — minimal placeholder, e.g. `<h1>CV</h1>`
- [ ] Create `styles.css` — empty or minimal

### 5 — Monorepo wiring

- [ ] Add `apps/cv/src/main/resources/static` to `pnpm-workspace.yaml` packages list (same pattern as portal's static folder)
- [ ] Add the CV static workspace to `tsconfig.base.json` path references if applicable

## Technical Specifications

- **Language**: Java 17 (backend), TypeScript + Vue 3 (frontend)
- **Backend framework**: Spring Boot 3.3.6, Spring WebFlux (reactive, Netty)
- **Build tool**: Gradle 9.3 (Groovy DSL), pnpm workspaces
- **Module Federation**: `@module-federation/vite` — CV is a **remote only**, no `init()` call, no host bootstrap
- **Ports**: backend `8081`, Vite dev `4203`, Vite preview `4301` (portal uses `8080` / `4202` / `4300`)
- **Package naming**: `com.portfolio.cv.*`
- **DB name**: `cv` (portal uses `portal`)
- **Coding standards**: match portal conventions exactly — same Gradle task names, same file structure, same import aliases

## Constraints

- Must not conflict with any portal ports (`8080`, `4202`, `4300`)
- Must not modify any existing portal source files
- The frontend must have **no host entry point** (`main.ts` with `HostService.bootstrap` must NOT be created) — only module exposes
- Gradle configuration cache must remain enabled — avoid non-cacheable task configurations
- `settings.gradle` change is required for the CV subproject to be recognised by Gradle

## Success Criteria

- [ ] `.\gradlew :apps:cv:build -x test` completes successfully (frontend built, Java compiled, OpenAPI generated)
- [ ] `$env:DATABASE_PASSWORD="postgres"; .\gradlew :apps:cv:bootRun -x buildFrontend` starts Spring Boot on port `8081` without errors
- [ ] `http://localhost:8081/actuator/health` returns `{"status":"UP"}`
- [ ] Portal and CV backends can run simultaneously without port conflicts
- [ ] `pnpm run dev` inside `apps/cv/src/main/resources/static` starts Vite on port `4203`
- [ ] `http://localhost:4203/remoteEntry.js` is served (Module Federation remote entry)
- [ ] `include('apps:cv')` is present in root `settings.gradle`
- [ ] CV package appears in `pnpm-workspace.yaml`

## Examples

### Portal directory structure to mirror
```
apps/portal/
├── build.gradle
├── gradle.properties
└── src/
    ├── main/
    │   ├── java/com/portfolio/portal/PortalApplication.java
    │   └── resources/
    │       ├── application.yml
    │       ├── openapi/api.yml
    │       ├── db/migration/V1__init.sql
    │       └── static/
    │           ├── package.json
    │           ├── vite.config.mts
    │           ├── index.html
    │           └── src/
    │               ├── apps/
    │               │   └── setup/
    │               │       ├── Module.ts
    │               │       ├── app/App.vue
    │               │       ├── views/SetupView.vue
    │               │       └── styles.css
    │               └── host/
    │                   └── main.ts   ← CV does NOT have this
    └── test/
```

### Expected CV vite.config.mts federation block
```typescript
federation({
  name: 'cv',
  filename: 'remoteEntry.js',
  exposes: {
    './CvApp': './src/apps/cv/Module.ts',
  },
  shared: {
    vue: { singleton: true, requiredVersion: '^3.5.0' },
    'vue-router': { singleton: true, requiredVersion: '^4.5.0' },
  },
  dts: false,
  manifest: true,
})
```

## Additional Notes

- The CV service has **no `devMockApiPlugin`** — the portal's mock is specific to the portal's `/api/host/*` endpoints
- Flyway migration `V1__init.sql` can start empty (`-- placeholder`) — the important thing is Flyway finds it and sets `schema_version` to 1
- The `index.html` in the CV static folder is only needed for Vite dev mode; in production the Spring Boot server serves its own views
- If the OpenAPI `api.yml` placeholder causes generator issues, ensure at minimum a valid OpenAPI 3.0 document with `info`, `paths`, and at least one response schema
- Remember to run `pnpm install` from the monorepo root after adding the new workspace package so the symlink is created
