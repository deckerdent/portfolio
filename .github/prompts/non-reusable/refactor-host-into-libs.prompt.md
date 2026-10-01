---
agent: agent
---

# Refactor Host Application into `@portfolio` Scoped Libraries

## Context

- The portal host is a Vite + TypeScript + Module Federation (Navigo-based) SPA living under `apps/portal/src/main/resources/static/src/host/`.
- Current structure is a flat in-source layout with these areas:
  - `api/axiosInstance.ts` — configured Axios instance with retry
  - `errors/` — `HostConfigError`, `SlotNotFoundError`, `MountFunctionNotFoundError`
  - `host/` — `HostService`, `Host` model, `HostFactory`, `RouterService`
  - `module/` — `ModuleService`, `Module` model, `ModuleFactory`
  - `main.ts` — entry point calling `HostService.bootstrap()`
- Remote modules (e.g. `src/apps/test/Module.ts`) export a `mount(container, basename)` function that returns `{ unmount }`.
- The monorepo is managed with pnpm workspaces + Nx under `@portfolio` namespace convention.
- All packages live under `apps/portal/src/main/resources/static/`.

## Objective

Extract the current host source into three scoped Nx/pnpm library packages (`@portfolio/core`, `@portfolio/host`, `@portfolio/app`) stored under `libs/browser/`, and define a `ModuleLifecycle` contract in `@portfolio/core` that remote apps implement to give the host full lifecycle control.

## Requirements

### Workspace & Package Setup
- [ ] `libs/browser/` is the grouping folder for all browser-targeted libraries — it is **not** a package itself
- [ ] Create `libs/browser/core/` — initialise as a Vite library project under `@portfolio/core`
- [ ] Create `libs/browser/host/` — initialise as a Vite library project under `@portfolio/host`
- [ ] Create `libs/browser/app/` — initialise as a Vite library project under `@portfolio/app`
- [ ] Register all three packages in `pnpm-workspace.yaml`
- [ ] Add proper `tsconfig` path aliases for all three `@portfolio/*` packages in `tsconfig.base.json`

### `@portfolio/core`
- [ ] Move `src/host/api/axiosInstance.ts` into `libs/browser/core/src/api/`
- [ ] Move `src/host/errors/` (`SlotNotFoundError`, `MountFunctionNotFoundError`) into `libs/browser/core/src/errors/`
- [ ] Move `src/host/module/model/Module.ts` and `src/host/module/factory/ModuleFactory.ts` into `libs/browser/core/src/module/`
- [ ] Move `src/host/module/ModuleService.ts` into `libs/browser/core/src/`
- [ ] Declare a `ModuleLifecycle` interface (see _Technical Specifications_ below) and export it from the lib's public index
- [ ] Add a `lifecycle` property of type `ModuleLifecycle | null` to the `Module` class (defaults to `null`)
- [ ] Add `init(): Promise<void>` method on `Module` — calls `loadRemote`, stores the result as `lifecycle` if `lifecycle` is currently `null`, then calls `lifecycle.load?.()` 
- [ ] Add `mount(container: HTMLElement, basename: string): Promise<void>` method on `Module` — if `lifecycle` is null call `init()` first, then call `lifecycle.bootstrap?.()` followed by `lifecycle.mount(container, basename)`
- [ ] Add `unmount(): Promise<void>` method on `Module` — calls `lifecycle.unload?.()` then `lifecycle.unmount?.()`
- [ ] `ModuleService.mount()` delegates to `module.mount(slot, activationUrl)` instead of calling `loadRemote` directly

### `@portfolio/host`
- [ ] Move `src/host/errors/HostConfigError.ts` into `libs/browser/host/src/errors/`
- [ ] Move `src/host/host/` (`HostService`, `Host` model, `HostFactory`, `RouterService`) into `libs/browser/host/src/`
- [ ] `@portfolio/core` is a dependency of `@portfolio/host`

### `@portfolio/app`
- [ ] Provide shared Vue + Vue Router setup helpers for remote app modules (e.g. `createMountable(App, routes)` factory that returns a `ModuleLifecycle`-compliant object)
- [ ] Lives at `libs/browser/app/`
- [ ] `@portfolio/core` is a dependency of `@portfolio/app` (for the `ModuleLifecycle` type)

