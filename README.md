# Enlist — Micro-Frontend Self-Contained Systems

This is a public companion to a larger private monorepo. It focuses on the `portal` service as a reference implementation of the architectural patterns used across the full project, with more detailed documentation and commentary than the private repository.

---

## Architecture

### Self-Contained Systems (SCS)

A Self-Contained System is an autonomous, independently deployable service that owns its full vertical slice — UI, business logic, and data. Systems communicate only through well-defined interfaces (APIs or events) and avoid shared databases or tight runtime dependencies. The goal is to allow teams to develop, deploy, and scale each system independently.

### Micro-Frontends

Micro-frontends extend the SCS principle to the UI layer. Rather than a single monolithic frontend consuming multiple backend APIs, each system owns and serves its own frontend. This keeps the vertical slice intact: the team responsible for a system owns everything from the database to the pixels on screen.

### Frontend Served from Within the Backend

In this project, each system's frontend is **bundled directly into the backend JAR** and served as static resources by the Spring Boot application. This eliminates the need for separate frontend hosting or a CDN, simplifies deployment to a single artifact, and ensures the frontend and backend of a system are always versioned and deployed together.

At runtime, the JAR serves the built frontend from its classpath. An optional local `static/` directory next to the JAR can override individual files without rebuilding — useful for environment-specific configuration.

### Monorepo

The monorepo structure is **not an architectural requirement** for SCS. Each app in `apps/` is a fully self-contained Spring Boot service that could live in its own repository. The monorepo is used here — and in the larger private repo — purely for developer ergonomics: shared tooling, easier cross-module navigation, and a single place to observe all modules together.

### Module Federation

Frontends use [Vite Module Federation](https://module-federation.io/) to expose micro-apps as federated modules. Each app in `src/apps/` is independently loadable at runtime, allowing the host to compose the UI from multiple autonomous units without compile-time coupling.

---

## Project Structure

```
apps/
  portal/                        # Reference SCS implementation
    build.gradle                 # Builds frontend + backend, bundles dist into JAR
    static/
    src/
      main/
        java/                    # Spring Boot application (WebFlux, Java 17)
        resources/
          application.yml        # Spring Boot configuration
          static/                # Vite + Vue 3 frontend project
            src/
              apps/
                default/         # DefaultApp — federated micro-app
                test/            # TestApp — federated micro-app
              components/
                HelloComponent.ts  # Shared web component (custom element)
              host/              # Host application — bootstraps Module Federation
            vite.config.mts      # Vite build config with Module Federation
            package.json
      test/
        java/                    # Spring Boot integration tests
```

---

## Getting Started

### Prerequisites

- Java 17
- Node.js 20+
- [pnpm](https://pnpm.io/) 9+

### Full Build

Builds the java app and the contained frontend project together and packages it into the Spring Boot JAR:

```bash
./gradlew build
```

### Skip Frontend Build

Useful when iterating on the backend only:

```bash
./gradlew build -PskipFrontend
```

### Run the Application

```bash
./gradlew :apps:portal:bootRun
```

The application is available at [http://localhost:8080](http://localhost:8080).

### Frontend Dev Server

For frontend development with hot module replacement:

```bash
# From the repo root
pnpm --filter portal dev

# Or directly
cd apps/portal/src/main/resources/static
pnpm dev
```

The Vite dev server runs on [http://localhost:4202](http://localhost:4202).

---

## Runtime File Overrides

Place files in a `static/` directory next to the running JAR to override resources served by the application without rebuilding. The application checks this directory first before falling back to the bundled classpath resources. This is intended for environment-specific configuration files, not for general development.

# Known Issues

<tbd>