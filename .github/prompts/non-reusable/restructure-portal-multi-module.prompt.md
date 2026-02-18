---
agent: agent
---

# Restructure Portal for Multi-Module Federation Export

## Context
- Current situation: Portal Vue application exists in `/static/portal` with a single Module.ts entry point
- Module Federation is configured but only exposes one module (`./Module`)
- Need to support multiple independently consumable modules from the same portal project
- Relevant technologies/frameworks: Vue 3, Vite, Module Federation, TypeScript
- Build tool: Vite with @module-federation/vite plugin
- Current structure: Flat src/ folder with main.ts, Module.ts, app/, router/, views/, styles.css

## Objective
Restructure the portal application to support multiple Module Federation exports: two complete Vue applications and one standalone web component. Organize code into `apps/` and `components/` folders for clear separation of concerns.

## Requirements
- [ ] Create `src/apps/` directory to house multiple Vue applications
- [ ] Create `src/apps/default/` containing the current application code
- [ ] Move existing main.ts, Module.ts, app/, router/, views/, assets/ into `src/apps/default/`
- [ ] Create `src/apps/test/` as a complete copy of the default app
- [ ] Modify the HomeView.vue in `src/apps/test/` to have distinctly different content/styling
- [ ] Create `src/components/` directory for standalone web components
- [ ] Create a standalone web component using Vue's `defineCustomElement` API
- [ ] The web component should be a simple div displaying "hello from component"
- [ ] Update Module Federation config to expose all three modules:
  - `./DefaultApp` → default app's Module.ts
  - `./TestApp` → test app's Module.ts
  - `./HelloComponent` → standalone web component
- [ ] Ensure both apps and the component can be independently loaded by the host
- [ ] Update any import paths broken by the restructure
- [ ] Keep shared utilities in src/ if needed (or create src/shared/)

## Technical Specifications
- Language/Framework: TypeScript, Vue 3 Composition API
- Web Component: Use `defineCustomElement` from 'vue'
- Module Federation: @module-federation/vite configuration
- File naming conventions:
  - Apps: `Module.ts` exports mount function
  - Components: `[ComponentName].ts` exports custom element
- Coding standards: Follow existing Vue 3 patterns, use TypeScript strict mode
- Router: Each app should have its own router instance (don't share router state)

## Constraints
- Must maintain existing functionality of the default app
- Both apps must be independently mountable
- Web component must work as a standard custom element (can be used with `customElements.define()`)
- No breaking changes to the host application's current consumption of the default app
- Build output must remain compatible with Module Federation's remote loading
- File paths in vite.config.mts must be updated to reflect new structure

## Success Criteria
- [ ] Portal builds successfully with no TypeScript errors
- [ ] Default app loads and functions identically to before restructure
- [ ] Test app loads with visibly different HomeView content
- [ ] Web component can be imported and registered as a custom element
- [ ] All three modules are exposed in the Module Federation manifest
- [ ] Host application can load each module independently via `loadRemote()`
- [ ] Hot module replacement (HMR) works for all apps and components during development
- [ ] Production build generates correct assets for all three modules

## Examples

### Module Federation Config Structure
```typescript
exposes: {
  './DefaultApp': './src/apps/default/Module.ts',
  './TestApp': './src/apps/test/Module.ts',
  './HelloComponent': './src/components/HelloComponent.ts',
}
```

### Web Component Implementation
```typescript
// src/components/HelloComponent.ts
import { defineCustomElement } from 'vue';

const HelloComponent = defineCustomElement({
  template: `<div>hello from component</div>`,
  styles: [`div { padding: 1rem; color: blue; }`]
});

export default HelloComponent;
```

### App Module Structure
```typescript
// src/apps/default/Module.ts
export function mount(container: HTMLElement) {
  // Mount Vue app to container
}
```

### Host Consumption
```typescript
// In host application
const defaultApp = await loadRemote("portal/DefaultApp");
const testApp = await loadRemote("portal/TestApp");
const HelloComponent = await loadRemote("portal/HelloComponent");

// Register web component
customElements.define('hello-component', HelloComponent);
```

## Additional Notes
- The web component should be framework-agnostic once compiled (standard custom element)
- Consider creating a shared types file if both apps need common interfaces
- The test app's altered HomeView should be visually distinct (different text, colors, or layout)
- Ensure styles.css is either duplicated or imported correctly in both apps
- Each app should have its own independent Vue instance (don't share app state)
- The component should not require a router or complex setup
- Consider adding a README in src/apps/ explaining the multi-app structure
