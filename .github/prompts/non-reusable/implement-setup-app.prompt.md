---
agent: agent
---

# Implement Setup App and Bootstrapping Flow

## Context

- **Location**: `apps/portal/src/main/resources/static/src/apps/`
- **Existing app examples**: `src/apps/default/` and `src/apps/test/` — both follow the same `ModuleLifecycle` pattern with `Module.ts` (exports `mount`/`unmount`), `app/App.vue`, `views/`, `styles.css`
- **Host entry point**: `src/host/main.ts` — currently bootstraps unconditionally from `/host.json`
- **Backend API** (see `apps/portal/src/main/resources/openapi/api.yml`):
  - `GET /api/host/initialized` → `{ initialized: boolean }` — true when `title` and `basePath` props are stored in DB
  - `GET /api/host` → `HostResponse` (same shape as `HostData`: `title`, `basePath`, `appsUrl`, `sourceUrls`)
  - `POST /api/host/config` with body `{ key: string, value: string }` — upserts a named config prop
  - Backend keys for `PortalHost.applyProps()`: `"title"`, `"basePath"`, `"appsUrl"`, `"sourceUrls"` (comma-separated string)
- **Module Federation**: exposed modules registered in `vite.config.mts` under `federation.exposes`
- **`axiosInstance`**: exported from `@portfolio/core`, already used by `HostService` for HTTP calls
- **WebAwesome**: available as `@awesome.me/webawesome`; uses custom element tag names prefixed with `wa-`; emits custom events (`wa-input`, `wa-change`) NOT native DOM events, so Vue `v-model` does not work directly on WA inputs

## Objective

Create a `setup` micro-frontend app that renders an initial portal registration form, wire the host bootstrap to check the `/api/host/initialized` endpoint and conditionally load config from the DB API or the static `host.json`, and prepare the static asset files that gate the setup flow.

## Requirements

### 1 — Setup App (`src/apps/setup/`)

- [ ] Create `Module.ts` following the exact pattern of `src/apps/default/Module.ts`:
  - `let _app: App | null = null`
  - `createRouterInstance(basename)` with `createWebHistory(basename)` and a single route `{ path: '/', name: 'setup', component: SetupView }`
  - Export `mount: ModuleLifecycle['mount']` and `unmount: NonNullable<ModuleLifecycle['unmount']>`
- [ ] Create `app/App.vue` — minimal wrapper, just `<RouterView />`, no extra nav or header (the `portal-header` custom element fills the header slot)
- [ ] Create `views/SetupView.vue` — the registration page (see UI requirements below)
- [ ] Create `styles.css` — import if any scoped styles are needed

### 2 — SetupView.vue — UI (use WebAwesome as much as possible)

- [ ] **Welcome section** (above the frame):
  - `"Welcome"` rendered as a distinct heading (e.g. `<p>` or `<span>`) that is visually larger and on its own line
  - Below it, a smaller paragraph with the following message (may be lightly rephrased for natural flow, but must keep the core intent):
    > *"We're pleased you found your way to this project. It's a public demo project aiming to demonstrate an idea of how a micro-frontend based multi-app web portal could be set up. Please fill the form to set up this portal initially."*
- [ ] **Frame / card** wrapping the form (use `wa-card` or equivalent WA container):
  - Card/frame **heading**: `"Portal Setup"`
- [ ] **Form fields** inside the frame:
  - `title` — `wa-input`, label "Title", **required**, no default
  - `basePath` — `wa-input`, label "Base Path", **required**, default value `"/"`
  - `appsUrl` — **hidden** from the user (do NOT render a visible input), value fixed to `"apps.json"`, included in submit payload with key `"appsUrl"`
  - `sourceUrls` — **hidden** from the user, value fixed to `"sources.json"`, included in submit payload with key `"sourceUrls"`
