---
agent: agent
---

# Update Project README

## Context
- Project type: Java Spring and microfrontend-based portal solution with pluggable extensions
- Architecture: Self-contained systems with distributed microfrontends
- Build system: Gradle-based monorepo structure
- Frontend approach: Module federation / microfrontend architecture
- Each app is an independent Spring service that hosts its own frontend modules

## Objective
Create a comprehensive README.md file that clearly documents the project's architecture, setup instructions, development workflow, and module structure for developers joining or contributing to the portal solution.

## Requirements
- [ ] Document the overall architecture (portal + self-contained apps)
- [ ] Explain the apps folder structure and self-contained system concept
- [ ] Describe how each app contains its own Spring service and microfrontends
- [ ] Document the manifest file (YML) structure and purpose
- [ ] Explain the libs folder organization (Java and JavaScript utilities)
- [ ] Document the scaffolders and how to use them
- [ ] Include setup and installation instructions
- [ ] Provide development workflow guidelines
- [ ] Add build and run instructions
- [ ] Document the microfrontend integration approach
- [ ] Include contribution guidelines

## Technical Specifications
- Language/Framework: Java Spring, JavaScript/TypeScript microfrontends
- Build tool: Gradle
- Package manager: pnpm (based on workspace structure)
- Scaffolding: Yeoman-based generators
- Project structure: Monorepo with Nx integration (nx.json present)

## Constraints
- Must be beginner-friendly for new developers
- Should explain the self-contained system architecture clearly
- Must document the relationship between Java services and microfrontends
- Should provide clear examples of the manifest file structure

## Success Criteria
- [ ] README clearly explains the portal architecture
- [ ] New developers can understand the self-contained system concept
- [ ] Setup instructions are complete and executable
- [ ] Module structure is well-documented with examples
- [ ] Scaffolder usage is documented with examples
- [ ] Development workflow is clear and actionable

## Examples

### App Structure Example
```
apps/
  portal/              # Central portal host application
    src/main/
      resources/static/
        portal-shell/  # Host microfrontend
  my-app/              # Example self-contained app
    src/main/
      java/            # Spring service
      resources/
        static/
          feature-a/   # Microfrontend module A
          feature-b/   # Microfrontend module B
        manifest.yml   # Registration config
```

### Manifest File Example
```yaml
modules:
  - name: feature-a
    path: /static/feature-a
    exposed: ./Module
  - name: feature-b
    path: /static/feature-b
    exposed: ./Module
```

## Additional Notes
- The portal app is special as it hosts the main application shell
- Each service can expose multiple frontend modules through HTTP
- The static folder structure allows multiple microfrontend projects per service
- Scaffolders should help maintain consistency across new self-contained systems
- Consider documenting the registration process for new modules to the host
- May need to explain the communication pattern between microfrontends
- Consider adding a architecture diagram in the future
