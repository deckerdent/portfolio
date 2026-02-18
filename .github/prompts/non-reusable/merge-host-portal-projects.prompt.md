---
agent: agent
---

# Merge Host and Portal Frontend Projects

## Context
- Current situation: Two separate Vite projects exist in the workspace:
  - `/static/host`: Host application that loads remote modules via Module Federation
  - `/static/portal`: Portal application with multiple apps (default, test) and web components
- Both projects have separate:
  - vite.config.mts files
  - package.json files
  - src/ directories
  - Build outputs (dist/)
- Portal exposes modules: DefaultApp, TestApp, HelloComponent
- Host consumes these modules and mounts them to different slots
- Both use Module Federation, Vue 3, TypeScript, and pnpm workspace
- Build tool: Vite with @module-federation/vite plugin
- Current portal structure: src/apps/default/, src/apps/test/, src/components/

## Objective
Consolidate the host and portal projects into a single unified frontend project in the portal directory. The host application code should become part of the portal project, with a merged Vite configuration supporting both host and remote module functionality.

## Requirements
- [ ] Move all host source code (src/, public/) into the portal project structure
- [ ] Create a new directory structure in portal to accommodate both host and remote code
- [ ] Merge vite.config.mts files from both projects into a single configuration
- [ ] Merge package.json dependencies and scripts
- [ ] Update Module Federation configuration to support both host and remote roles
- [ ] Preserve all existing functionality:
  - Host loads and mounts DefaultApp, TestApp, HelloComponent
  - Portal exposes the three modules
  - All apps and components work as before
- [ ] Update import paths and references to reflect new structure
- [ ] Consolidate build outputs appropriately
- [ ] Update index.html files as needed
- [ ] Remove the now-empty static/host directory after migration
- [ ] Ensure pnpm workspace configuration reflects the change
- [ ] Update any references in the Java Spring Boot project (build.gradle, etc.)

## Technical Specifications
- Language/Framework: TypeScript, Vue 3 Composition API
- Build tool: Vite 7.x with unified configuration
- Module Federation: @module-federation/vite, @module-federation/enhanced
- Package manager: pnpm with workspace support
- Proposed directory structure:
  ```
  portal/
    src/
      host/           # Former host app code
        main.ts
        models/
        (host-specific code)
      apps/
        default/      # Existing portal default app
        test/         # Existing portal test app
      components/     # Existing web components
      shared/         # Shared utilities/types
    public/           # Static assets for both
    index-host.html   # Host entry point
    index.html        # Portal dev entry point (existing)
    vite.config.mts   # Merged configuration
    package.json      # Merged dependencies
  ```
- Vite config should support:
  - Multiple entry points if needed
  - Both host and remote Module Federation roles
  - Proper build outputs for both functionalities
- Coding standards: Maintain existing Vue 3 patterns, TypeScript strict mode

## Constraints
- Must maintain all existing functionality without breaking changes
- Host application must still be able to load portal remotes
- Module Federation must continue to work correctly
- Build process should remain efficient (no significant build time increase)
- Development mode (pnpm dev) should still work for both host and remote testing
- TypeScript paths and imports must resolve correctly
- Hot Module Replacement (HMR) should work for all code
- Java Spring Boot integration must continue to serve static files correctly
- Single package.json should not create dependency conflicts

## Success Criteria
- [ ] Portal project builds successfully with no TypeScript errors
- [ ] Host functionality works: loads and mounts all three remote modules
- [ ] All three portal modules (DefaultApp, TestApp, HelloComponent) expose correctly
- [ ] Development server (pnpm dev) works and shows host with loaded remotes
- [ ] Production build generates correct assets for both host and remote consumption
- [ ] No duplicate dependencies in package.json
- [ ] Java Gradle build still includes the frontend assets correctly
- [ ] All import paths resolve without errors
- [ ] HMR works during development for changes in any part of the code
- [ ] static/host directory is removed or archived
- [ ] pnpm-workspace.yaml reflects the simplified structure

## Examples

### Proposed Vite Config Structure
```typescript
// vite.config.mts
export default defineConfig(({ mode, command }) => ({
  root: import.meta.dirname,
  
  // Multi-page or conditional entry points
  build: {
    rollupOptions: {
      input: {
        host: './index-host.html',
        portal: './index.html',
      },
    },
  },
  
  plugins: [
    ViteEjsPlugin({ env: { mode, command } }),
    vue(),
    federation({
      name: 'portal',
      filename: 'remoteEntry.js',
      // Host configuration
      remotes: {
        portal: 'http://localhost:4202/mf-manifest.json',
      },
      // Remote configuration
      exposes: {
        './DefaultApp': './src/apps/default/Module.ts',
        './TestApp': './src/apps/test/Module.ts',
        './HelloComponent': './src/components/HelloComponent.ts',
      },
      // Shared between host and remote
      shared: {
        vue: { singleton: true, requiredVersion: '^3.5.0' },
        'vue-router': { singleton: true, requiredVersion: '^4.5.0' },
      },
    }),
  ],
}));
```

### Merged Package.json
```json
{
  "name": "portal",
  "scripts": {
    "dev": "vite",
    "dev:host": "vite --mode host",
    "build": "vite build",
    "build:all": "vite build --mode production"
  },
  "dependencies": {
    // Merged from both projects
    "@module-federation/enhanced": "catalog:default",
    "@module-federation/vite": "catalog:default",
    "vue": "catalog:default",
    "vue-router": "catalog:default"
  }
}
```

### Updated Import Paths
```typescript
// From: import { MicroFrontendApp } from './models/MicroFrontendApp'
// To:   import { MicroFrontendApp } from '../host/models/MicroFrontendApp'
```

## Additional Notes
- Consider whether the host should be a separate build target or integrated into the same build
- The index-host.html might need different base paths or configurations
- Module Federation's manifest.json should correctly expose and consume modules within the same build
- May need to use Vite's multi-page app approach or conditional builds
- Ensure that when running as "remote" mode, host functionality is not included in the bundle
- Consider adding a README explaining the unified structure and how to build/run
- Test that the Java Spring Boot app correctly serves both the host entry point and remote modules
- Gradle task for building Vue apps may need updating to reflect single project
- Consider keeping separate TypeScript configs if needed (tsconfig.host.json, tsconfig.remote.json)
- Watch for circular dependencies between host and remote code
- May need environment variables or build flags to differentiate host vs remote builds