- [ ] **Submit button** — `wa-button` with variant `brand` (or equivalent primary variant), type attribute `"submit"`, label e.g. `"Set Up Portal"`
- [ ] **WA input binding**: because `wa-input` fires custom events, bind values using `:value="field"` + `@wa-input="field = ($event.target as WaInput).value"` (or read `.value` on the element ref at submit time)
- [ ] **On submit**:
  1. Prevent default form submission
  2. POST `{ key: 'title', value: title }` to `/api/host/config` via `axiosInstance`
  3. POST `{ key: 'basePath', value: basePath }` to `/api/host/config` via `axiosInstance`
  4. POST `{ key: 'appsUrl', value: 'apps.json' }` to `/api/host/config` via `axiosInstance`
  5. POST `{ key: 'sourceUrls', value: 'sources.json' }` to `/api/host/config` via `axiosInstance`
  6. Call `window.location.reload()` to trigger a full page reload

### 3 — `src/host/main.ts` — Bootstrap Flow

- [ ] Before calling `HostService.bootstrap(...)`, call `GET /api/host/initialized` via `axiosInstance`
- [ ] If `initialized === false` → call `HostService.bootstrap('/host.json')` (static file, existing default behaviour)
- [ ] If `initialized === true` → call `HostService.bootstrap('/api/host')` (loads config from the database via the backend API)
- [ ] The `/api/host` route returns `HostResponse` which is compatible with `HostData` (`title`, `basePath`, `appsUrl`, `sourceUrls`) — `HostService.bootstrap(url)` does a generic `axiosInstance.get<HostData>(url)` so it works as-is

### 4 — `public/host.json` — Update Static Config

Update to point at `apps_setup.json` so the uninitialized flow shows only the setup app and the header:

```json
{
    "title": "portfolio",
    "basePath": "/",
    "appsUrl": "/apps_setup.json",
    "sourceUrls": ["/sources.json"]
}
```

### 5 — `public/apps_setup.json` — New Static Apps Manifest

Create this file next to `apps.json`. Include **only** the header and the setup app:

```json
[
    {
        "displayName": "Header",
        "entry": "",
        "scope": "portal",
        "module": "PortalHeader",
        "description": "",
        "slot": "header",
        "activationUrl": "/"
    },
    {
        "displayName": "Portal Setup",
        "entry": "",
        "scope": "portal",
        "module": "SetupApp",
        "description": "",
        "slot": "app",
        "activationUrl": "/"
    }
]
```

### 6 — `vite.config.mts` — Expose the Setup App

- [ ] Add `'./SetupApp': './src/apps/setup/Module.ts'` to the `federation.exposes` object

## Technical Specifications

- **Framework**: Vue 3 with Composition API (`<script setup lang="ts">`)
- **HTTP client**: `axiosInstance` from `@portfolio/core` (already configured with base URL, no extra setup needed)
- **Types**: `ModuleLifecycle` from `@portfolio/core`; `HostData` / `AppData` from `@portfolio/core` if needed in the form
- **Routing**: `vue-router` v4 with `createWebHistory`, same pattern as existing apps
- **Styles**: use WebAwesome design tokens (`--wa-*`) and utility classes; keep scoped CSS minimal
- **WA input value extraction**: WA inputs expose a `.value` property on the custom element instance (same as native `<input>`); cast to `HTMLInputElement` or use `(el as any).value` in `<script setup>`
- **Module Federation**: this is a `singleton: true` shared-module environment — do not re-import `vue` or `vue-router` beyond what Vite's MF plugin already shares

## Constraints

- Do **not** add navigation links inside `App.vue` for the setup app — the portal header (loaded via the header slot) provides that
- The hidden fields (`appsUrl`, `sourceUrls`) must **not** be rendered visibly; they are implementation details, not user-facing
- `basePath` must default to `"/"` pre-filled in the rendered input so the user can submit without typing a value
- Only `title` and `basePath` are required by `isInitialized()` on the backend; the other two are bonus props sent along to complete the initial config
- The `apps_setup.json` must **not** include `DefaultApp` or `TestApp` — those are irrelevant in the setup flow
- After `window.location.reload()` the `initialized` endpoint will return `true` (both props are now in DB), and `HostService.bootstrap('/api/host')` will load the real config from the database

## Success Criteria

