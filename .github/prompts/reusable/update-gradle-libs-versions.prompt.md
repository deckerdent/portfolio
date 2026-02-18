---
agent: agent
---

# Update Gradle Version Catalog (libs.versions.toml)

## Context
- Project uses Gradle version catalogs in gradle/libs.versions.toml
- Dependencies should align with existing Spring Boot, Spring Cloud, and related ecosystem versions
- The version catalog should prefer stable, compatible releases
- Versions should be inferred from existing dependencies and BOMs when possible

## Objective
Update gradle/libs.versions.toml to add or refresh dependency versions and library coordinates, selecting stable and compatible versions based on the project’s current ecosystem and BOM alignment.

## Requirements
- [ ] Detect existing versions in the repo (Spring Boot, Spring Cloud, etc.) and align new entries to them
- [ ] Add or update version keys in [versions]
- [ ] Add or update library entries in [libraries] using version.ref where applicable
- [ ] Prefer BOM-managed versions when the project already uses a BOM
- [ ] Use stable releases only (no alpha, beta, RC unless already used)
- [ ] Preserve existing entries and naming conventions
- [ ] Avoid breaking changes by matching major versions currently in use
- [ ] Update settings.gradle repositories if new libraries require additional repositories

## Technical Specifications
- File: gradle/libs.versions.toml
- File: settings.gradle (repositories configuration if needed)
- Format: Gradle version catalog TOML
- Use [versions], [libraries], and [plugins] sections appropriately
- Prefer version references (version.ref) over inline versions
- If a dependency is managed by Spring Boot BOM, set version to spring-boot (or omit version if catalog pattern supports BOM)

## Constraints
- Must remain compatible with the existing Gradle and Java versions in the repo
- Must not downgrade existing versions
- Keep naming consistent with existing catalog keys
- Avoid introducing duplicate or conflicting version keys

## Success Criteria
- [ ] All required dependencies are added with compatible versions
- [ ] Version keys are consistent and reusable
- [ ] No unstable/pre-release versions are introduced
- [ ] Catalog remains valid TOML and Gradle-compatible
- [ ] New entries align with the project’s existing dependency ecosystem
- [ ] settings.gradle includes any required repositories for newly added libraries

## Examples

### Version Key + Library Entry
```toml
[versions]
spring-boot = "3.2.1"

[libraries]
spring-webflux = { module = "org.springframework.boot:spring-boot-starter-webflux", version.ref = "spring-boot" }
```

## Additional Notes
- If Spring Cloud dependencies are present, align with the Spring Cloud BOM version that matches the Spring Boot version
- Consider OpenTelemetry, Hibernate, and Testcontainers compatibility matrices when choosing versions
- If versions cannot be inferred safely, add a TODO note or request clarification
- Only add repositories that are strictly required and document why each was added
