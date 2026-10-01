---
agent: agent
---

# Set Up Navigo Router in Host main.ts

## Context

- The project is a Spring Boot 3 + Vue 3 + TypeScript micro-frontend host using Module Federation (`@module-federation/enhanced`).
- The host application (`src/host/main.ts`) dynamically loads modules from `modules.json` via `ModuleFactory.load()`. Each `Module` has a `slot`, `activationUrl`, `scope`, and `module` property.
- The `mountModule(module: Module)` function handles loading the remote, checking the slot and mount function, and mounting it — including typed error handling.
- Routing is needed to mount only the relevant "app"-slot modules when a route is activated. Non-app modules (header, sidebar, footer, etc.) are mounted unconditionally on startup; only slot `"app"` modules are route-driven.
- The router library to use is **Navigo** (`navigojs` on npm). It must be added to the pnpm workspace catalog and installed into the portal package.

Relevant types:
```typescript
type Slot = 'header' | 'footer' | 'sidebar-left' | 'sidebar-right' | 'app' | 'floating-button';

class Module {
    scope: string;
    module: string;
    slot: Slot;
    activationUrl: string;  // e.g. "/" or "/about"
    // ...
}
```

Current `main.ts` flow:
```typescript
const bootstrap = async (): Promise<void> => {
    init({ name: 'host', remotes: [], shared: {} });
    const modules = await ModuleFactory.load();
    const manifests = ModuleFactory.getUniqueEntries(modules);
    registerRemotes(manifests);
    modules.forEach(module => { mountModule(module); });
};
await bootstrap();
```

## Objective

Add Navigo-based client-side routing to the host. Only modules with `slot === 'app'` are eligible to register routes (via their `activationUrl`). When a route is hit, **all slots** are updated: for each slot, the previously mounted content is removed, then the first module in the array whose `activationUrl` matches the current route is mounted. Slots with no matching module for the active route are left untouched — the previously mounted module stays.

## Requirements

- [ ] Add `navigo` to the pnpm workspace catalog (`pnpm-workspace.yaml`) with the latest compatible version
- [ ] Install `navigo` into the portal package `package.json` using the catalog reference
- [ ] Create a `const initRouter` arrow function in `main.ts` that accepts the full `Module[]` array
- [ ] Inside `initRouter`, filter modules where `slot === 'app'` to get route-driven modules
- [ ] Group the filtered modules by `activationUrl` — multiple modules can share the same route (e.g. a sidebar and main content both activating on `/dashboard`)
- [ ] Register one Navigo route per unique `activationUrl` using the grouped map
- [ ] `activate(activationUrl: string, modules: Module[])` is a separate `const` arrow function that:
  - For each unique `slot` value present in `modules`, finds the **first** module in the array where `activationUrl` matches the current route AND `slot` matches
  - Before mounting the matched module, clears the slot's existing DOM content (`innerHTML = ''` or equivalent)
  - Calls `mountModule(module)` only for slots that have a match — slots with no matching module for this route are left untouched (not cleared)
  - If two modules share the same `slot` and `activationUrl`, only the first one in the array is mounted
- [ ] Call `router.resolve()` after all routes are registered to trigger the initial route on page load
- [ ] `initRouter` is called from `bootstrap()` after `registerRemotes()`, passing the loaded `modules` array
- [ ] On startup, no modules are mounted unconditionally — all mounting is driven by the initial route resolved by `router.resolve()`
- [ ] All new functions use `const` arrow function syntax (consistent with existing file style)
- [ ] No `any` types

## Technical Specifications

- Language: TypeScript strict mode
- Router: Navigo (latest) — import as `import Navigo from 'navigo'`
- File to modify: `src/host/main.ts`
- Files to modify for dependency: `pnpm-workspace.yaml` (catalog), `src/main/resources/static/package.json` (portal package)
- Navigo hash mode: use `new Navigo('/')` (history API mode, not hash)
- Function order in file: `activate` → `initRouter` → `mountModule` → `bootstrap` (to avoid temporal dead zone issues with `const`)

## Constraints

- Do not use `vue-router` — this is the host shell, not a Vue app
- `mountModule` is already defined — do not redefine or modify it
- The `modules` array must be fetched only once (in `bootstrap`) and passed into `initRouter` and `activate` — no second fetch
- Do not call `mountModule` for app-slot modules unconditionally at startup — only route handlers should mount them
- Navigo routes must be registered before `router.resolve()` is called

## Success Criteria

- [ ] `pnpm-workspace.yaml` contains `navigo: ^<latest>` in the `catalogs.default` section
- [ ] `package.json` for the portal package lists `navigo: 'catalog:default'` in `dependencies`
- [ ] `initRouter` registers exactly one route per unique `activationUrl` among slot `"app"` modules
- [ ] Navigating to `/` triggers `activate('/', modules)` which mounts the first matching module per slot where `activationUrl === '/'`
- [ ] A slot with no module matching the active route is left untouched — its previously mounted content is preserved
- [ ] A slot with a matching module is cleared before the new module is mounted — no duplicate content accumulates across route changes
- [ ] If two modules share the same `slot` and `activationUrl`, only the first in the array is mounted
- [ ] TypeScript compiles with no errors under `strict: true`

## Examples

### modules.json with two routes

```json
[
  { "scope": "portal", "module": "DefaultApp",     "slot": "app",          "activationUrl": "/" },
  { "scope": "portal", "module": "AboutApp",       "slot": "app",          "activationUrl": "/about" },
  { "scope": "portal", "module": "HelloComponent", "slot": "header",       "activationUrl": "/" },
  { "scope": "portal", "module": "NavComponent",   "slot": "header",       "activationUrl": "/about" }
]
```

### Expected router registration

```typescript
// Routes are derived only from slot === 'app' modules:
// Route for "/"      → activationUrl from DefaultApp
// Route for "/about" → activationUrl from AboutApp
// Routes are NOT registered from HelloComponent or NavComponent (slot !== 'app')
```

### Expected activate('/') behaviour

```typescript
activate('/', modules);
// slot 'app'    → first match: DefaultApp    → mountModule(DefaultApp)
// slot 'header' → first match: HelloComponent → mountModule(HelloComponent)
// slot 'footer' → no match → untouched (previously mounted content stays)
```

### Expected activate('/about') behaviour

```typescript
activate('/about', modules);
// slot 'app'    → first match: AboutApp     → mountModule(AboutApp)
// slot 'header' → first match: NavComponent → mountModule(NavComponent)
// slot 'footer' → no match → untouched
```

### First-match-per-slot dedup

```typescript
// If modules = [
//   { module: 'AppA', slot: 'app', activationUrl: '/' },
//   { module: 'AppB', slot: 'app', activationUrl: '/' },  // same slot + url
// ]
// activate('/') mounts only AppA (first in array), AppB is ignored
```

## Additional Notes

- Slots are cleared (`innerHTML = ''`) immediately before mounting the new module — this ensures no stale content from a previous route remains
- Clearing only happens when a match is found; slots with no match are not touched
- `router.resolve()` must be called after all `.on()` registrations to evaluate the current URL
- If two modules share the same `slot` and `activationUrl`, only the **first** in the array is mounted — subsequent matches are ignored
- The `activate` function should be pure with respect to its inputs — it receives the full `modules` array and filters internally, making it easily testable
