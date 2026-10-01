---
agent: agent
---

# Rewrite Repository README with Requested Section Structure

## Context
- The repository is a monorepo for a portfolio platform using a Self-Contained Systems (SCS) approach with microfrontends.
- Core apps are hosted under `apps/` (notably `portal` as host and `cv` as a remote module).
- Frontend composition relies on Module Federation, with shared browser libraries in `libs/browser/core` and `libs/browser/host`.
- The existing README needs to be rewritten to match a strict section order and content expectations.

## Objective
Rewrite the root `README.md` so it follows the exact requested structure and communicates architecture and implementation clearly, concisely, and consistently with the current project state.

## Requirements
- [ ] Rewrite `README.md` at repository root.
- [ ] Use this exact high-level section order:
  1. Intro
  2. Basics
  3. Architecture
  4. Implementation
  5. Planned Features
  6. Known Issues
- [ ] In **Intro**, keep the section intentionally blank (heading present, no descriptive content).
- [ ] In **Basics**, briefly explain each of the following concepts in separate short subsections:
  - Self-Contained Systems (SCS)
  - Microfrontends
  - Module Federation
  - Web Awesome
- [ ] In **Basics**, add a separate subsection listing common technologies used: Spring, Gradle, Vue.
- [ ] Do not further describe Spring/Gradle/Vue beyond naming them as common technologies.
- [ ] In **Architecture**, explain:
  - How shared libraries (`libs/browser/core`, `libs/browser/host`) work as reusable modules
  - How shared state can be propagated across remotes using those shared modules (e.g., shared store/runtime contracts)
  - The basic project structure (apps, libs, build/runtime boundaries)
  - What is considered an **app** vs a **component** in this repository
- [ ] In **Implementation**, elaborate on frontend structure for apps and components:
  - App-level organization and lifecycle expectations (mount/unmount style modules)
  - Component-level organization and reuse boundaries
  - Brief API description (high-level summary of backend/host APIs only; no exhaustive endpoint catalog)
- [ ] In **Planned Features**, state that future work includes:
  - A scaffolder to create new projects/modules more easily
  - Dedicated services for user management
  - Dynamic registration of new services/modules
  - Router configuration management
  - Authentication/authorization enhancements
  - Internationalization
  - Other potential platform extensions
- [ ] In **Known Issues**, state that:
  - The system is unfinished
  - Security implementation is currently missing and OAuth is planned
  - Future projects may adopt advanced techniques (CQRS, caching, OpenFGA-based authorization, i18n, etc.)
  - There are currently no views/forms for creating new records
- [ ] Keep wording concise, technically accurate, and portfolio-appropriate.

## Technical Specifications
- Language/Framework: Markdown (`README.md`)
- Coding standards to follow:
  - Clear heading hierarchy (`##`/`###`)
  - Short paragraphs and scan-friendly bullets where useful
  - Terminology consistency: SCS, microfrontends, remotes, host, shared libs
- Design patterns or architecture:
  - Reflect SCS + microfrontend composition and shared runtime library model already used in this repo

## Constraints
- Must preserve the exact requested section order.
- Intro must remain blank by design.
- Do not invent implemented capabilities that do not exist.
- Keep API descriptions brief and high-level.
- Keep README professional and concise; avoid marketing language.

## Success Criteria
- [ ] `README.md` matches the exact section structure requested.
- [ ] Basics includes short explanations of SCS, Microfrontends, Module Federation, and Web Awesome.
- [ ] Basics includes a separate subsection listing Spring, Gradle, and Vue without further explanation.
- [ ] Architecture clearly explains shared libs, shared state across remotes, and app/component definitions.
- [ ] Implementation clearly describes frontend app/component structure and briefly describes APIs.
- [ ] Planned Features and Known Issues accurately reflect requested future direction and current gaps.

## Examples (Optional)
```markdown
## Intro

## Basics
### Self-Contained Systems (SCS)
...
### Microfrontends
...
### Module Federation
...
### Web Awesome
...
### Common Technologies
- Spring
- Gradle
- Vue
```

## Additional Notes
- If any requested concept is ambiguous, prefer concise wording and avoid speculative implementation details.
- Keep future-oriented statements clearly marked as planned, not current behavior.
