---
agent: agent
---

# Create Module Federation Host Application

## Context
- Current situation: Monorepo with self-contained Spring Boot + Vue microfrontend apps
- Need a host application to orchestrate and load multiple microfrontends dynamically
- Host will be a vanilla TypeScript application (not a framework-based app)
- Uses EJS templating for HTML generation and Vite for bundling
- Integrates with Module Federation for runtime loading of remote microfrontends
- Portal microfrontend already exists and needs to be integrated as the first remote

## Objective
Create a vanilla TypeScript host application that provides a flexible slot-based layout using Module Federation to dynamically load and mount microfrontend applications based on a JSON configuration file.

**Before starting implementation, create a TODO list using the manage_todo_list tool with all tasks from the Requirements section below, broken down into specific, actionable steps.**

## Requirements

### Project Structure
- [ ] Use Nx web generator to create TypeScript application: `pnpm nx g @nx/web:application host --directory=apps/portal/src/main/resources/static/host --bundler=vite --unitTestRunner=vitest --e2eTestRunner=none --style=css`
- [ ] Host will be served by the existing portal Spring Boot backend
- [ ] Frontend code placed in `apps/portal/src/main/resources/static/host/`
- [ ] Configure to serve from `/static/host/` path
- [ ] No separate Gradle configuration needed (uses portal's backend)

### HTML Layout (EJS Template)
- [ ] Create `index.html.ejs` template file
- [ ] Implement flexbox-based layout with the following slots:
  - `slot:header` - Full viewport width, height 0 (expands with content), fixed to top
  - `slot:footer` - Full viewport width, height 0 (expands with content), fixed to bottom
  - `slot:sidebar-left` - Height fills between header/footer, width 0 (expands with content)
  - `slot:sidebar-right` - Height fills between header/footer, width 0 (expands with content)
  - `slot:app` - Fills remaining space in center
  - `slot:floating-button` - Lower right corner, width/height 0 (expands with content)
- [ ] Use semantic HTML5 tags with appropriate IDs matching the slot pattern
- [ ] Include CSS for flex layout ensuring slots collapse when empty
- [ ] Inject bundled TypeScript as script tag in HTML

### Configuration Model
- [ ] Create `assets/apps.json` file to store microfrontend configurations
- [ ] Create TypeScript model class `MicroFrontendApp` with properties:
  - `displayName: string` - Display name (letters, numbers, spaces, underscores, hyphens only)
  - `sourceUrl: string` - Remote entry URL (must be valid relative or absolute URL)
  - `scope: string` - Module Federation scope name (letters, numbers, underscores only)
  - `module: string` - Module Federation module path (letters, numbers, underscores only)
  - `description: string` - Short description (max 100 characters)
  - `activationUrl: string` - Derived from displayName (spaces/underscores → hyphens)
  - `slot: string` - Target slot ID (e.g., 'app', 'sidebar-left')
- [ ] Implement validation for each property in the model class constructor
- [ ] Create factory class `MicroFrontendAppFactory` to load and parse apps.json
- [ ] Factory should return array of validated `MicroFrontendApp` instances

### Module Federation Integration
- [ ] Install `@module-federation/runtime` and `@module-federation/bridge-vue3` packages
- [ ] Configure Vite with `@module-federation/vite` plugin for host mode
- [ ] Initialize Module Federation runtime in TypeScript application
- [ ] Use Module Federation Bridge to create framework-agnostic wrappers
- [ ] Dynamically register remotes from apps.json configuration
- [ ] Use Bridge to load remotes and get their exposed modules
- [ ] Implement dynamic import and mount logic for each microfrontend using Bridge API
- [ ] Handle mounting microfrontends to their designated slot containers

### Portal Microfrontend Integration
- [ ] Wrap portal's Vue app using `createBridgeComponent` from `@module-federation/bridge-vue3`
- [ ] Update portal's `Module.ts` to export bridge-wrapped component
- [ ] Bridge component should handle mount/unmount lifecycle automatically
- [ ] Ensure portal exposes the bridge component via Module Federation in vite.config.mts
- [ ] Configure portal as first entry in host's apps.json
- [ ] Use Bridge API in host to load and mount portal into `slot:app` container
- [ ] Test portal loads and mounts correctly with proper lifecycle management

### Build Configuration
- [ ] Configure Vite build to generate production bundle
- [ ] Set up EJS compilation to inject bundle references
- [ ] Configure output directory as `dist/`
- [ ] Ensure TypeScript compilation works correctly
- [ ] Add build scripts to package.json: `dev`, `build`, `preview`

## Technical Specifications

### Languages & Frameworks
- TypeScript 5.6.2
- Vite 5.4.11 for bundling
- EJS for HTML templating
- Module Federation runtime 0.24.0
- Module Federation Vite plugin 1.11.0
- Module Federation Bridge Vue3 (latest stable version)
- Module Federation Bridge React (for future React-based remotes)

### Project Configuration Files
- `package.json` - NPM dependencies and scripts
- `tsconfig.json` - TypeScript compiler options
- `vite.config.ts` - Vite bundler and Module Federation config
- `build.gradle` - Gradle build for Spring Boot backend

### Coding Standards
- Use ES modules and modern TypeScript features
- Implement proper error handling for configuration loading
- Use async/await for dynamic imports
- Follow existing monorepo patterns (similar to portal app)
- Use strict TypeScript mode with proper type definitions

### Module Federation Configuration
- Host application name: `host`
- Remotes registered dynamically from apps.json
- Use Bridge API for framework-agnostic remote loading
- Bridge handles component lifecycle (mount/unmount/update)
- Shared dependencies: None initially (each remote manages its own)
- Remote entry file naming: `remoteEntry.js`
- Bridge components exposed as default exports from remotes

## Constraints

### Validation Rules
- `displayName`: Must match `/^[a-zA-Z0-9\s_-]+$/`
- `sourceUrl`: Must be valid URL (relative or absolute) using URL constructor
- `scope`: Must match `/^[a-zA-Z0-9_]+$/`
- `module`: Must match `/^[a-zA-Z0-9_]+$/`
- `description`: Max length 100 characters
- `activationUrl`: Auto-generated, replaces `/[\s_]+/g` with `-` in displayName
- `slot`: Must be one of: `header`, `footer`, `sidebar-left`, `sidebar-right`, `app`, `floating-button`

### Layout Behavior
- Slots must collapse to 0 width/height when empty
- Slots must expand smoothly with content using flexbox
- Main app slot must fill all remaining space
- Layout must be responsive and work on various screen sizes
- No hardcoded dimensions except initial 0 for collapsible slots

### Compatibility
- Must work with existing portal Vue 3 microfrontend
- Must support future microfrontends built with any framework
- Must serve correctly from Spring Boot static resources
- Must work in modern browsers (Chrome, Firefox, Edge, Safari latest)

## Success Criteria

- [ ] Host application builds successfully with `pnpm build`
- [ ] EJS template generates valid HTML with all slot containers
- [ ] CSS flexbox layout collapses empty slots and expands with content
- [ ] apps.json loads and parses into MicroFrontendApp instances
- [ ] All validation rules enforce correctly, throwing errors for invalid data
- [ ] Module Federation runtime initializes without errors
- [ ] Module Federation Bridge packages installed and configured
- [ ] Portal microfrontend wrapped with Bridge component
- [ ] Portal loads dynamically from host application using Bridge API
- [ ] Portal mounts correctly into `slot:app` container via Bridge
- [ ] Bridge handles component lifecycle (mount/unmount) automatically
- [ ] Bridge component accepts and uses rootPath configuration
- [ ] Spring Boot serves host application at `http://localhost:8080/static/host/`
- [ ] Dev server runs with `pnpm --filter host dev`
- [ ] No console errors during runtime
- [ ] All TypeScript compiles without errors or warnings

## Examples

### apps.json Structure
```json
[
  {
    "displayName": "Portal App",
    "sourceUrl": "http://localhost:4200/remoteEntry.js",
    "scope": "portal",
    "module": "Module",
    "description": "Main portal application with routing and navigation",
    "slot": "app"
  }
]
```

### Expected Bridge Component Export (Portal Module.ts)
```typescript
import { createBridgeComponent } from '@module-federation/bridge-vue3';
import { createApp } from 'vue';
import { createRouter, createWebHistory } from 'vue-router';
import App from './app/App.vue';
import HomeView from './views/HomeView.vue';
import AboutView from './views/AboutView.vue';

const bridgeComponent = createBridgeComponent({
  rootComponent: App,
  appOptions: (bridgeInfo) => {
    const router = createRouter({
      history: createWebHistory(bridgeInfo.rootPath || '/static/portal/'),
      routes: [
        { path: '/', name: 'home', component: HomeView },
        { path: '/about', name: 'about', component: AboutView },
      ],
    });
    
    return {
      router,
    };
  },
});

export default bridgeComponent;
```

### Host Loading Remote with Bridge
```typescript
import { loadRemote } from '@module-federation/runtime';

async function loadMicrofrontend(app: MicroFrontendApp) {
  try {
    // Load remote module using Bridge
    const remote = await loadRemote<any>(`${app.scope}/${app.module}`);
    
    // Get the bridge component (default export)
    const BridgeComponent = remote.default;
    
    // Mount to container
    const container = document.getElementById(`slot:${app.slot}`);
    if (!container) {
      throw new Error(`Slot container not found: ${app.slot}`);
    }
    
    // Bridge handles the mounting automatically
    BridgeComponent.mount(container, {
      rootPath: app.activationUrl,
    });
    
  } catch (error) {
    console.error(`Failed to load microfrontend ${app.displayName}:`, error);
  }
}
```

### Expected HTML Layout Structure
```html
<body>
  <div id="layout">
    <header id="slot:header"></header>
    <div id="main-content">
      <aside id="slot:sidebar-left"></aside>
      <main id="slot:app"></main>
      <aside id="slot:sidebar-right"></aside>
    </div>
    <footer id="slot:footer"></footer>
    <div id="slot:floating-button"></div>
  </div>
</body>
```

## Additional Notes

### Edge Cases to Handle
- Invalid JSON in apps.json should throw clear error
- Missing container elements should log warnings
- Failed remote loads should not crash entire application
- Network errors loading remotes should be caught and logged
- Multiple apps targeting same slot should be handled (last one wins or throw error)

### Future Enhancements
- Router integration to switch between apps based on URL
- Loading indicators while microfrontends load
- Error boundaries for failed microfrontend loads
- App registry/catalog UI showing available apps
- Dynamic slot resizing and responsive behavior
- Theme/styling coordination between host and remotes

### Integration with Existing Portal
- Portal already has `Module.ts` but needs to be refactored to use Bridge
- Replace manual mount/unmount functions with Bridge component wrapper
- Portal's vite.config.mts already exposes `./Module` - ensure it exports Bridge component as default
- Portal runs on port 4200 in dev, served from Spring Boot in production
- Bridge handles framework-specific mounting logic automatically
- Bridge provides consistent API across different framework remotes (Vue, React, etc.)
- Ensure Module Federation shared dependencies don't conflict

### Module Federation Bridge Benefits
- Framework-agnostic: Same loading pattern for Vue, React, Angular, etc.
- Lifecycle management: Automatic mount/unmount/update handling
- Routing integration: Bridge passes routing context to remotes
- Error boundaries: Built-in error handling for failed remotes
- Developer experience: Simplified remote consumption with consistent API
