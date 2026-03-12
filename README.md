# Portfolio — Micro-Frontend Self-Contained Systems

## Intro

This repo is a sample repo for a micro-frontend-based, extensible portal. It uses Self-Contained-Systems approach to slice functionality into "apps" and "components" organized in a mono-repo. It has: 

- shared functionality in libraries
- a global router to control module loading 
- local routers per app to control app routing
- web-component based shared component library and theme to keep the UI consistent
- dockerized services serving both front- and backend
- a slot based layout to mount apps and components easily

It does not yet have: 

- authentication/authorization
- i18n 
- advanced shared features like notifications, dynamic registring of applications or forms to administrate everything

## Basics

### Self-Contained Systems (SCS)

SCS means each system owns its full vertical slice, including UI, backend logic, and data. Each system is independently deployable and should communicate with other systems only through explicit interfaces.

### Microfrontends

Microfrontends apply SCS principles to the browser layer. Instead of one large frontend, UI features are delivered as separate modules owned by separate systems and composed at runtime.

### Module Federation

Module Federation is used to load frontend modules from independent builds at runtime. In this project, the `portal` host registers and mounts remote modules without compile-time coupling to their internal implementation.

### Web Awesome

Web Awesome is used as a web-component-based UI building approach that aligns with framework-agnostic frontend composition. It supports reusable, standards-based UI elements that can be consumed across modules.

### Common Technologies

- Spring
- Gradle
- Vue

## Architecture

Shared browser libraries in `libs/browser/core` and `libs/browser/host` provide runtime contracts and reusable host logic so each app does not reimplement cross-cutting behavior. This includes common model types, host orchestration utilities, and shared integration patterns for remotes.

State can be shared across remotes through these shared runtime modules (for example, a shared store abstraction exposed by host/core libraries). This allows multiple remotes to react to common navigation or host state without direct remote-to-remote coupling.

At the repository level, `apps/` contains self-contained deployable systems (for example `portal` and `cv`), while `libs/` contains reusable shared modules. Each app contains backend and frontend code, with frontend assets bundled into the backend artifact at build time.

In this repository, an **app** is a federated feature module with lifecycle entry points (typically `mount(container, basename)` and optional `unmount()`) that can be loaded by a host. A **component** is a reusable UI building block (for example in `src/components/`) used by apps, but not independently routed or deployed.

## Implementation

Frontend app modules are organized under each service frontend source tree (for example under `src/apps/`), where each module encapsulates its own rendering and lifecycle integration for host-driven mounting. The host resolves route/slot activation and mounts the selected app into a target container.

Frontend components are organized separately (for example under `src/components/`) and are intended for reuse by multiple app modules. Components focus on presentation and interaction primitives, while app modules own feature composition and lifecycle behavior.

Backend and host APIs are intentionally small and composition-focused. In `portal`, host endpoints provide initialization/configuration data and source/module metadata used to register remotes, while each app service exposes its own domain APIs behind `/api`.

## Testing

### Backend E2E (Testcontainers)

- Run portal backend tests:
	- `./gradlew :apps:portal:test`
- Run CV backend tests:
	- `./gradlew :apps:cv:test`

Both backend suites include containerized PostgreSQL integration tests.

### Frontend tests (Vitest)

- Portal frontend tests:
	- `pnpm --filter portal vitest run`
- CV frontend tests:
	- `pnpm --filter cv vitest run`

## Dockerized local stack

The repository includes:

- [docker-compose.yml](docker-compose.yml)
- [apps/portal/Dockerfile](apps/portal/Dockerfile)
- [apps/cv/Dockerfile](apps/cv/Dockerfile)

Start the full local stack (PostgreSQL + CV + Portal):

- `docker compose up --build`

Default URLs:

- Portal: `http://localhost:8080`
- CV: `http://localhost:8081`

PostgreSQL is initialized with `cv` and `portal` databases via [docker/postgres/init-multiple-dbs.sql](docker/postgres/init-multiple-dbs.sql).

## Planned Features

The platform is planned to be extended with a scaffolder to create new projects and modules with less manual setup. It is also planned to introduce dedicated services and platform capabilities for:

- User management
- Dynamic registration of new services/modules
- Router configuration management
- Authentication and authorization
- Internationalization
- Additional platform-oriented extensions as requirements evolve

## Known Issues

- There are currently no forms that allow creating new records through forms. Data only comes from jsons loaded into the database on startup.

- Much of the functionality needs to be relocated to libs. Components are not as re-usable and configurable as they could be. Modules need a way to get their own Module object to us their own configs in their own code. 

- The way we create axios instances in the modules will break as soon as we want to serve frontends from a cdn as import.meta.url will then not point to the backend domain. Reverse-Proxying frontends should work though to have at least this option for scaling. However, we'll likely implement configs in one or the other way and modules could replace the import.meta with a value from these configs.  

- Also standalone execution for local testing is not available for modules. A mock host may be beneficial.

- We need a good, dynamic approach for CORS so whenever a service gets registered to the host they can communicate and the host can actuall load the frontends. 