- [ ] Navigating to the portal when the DB is empty (uninitialized) renders the `SetupView` with welcome text and the registration form
- [ ] Submitting the form with a valid title POSTs all four key-value pairs to `/api/host/config` via `axiosInstance`
- [ ] The page reloads automatically after a successful submit
- [ ] After reload, `GET /api/host/initialized` returns `{ initialized: true }` and the host bootstraps from `/api/host`
- [ ] `GET /api/host/initialized` returning `true` causes `main.ts` to call `HostService.bootstrap('/api/host')` instead of `/host.json`
- [ ] `SetupApp` is resolvable as a Module Federation remote (`portal/SetupApp`)
- [ ] The form uses `wa-input`, `wa-button`, and a `wa-card` (or equivalent WA frame/container)
- [ ] "Welcome" is visually distinct (larger font, own line) from the descriptive paragraph beneath it
- [ ] `basePath` input pre-fills with `"/"`
- [ ] `appsUrl` and `sourceUrls` are not visible to the user but are sent on submit

## Examples

### `Module.ts` skeleton (mirrors `src/apps/default/Module.ts`)

```typescript
import { createApp, type App } from 'vue';
import { createRouter, createWebHistory } from 'vue-router';
import type { ModuleLifecycle } from '@portfolio/core';
import AppComponent from './app/App.vue';
import SetupView from './views/SetupView.vue';

let _app: App | null = null;

function createRouterInstance(basename: string) {
    return createRouter({
        history: createWebHistory(basename),
        routes: [
            { path: '/', name: 'setup', component: SetupView },
        ],
    });
}

export const mount: ModuleLifecycle['mount'] = (container, basename) => {
    _app = createApp(AppComponent);
    _app.use(createRouterInstance(basename));
    _app.mount(container);
};

export const unmount: NonNullable<ModuleLifecycle['unmount']> = () => {
    _app?.unmount();
    _app = null;
};
```

### `main.ts` bootstrap (updated)

```typescript
import '@awesome.me/webawesome/dist/styles/webawesome.css';
import '../styles/portal.css';
import { axiosInstance } from '@portfolio/core';
import { HostService } from '@portfolio/host';

const { data } = await axiosInstance.get<{ initialized: boolean }>('/api/host/initialized');
await HostService.bootstrap(data.initialized ? '/api/host' : '/host.json');
```

### API call on submit

```typescript
import { axiosInstance } from '@portfolio/core';

const onSubmit = async () => {
    await axiosInstance.post('/api/host/config', { key: 'title', value: title.value });
    await axiosInstance.post('/api/host/config', { key: 'basePath', value: basePath.value });
    await axiosInstance.post('/api/host/config', { key: 'appsUrl', value: 'apps.json' });
    await axiosInstance.post('/api/host/config', { key: 'sourceUrls', value: 'sources.json' });
    window.location.reload();
};
```

## Additional Notes

- **WA event handling**: `wa-input` fires a `wa-input` CustomEvent (not `input`). In Vue templates: `@wa-input="title = ($event.target as HTMLInputElement).value"`. Alternatively, attach a `ref` to the element and read `.value` only at submit time (simpler, avoids continuous reactivity).
- **`sourceUrls` key**: the backend `PortalHost.applyProps()` reads the key `"sourceUrls"` (plural) and splits by comma. The value `"sources.json"` (without leading `/`) is intentional — the frontend `HostService` prefixes with the path itself, and the existing `apps.json` uses no leading slash either. Keep consistent with whatever the `HostResponse` returns for `sourceUrls` when fetched from the API.
- **No `sources.json` is needed for the setup flow**: `apps_setup.json` has no apps that need remote sources, so `sourceUrls: ["/sources.json"]` in `host.json` is a forward-looking placeholder.
- **`wa-card` heading slot**: most WA card/frame components accept a `slot="header"` child for the title — check the WA docs for the exact slot name (`header`, `label`, or a prop).
- **Reload triggers re-evaluation**: after `window.location.reload()`, `main.ts` runs again from the top, hits the `initialized` check, gets `true`, and calls `HostService.bootstrap('/api/host')` — loading the DB-stored config including whatever `appsUrl` was saved (which would be `"apps.json"`, the full app manifest).
