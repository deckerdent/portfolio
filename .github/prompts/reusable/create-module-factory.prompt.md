---
agent: agent
---

# Create ModuleFactory and Central Axios Configuration

## Context

- The project is a Spring Boot 3 + Vue 3 + TypeScript micro-frontend host application using Module Federation (`@module-federation/enhanced`).
- `Module.ts` defines the `Module` class (with private backing fields and getters/setters) and the `Slot` type. It also exposes a computed `entry` getter that derives the `mf-manifest.json` URL from `sourceUrl`.
- `modules.json` lives in the Vite `public/` folder and is served at `/modules.json` at runtime (never bundled — must be fetched at runtime so it can be overridden by dropping a file next to the JAR in production).
- Multiple entries in `modules.json` may share the same `sourceUrl` (i.e. multiple exposed modules from one remote). Module Federation's `registerRemotes` requires one entry per remote, not one per module.
- The project uses `axios` for HTTP requests and `axios-retry` for retry logic.

Relevant file: `src/host/models/Module.ts`
```typescript
export type Slot = 'header' | 'footer' | 'sidebar-left' | 'sidebar-right' | 'app' | 'floating-button';

export class Module {
    private _displayName: string;
    private _sourceUrl: string;
    private _scope: string;
    private _module: string;
    private _description: string;
    private _slot: Slot;
    private _activationUrl: string;

    constructor(data: { displayName: string; sourceUrl: string; scope: string; module: string; description: string; slot: Slot; activationUrl: string; }) { ... }

    get entry(): string {
        return this._sourceUrl ? `${this._sourceUrl.replace(/\/$/, '')}/mf-manifest.json` : '/mf-manifest.json';
    }
    // ...getters/setters for all fields
}
```

## Objective

Create two files:
1. `src/host/services/ModuleFactory.ts` — a factory class that fetches and parses `modules.json`, exposes the full module list, and provides a deduped list of remote entries for Module Federation registration.
2. `src/host/api/axiosInstance.ts` — a centrally configured axios instance with `axios-retry`.

## Requirements

- [ ] `ModuleFactory` is a class (not a plain object or namespace) in `src/host/services/ModuleFactory.ts`
- [ ] Has a static `load(url?: string): Promise<Module[]>` method that fetches `modules.json` (default URL `/modules.json`) using the central axios instance and returns an array of `Module` instances
- [ ] Has a static `getUniqueEntries(modules: Module[]): { name: string; entry: string }[]` method that returns one entry per unique `sourceUrl` — keyed by `scope`, valued by `entry` — for use with `registerRemotes`
- [ ] If two modules share the same `sourceUrl` but have different `scope` values, both should appear (dedup is on `sourceUrl + scope` composite key, not `sourceUrl` alone)
- [ ] `axiosInstance.ts` creates and exports a default axios instance configured with `axios-retry`
- [ ] Retry config: 3 retries, exponential backoff, only retry on network errors or 5xx responses
- [ ] The axios instance is the only HTTP client used in `ModuleFactory` — no bare `fetch` calls
- [ ] All types are strict — no `any`
- [ ] Errors from the fetch are propagated (not silently swallowed); a failed load should reject the promise with a descriptive message

## Technical Specifications

- Language: TypeScript (strict mode)
- Framework: Vite + Vue 3 (browser environment — no Node-only APIs)
- HTTP: `axios` + `axios-retry`
- Module system: ESM (`import`/`export`)
- File locations:
  - `src/host/api/axiosInstance.ts`
  - `src/host/services/ModuleFactory.ts`
- Import `Module` and `Slot` from `../models/Module` (relative path from services/)
- Do not use barrel (`index.ts`) files unless they already exist

## Constraints

- Must work in a browser — no `fs`, `path`, or Node built-ins
- Do not use `import modulesJson from '/modules.json'` — the file must be fetched at runtime
- `axiosInstance` should not set a `baseURL` — callers pass full paths so it works both from Vite dev (port 4202) and Spring Boot (port 8080) without configuration
- Do not add `axios-retry` as a side effect to the global `axios` default — only configure the exported instance

## Success Criteria

- [ ] `ModuleFactory.load()` returns `Module[]` with all fields correctly mapped from JSON
- [ ] `ModuleFactory.getUniqueEntries()` deduplicates correctly: given three modules all with `sourceUrl: ""` and `scope: "portal"`, it returns exactly one entry `{ name: "portal", entry: "/mf-manifest.json" }`
- [ ] If two modules share `sourceUrl` but differ in `scope`, both appear in the result of `getUniqueEntries()`
- [ ] The axios instance retries on network error and 5xx, does not retry on 4xx
- [ ] TypeScript compiles with no errors under `strict: true`
- [ ] No `any` types anywhere in either file

## Examples

### `modules.json` input (three modules, one remote)
```json
[
  { "displayName": "Default App", "sourceUrl": "", "scope": "portal", "module": "DefaultApp", "description": "", "slot": "app", "activationUrl": "/" },
  { "displayName": "Test App",    "sourceUrl": "", "scope": "portal", "module": "TestApp",    "description": "", "slot": "sidebar-left", "activationUrl": "/" },
  { "displayName": "Hello",       "sourceUrl": "", "scope": "portal", "module": "HelloComponent", "description": "", "slot": "header", "activationUrl": "/" }
]
```

### Expected `getUniqueEntries()` output
```typescript
[{ name: "portal", entry: "/mf-manifest.json" }]
// Only one entry because all three share the same sourceUrl + scope
```

### Expected `getUniqueEntries()` output — two remotes sharing a sourceUrl with different scopes
```typescript
// Input has sourceUrl: "" but scope: "portal" and scope: "admin"
[
  { name: "portal", entry: "/mf-manifest.json" },
  { name: "admin",  entry: "/mf-manifest.json" }
]
```

## Additional Notes

- `Module.entry` already computes the correct manifest URL — `getUniqueEntries` should use it rather than re-deriving it
- The dedup key should be the composite `${scope}::${sourceUrl}` to correctly handle the two-remotes-same-host case
- Consider exporting `axiosInstance` as a named export as well as the default, so it can be imported either way
- `axios-retry` v4+ uses a named export: `import axiosRetry from 'axios-retry'`
