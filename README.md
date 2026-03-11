# Portfolio — Micro-Frontend Self-Contained Systems

## Intro

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

## Planned Features

The platform is planned to be extended with a scaffolder to create new projects and modules with less manual setup. It is also planned to introduce dedicated services and platform capabilities for:

- User management
- Dynamic registration of new services/modules
- Router configuration management
- Authentication and authorization enhancements
- Internationalization
- Additional platform-oriented extensions as requirements evolve

## Known Issues

This project is unfinished and currently does not include complete production-grade security implementation. OAuth-based security is planned for future iterations.

Future projects in this ecosystem may also adopt more advanced patterns and capabilities such as CQRS, caching, OpenFGA-based authorization, i18n, and similar improvements.

There are currently no views that allow creating new records through forms.