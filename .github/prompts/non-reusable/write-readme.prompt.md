---
agent: agent
---

# Write Project README

## Context
- This is a public portfolio project that is a fork/companion to a larger private monorepo
- The project is built around **Self-Contained Systems (SCS)** architecture using **Micro-Frontends**
- Each system (app) in `apps/` is a standalone Spring Boot service that hosts its own frontend
- Frontend code lives inside the backend service under `src/main/resources/static/` and is bundled into the JAR at build time
- The monorepo is used here for convenience and overview, not as an architectural necessity
- Tech stack: Spring Boot 3.3.6 (WebFlux, Java 17), Vue 3, Vite 7, Module Federation, pnpm workspaces, Gradle 9

## Objective
Write a comprehensive, well-structured `README.md` for the repository root that explains the architectural concepts, project structure, and how to build and run the project.

## Requirements
- [ ] Explain this is a public companion to a larger private repo, providing more insight into the architecture
- [ ] Define **Self-Contained Systems (SCS)**: autonomous, independently deployable systems that each own their full vertical slice (UI, logic, data)
- [ ] Define **Micro-Frontends**: extending SCS to the UI layer, each system owns and serves its own frontend
- [ ] Explain that frontends are **served from within the backend service** (bundled into the JAR) — this simplifies deployment, eliminates separate frontend hosting, and keeps vertical slices intact
- [ ] Explain the **monorepo** is not an SCS requirement — it's used here (and in the private repo) purely for oversight and developer ergonomics, not for tight coupling
- [ ] Document the structure of `apps/portal` in detail:
  - `src/main/java/` — Spring Boot application
  - `src/main/resources/static/` — Vite + Vue 3 frontend project (the microfrontend host + apps)
  - `src/main/resources/static/src/apps/` — individual micro apps (DefaultApp, TestApp)
  - `src/main/resources/static/src/components/` — shared web components (HelloComponent)
  - `src/main/resources/static/src/host/` — the host application bootstrapping Module Federation
  - `build.gradle` — builds both frontend (via pnpm) and backend, bundles dist into JAR
- [ ] Document build instructions:
  - Prerequisites: Java 17, Node.js, pnpm
  - Full build: `./gradlew build`
  - Skip frontend: `./gradlew build -PskipFrontend`
  - Frontend dev server only: `pnpm --filter portal dev` or `cd apps/portal/src/main/resources/static && pnpm dev`
  - Run the application: `./gradlew :apps:portal:bootRun`
- [ ] Keep the tone concise and professional — this is a portfolio project

## Technical Specifications
- Language: Markdown
- File: `README.md` at repository root (replace the existing `tbd` content)
- Use headings, code blocks, and a directory tree where appropriate
- No excessive length — be informative but scannable

## Constraints
- Do not document unimplemented features
- Do not include personal/private repo URLs
- Keep architectural explanations brief — 2-4 sentences per concept

## Success Criteria
- [ ] Reader understands the SCS + micro-frontend architecture after reading
- [ ] Reader understands why frontends live inside the backend service
- [ ] Reader understands why a monorepo is used without thinking it's architecturally required
- [ ] Reader can build and run the project from the README alone
- [ ] `apps/portal` structure is clearly documented

## Additional Notes
- The `static/` folder at the repo root is for external file overrides at runtime (not source code)
- Module Federation is used to expose micro-apps — each `src/apps/*/Module.ts` is an exposed federated module
- The private repo contains additional apps beyond `portal` — this public repo focuses on `portal` as the reference implementation
