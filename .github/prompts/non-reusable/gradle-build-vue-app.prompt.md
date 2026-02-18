---
agent: agent
---

# Integrate Vue Frontend Build into Gradle JAR Packaging

## Context
- **Current situation**: Vue/Vite frontend project exists at `apps/portal/src/main/resources/static/` with separate build process
- **Project structure**: Spring Boot application with Gradle build system, Vue 3 frontend with Vite
- **Package manager**: pnpm workspace (not npm)
- **Build output**: Vite produces `dist/` folder with static assets
- **Serving requirement**: Static content must be served from root path `/`, not `/static`
- **Gradle version**: Using Spring Boot Gradle Plugin 3.3.6
- **Java version**: 17

## Objective
Create a Gradle build task that automatically builds the Vue frontend using pnpm, includes the dist output in the Spring Boot JAR, and configures the application to serve static content from the root path `/`.

## Requirements
- [ ] Create a Gradle task to execute `pnpm build` in the frontend directory
- [ ] Ensure frontend build task runs before `processResources` or JAR packaging
- [ ] Copy `dist/` contents to the appropriate location in the JAR's static resources
- [ ] Configure resource location so files are served from `/` instead of `/static`
- [ ] Handle both development and production build scenarios
- [ ] Ensure build fails if frontend build fails
- [ ] Support clean builds (remove old dist files)
- [ ] Work with existing pnpm workspace configuration

## Technical Specifications
- **Build tool**: Gradle (Groovy DSL preferred, or Kotlin DSL)
- **Frontend directory**: `src/main/resources/static/`
- **Build command**: `pnpm build` (must run in the frontend directory)
- **Build output**: `src/main/resources/static/dist/`
- **Target location in JAR**: `static/` directory at JAR root (so files are served from `/`)
- **Spring Boot configuration**: May need to configure `spring.web.resources.static-locations` or similar
- **Resource processing**: Use Gradle's `processResources` task or custom copy tasks
- **Task dependencies**: Frontend build must complete before resource processing

## Constraints
- Must use `pnpm`, not `npm` or `yarn`
- Must not break existing Gradle build tasks
- Must work in CI/CD environments (pnpm must be available)
- Frontend code should not be modified, only Gradle configuration
- Must maintain development workflow (developers can still run `pnpm dev` separately)
- Build should be idempotent and reproducible

## Success Criteria
- [ ] Running `./gradlew build` automatically builds the Vue frontend
- [ ] The generated JAR contains all files from `dist/` in the correct location
- [ ] Running the JAR serves static files from `/` (e.g., `/index.html`, `/assets/...`)
- [ ] Build fails gracefully with clear error if `pnpm build` fails
- [ ] `./gradlew clean` removes frontend build artifacts
- [ ] Build time is reasonable (frontend only builds once per gradle build)
- [ ] No manual steps required to prepare frontend for production JAR

## Examples
### Expected Gradle Task Structure
```gradle
task buildFrontend(type: Exec) {
    workingDir 'src/main/resources/static'
    commandLine 'pnpm', 'build'
    // Additional configuration...
}

// Resource handling
processResources {
    dependsOn buildFrontend
    // Copy dist to appropriate location...
}
```

### Expected JAR Structure
```
app.jar
├── BOOT-INF/
│   ├── classes/
│   │   └── static/          ← Frontend assets here (served from /)
│   │       ├── index.html
│   │       ├── assets/
│   │       │   ├── *.js
│   │       │   ├── *.css
│   │       └── mf-manifest.json
```

### Expected URL Mapping
- `http://localhost:8080/` → serves `index.html`
- `http://localhost:8080/assets/main.js` → serves `assets/main.js`
- `http://localhost:8080/mf-manifest.json` → serves `mf-manifest.json`

## Additional Notes
- The Vue app uses Module Federation, so `mf-manifest.json` and `remoteEntry.js` must be accessible
- Consider adding a Gradle property to skip frontend build during development (e.g., `-PskipFrontend=true`)
- May need to update `.gitignore` to exclude `dist/` if not already done
- Ensure the `dist/` folder is not included in the source JAR, only the main JAR
- If using Spring Boot DevTools, consider excluding the dist folder from triggering restarts
- The Vite config currently has `base` commented out, which is correct for serving from `/`
- Consider handling Windows vs. Unix pnpm command differences (pnpm.cmd on Windows)
