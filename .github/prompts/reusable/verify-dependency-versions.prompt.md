---
agent: agent
---

# Verify Dependency Versions (Gradle + pnpm)

## Context
- Project uses Gradle version catalog in gradle/libs.versions.toml
- Project uses pnpm workspace catalogs in pnpm-workspace.yaml
- Dependencies span Spring Boot/Spring Cloud ecosystem and Vue/Vite frontend stack
- Version alignment and stability are critical across the monorepo

## Objective
Validate and update dependency versions in gradle/libs.versions.toml and pnpm-workspace.yaml to ensure compatibility across related ecosystems and alignment with the latest stable releases.

## Requirements
- [ ] Review all versions in gradle/libs.versions.toml for compatibility (Spring Boot, Spring Cloud, Hibernate, OpenTelemetry, Testcontainers, etc.)
- [ ] Review all versions in pnpm-workspace.yaml catalogs for compatibility (Vue, Vite, module federation, router, tooling)
- [ ] Ensure versions are the latest stable releases for each ecosystem
- [ ] Ensure related libraries use compatible version ranges (e.g., Spring Boot ↔ Spring Cloud, Hibernate ↔ Hibernate Search)
- [ ] Avoid pre-release versions unless explicitly required
- [ ] Update version keys and catalog entries as needed
- [ ] Preserve existing naming conventions and catalog structure
- [ ] Document any version changes with a short rationale in comments if applicable

## Technical Specifications
- Files:
  - gradle/libs.versions.toml
  - pnpm-workspace.yaml
- Gradle: use [versions] + [libraries] with version.ref
- pnpm: use catalogs (catalog: or catalog:default)
- Prefer BOM-managed versions where applicable

## Constraints
- Must not introduce breaking changes without explicit justification
- Must remain compatible with current Java and Node.js baselines
- Avoid duplicate or conflicting version keys
- Keep catalogs valid for Gradle and pnpm parsing

## Success Criteria
- [ ] All versions are verified against latest stable releases
- [ ] Cross-ecosystem compatibility is maintained
- [ ] Updated files are valid and consistent
- [ ] No dependency conflicts introduced
- [ ] Changes are minimal and justified

## Examples

### Gradle version alignment
```toml
[versions]
spring-boot = "3.3.x"
spring-cloud = "2023.0.x"
```

### pnpm catalog alignment
```yaml
catalogs:
  default:
    vue: ^3.x
    vite: ^5.x
```

## Additional Notes
- If exact versions cannot be verified confidently, add a TODO note instead of guessing
- Prefer official BOMs or compatibility matrices for Spring ecosystem alignment
- Ensure module federation plugin version supports the selected Vite version