### Entry Point
- [ ] Update `src/host/main.ts` to import from `@portfolio/host`
- [ ] Update the existing `src/apps/test/Module.ts` to return a `ModuleLifecycle`-compliant object

## Technical Specifications

- **Language**: TypeScript (strict mode)
- **Build tool**: Vite library mode for each lib
- **Package manager**: pnpm workspaces
- **Module system**: ESM (`"type": "module"`)
- **Namespace**: All packages scoped under `@portfolio/`

### `ModuleLifecycle` Interface (declared in `@portfolio/core`)

```typescript
export interface ModuleLifecycle {
    /** Called once after loadRemote resolves — use for one-time setup */
    load?: () => Promise<void> | void;
    /** Called before every mount — use for pre-mount preparation */
    bootstrap?: () => Promise<void> | void;
    /** Mount the app into the given container at the given basename */
    mount: (container: HTMLElement, basename: string) => Promise<void> | void;
    /** Called before unmount — use for cleanup / state teardown */
    unload?: () => Promise<void> | void;
    /** Unmount the app from the DOM */
    unmount?: () => Promise<void> | void;
}
```

### Lifecycle Call Order (on `Module`)

```
init()     → loadRemote → lifecycle.load?()
mount()    → init() if lifecycle null → lifecycle.bootstrap?() → lifecycle.mount()
unmount()  → lifecycle.unload?() → lifecycle.unmount?()
```

### Existing `src/apps/test/Module.ts` — expected export shape after refactor

```typescript
import type { ModuleLifecycle } from '@portfolio/core';

export function mount(container: HTMLElement, basename: string): ModuleLifecycle {
    // create Vue app, router, etc.
    return {
        mount(container, basename) { app.mount(container); },
        unmount() { app.unmount(); },
    };
}
```

> **Note**: The host's `ModuleService` calls `loadRemote` which resolves to this export. `ModuleService` currently checks for a `mount` function on the resolved object (`isMountable`). After refactoring `Module` will own this logic — `ModuleService` simply calls `module.mount()`.

## Constraints

- Do **not** change the public API of `HostService.bootstrap()` or `RouterService`
- Remote modules must remain loadable via Module Federation — no changes to federation config
- The existing `Navigo`-based routing in `RouterService` must not be broken
- The `data-navigo` + `updatePageLinks()` pattern in `RouterService` must be preserved
- The `mountedInstances` unmount tracking in `RouterService` must continue to call `module.unmount()` (now delegated to `Module`)
- Keep all custom error classes; do not replace with generic `Error`
- No circular dependencies between `@portfolio/core → @portfolio/host`

## Success Criteria

- [ ] `pnpm install` resolves cleanly with all three `@portfolio/*` packages linked
- [ ] `@portfolio/core`, `@portfolio/host` and `@portfolio/app` each build independently via `vite build`
- [ ] `main.ts` imports only from `@portfolio/host` — no relative imports to old `src/host/` paths
- [ ] `src/apps/test/Module.ts` exports a `ModuleLifecycle`-compliant object and TypeScript reports no errors
- [ ] Navigating between host routes mounts/unmounts remote modules correctly (no zombie Vue app listeners)
- [ ] `document.title` updates correctly on route activation (`<hostTitle> - <moduleName>`)
- [ ] No TypeScript `strict` errors across all four libs and the host entry point
- [ ] Existing `SlotNotFoundError`, `MountFunctionNotFoundError`, `HostConfigError` are preserved and still thrown in the correct places

## Additional Notes

- `ModuleLifecycle.load` vs `bootstrap`: `load` is a one-time post-`loadRemote` hook (cache warm-up, i18n init, etc.); `bootstrap` runs before every mount (restore state, reset stores, etc.). Implementations may omit both.
- The `Module.init()` idempotency guard (`if lifecycle === null`) means repeated calls are safe — important since `mount()` calls `init()` lazily.
- `RouterService.mountedInstances` currently stores `() => void` unmount callbacks. After refactor it should call `module.unmount()` directly — consider storing the `Module` reference instead of a raw callback.
- `@portfolio/app` may initially be a thin wrapper; its value grows as more remote apps are added and share the same Vue + Vue Router bootstrapping pattern.
- Nx project boundaries should be configured so `@portfolio/app` cannot import from `@portfolio/host` (app modules must not depend on host internals).
