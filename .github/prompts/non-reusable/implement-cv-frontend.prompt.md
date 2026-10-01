---
agent: agent
---

# Implement CV Frontend — Vue Micro-Frontend Module

## Context

- **Project**: `apps/cv/src/main/resources/static/` — Vue 3 + TypeScript micro-frontend, Module Federation remote, served by the Spring Boot CV service on port `8081`
- **Vite dev port**: `4203`
- **Backend API base**: `http://localhost:8081/api/cv/` (or relative `/api/cv/` in production); all endpoints respond with JSON
- **Theme system**: `apps/portal/src/main/resources/static/src/styles/portal.css` — defines the `.wa-theme-portal` class with full Web Awesome CSS custom property overrides (colors, fonts, spacing, radii, shadows). This class is already applied to the host document by the portal; the CV module inherits it automatically and must **not** re-apply it on its own root element.
- **Fonts** (loaded via `portal.css`): `Inter` (body), `Space Grotesk` (headings), `JetBrains Mono` (code) — reference via `--wa-font-family-body`, `--wa-font-family-heading`, etc.
- **HTTP client**: `createAxiosInstance` from `@portfolio/core` (`libs/browser/core/src/api/axiosInstance.ts`) — the shared `axiosInstance` has no `baseURL` and resolves relative to the current origin (the portal, port `8080`). The CV module must create its own instance: `createAxiosInstance({ baseURL: new URL(import.meta.url).origin })`. `import.meta.url` resolves to the URL of the CV chunk itself, which is served from the CV backend — giving the correct origin at runtime. This breaks if assets are ever served from a CDN at a different origin than the API.
- **Component library**: `@awesome.me/webawesome` — custom elements imported **per component**, never globally. Each SFC imports only what it uses:
  ```typescript
  import '@awesome.me/webawesome/dist/styles/webawesome.css';
  import '@awesome.me/webawesome/dist/components/card/card.js';
  import '@awesome.me/webawesome/dist/components/spinner/spinner.js';
  ```
  After the last import, call `await allDefined()` (from `@awesome.me/webawesome/dist/utilities/defined.js`) at the top level of `<script setup>` to ensure all registered elements are ready before rendering. Reference `SetupView.vue` in `apps/portal/` as the established pattern.
- **Existing scaffold** (files to replace/extend):
  - `src/apps/cv/Module.ts` — currently wires a single `{ path: '/', component: CvView }` route
  - `src/apps/cv/app/App.vue` — currently just `<RouterView />`
  - `src/apps/cv/views/CvView.vue` — placeholder `<h1>CV</h1>`
  - `src/apps/cv/styles.css` — empty (`/* CV app styles */`)
- **Reference pattern**: `apps/portal/` for portal header component style (custom elements, CSS parts), and `implement-cv-service.prompt.md` for API shape and module lifecycle pattern

---

## Objective

Replace the placeholder CV frontend with a polished, professional CV viewer — a persistent `GeneralInfoPanel` always visible on the left/top, and a tabbed area on the right/below with three route-driven views: Professional Experience, Education, and Skills & Competences. Use only Web Awesome components and CSS custom properties; write zero custom color or typography CSS.

---

## Requirements

### 1 — Module wiring (`Module.ts`)

- [ ] Create a CV-specific axios instance at module scope and provide it to the Vue app:
  ```typescript
  import { createAxiosInstance } from '@portfolio/core';
  const cvApi = createAxiosInstance({ baseURL: new URL(import.meta.url).origin });
  // inside mount(): app.provide('cvApi', cvApi)
  ```
- [ ] Add three routes in addition to (or replacing) the placeholder:
  - `{ path: '/', redirect: '/experience' }`
  - `{ path: '/experience', name: 'experience', component: ExperienceView }`
  - `{ path: '/education', name: 'education', component: EducationView }`
  - `{ path: '/skills', name: 'skills', component: SkillsView }`
- [ ] Keep the `mount` / `unmount` `ModuleLifecycle` pattern exactly as the scaffold has it (create router, create app, mount to container)

### 2 — Root layout (`App.vue`)

- [ ] **Left/top panel** — always renders `<GeneralInfoPanel />` (new component, see §5)
- [ ] **Right/main area** — renders a `<wa-tab-group>` containing:
  - Three `<wa-tab slot="nav">` elements: `"Experience"`, `"Education"`, `"Skills & Competences"` — each with a `panel` attribute matching a `<wa-tab-panel>` name
  - Inside each `<wa-tab-panel>`, render `<RouterView />` (only the active panel's slot is shown)
  - On mount: set the active tab based on `route.name` (map route name → tab panel name)
  - On `@wa-tab-show` event: call `router.push({ name: tabToRoute[e.detail.name] })` to keep router and tabs in sync
- [ ] **Layout**: CSS grid — sidebar column (`~300px` fixed or `25%`) + main column (`1fr`); collapse to single column below `768px`. Use `--wa-space-*` tokens for gaps and padding. No hardcoded pixel colors.

### 3 — `ExperienceView.vue` (`views/`)

- [ ] Injects `cvApi` (`inject('cvApi')`) and fetches `GET /api/cv/experiences` on mount; shows `<wa-spinner>` while loading, `<wa-callout variant="danger">` on error
- [ ] Renders entries sorted by `startDate` **descending** (most recent first)
- [ ] Each entry: a `<wa-card>` with:
  - **Header slot**: job title (`--wa-font-family-heading`, `font-weight: bold`) + company name as subtitle (`color: var(--wa-color-text-quiet)`)
  - **Body**: date range line — format dates with `Intl.DateTimeFormat('default', { year: 'numeric', month: 'short' })`, show `"present"` when `endDate` is `null`; and location (if set) with a `<wa-icon name="location-pin">` prefix
  - **Footer slot** (if `description` is set): description text in `--wa-color-text-quiet`, `font-size: var(--wa-font-size-s)`
- [ ] Timeline visual: a left border accent (`border-left: 3px solid var(--wa-color-brand-border-normal)`) on each card, with a vertical gap between cards

### 4 — `EducationView.vue` (`views/`)

- [ ] Same structure as `ExperienceView` but fetches `GET /api/cv/education` via the injected `cvApi`
- [ ] Shows `schoolName` where `ExperienceView` shows `companyName`; uses `<wa-icon name="graduation-cap">` or `"book"` for the location line

### 5 — `SkillsView.vue` (`views/`)

- [ ] Two sections rendered side by side (CSS grid `1fr 1fr`, stacked below `600px`):

  **Skills section** — fetches `GET /api/cv/skills` via the injected `cvApi`:
  - Section heading "Skills" in `--wa-font-family-heading`
  - Each skill: skill name on the left, a visual level bar on the right — render 10 `<wa-icon>` elements: filled `circle` for levels ≤ `skill.level`, outlined `circle` for the rest (use `name="circle"` vs `name="circle"` with appropriate variant, or use `<wa-progress-bar value="...">` set to `skill.level * 10`)
  - Rows separated with `gap: var(--wa-space-xs)`

  **Competences section** — fetches `GET /api/cv/competences` via the injected `cvApi`:
  - Section heading "Competences" in `--wa-font-family-heading`
  - Each competence rendered as a `<wa-badge appearance="outlined">` or `<wa-tag>` with its `description` text
  - Tags wrap in a flex row with `flex-wrap: wrap; gap: var(--wa-space-xs)`

### 6 — `GeneralInfoPanel.vue` (`components/`)

- [ ] Injects `cvApi` and fetches `GET /api/cv/general-info`, `GET /api/cv/hobbies`, and `GET /api/cv/languages` in parallel (`Promise.all`) on mount; shows `<wa-spinner>` while any is loading
- [ ] **Layout**: CSS grid — avatar column (`120px`) + info column (`1fr`); below `480px` stack vertically and center-align the avatar

  **Avatar**:
  - If `generalInfo.imageUrl` is set: `<img>` with `border-radius: var(--wa-border-radius-circle)`, `width: 100px`, `height: 100px`, `object-fit: cover`
  - Otherwise: a styled `<div>` showing the person's initials (first letter of `firstName` + first letter of `lastName`), `background: var(--wa-color-brand-fill-normal)`, `color: var(--wa-color-brand-on-normal)`, `border-radius: var(--wa-border-radius-circle)`, `font-size: var(--wa-font-size-2xl)`, `font-family: var(--wa-font-family-heading)`

  **Info column**:
  - Full name as `<h1>` using `--wa-font-family-heading`, `font-size: var(--wa-font-size-2xl)`
  - One-liner: nationality + date of birth formatted as `dd Month yyyy` using `Intl.DateTimeFormat`
  - One-liner: marital status (human-readable, e.g. `"Single"` from `"SINGLE"`) + number of children (if `> 0`: `"· 2 children"`)
  - Summary as `<p>` with `--wa-line-height-normal`, `color: var(--wa-color-text-quiet)`, `font-size: var(--wa-font-size-s)`

  **Languages row** (below info):
  - Label "Languages:" in semibold + each language as `name` followed by a dot/star rating: render `level` filled `<wa-icon name="star">` and `(10 - level)` outlined `<wa-icon name="star">` (or use a simpler compact format)
  - Separated by `|` or rendered as a flex row with `gap: var(--wa-space-s)`

  **Hobbies row** (below languages):
  - Label "Hobbies:" in semibold + each hobby as a `<wa-tag size="small">` chip in a flex wrap row

### 7 — `styles.css` (CV app styles)

- [ ] Only layout rules that cannot come from WA tokens alone — for example:
  - `.cv-layout` grid definition for the two-column App.vue shell
  - `.cv-timeline-card` for the left-border timeline accent
  - `.cv-avatar` for the circular image/initials div
- [ ] **Zero hardcoded color values** — all color references must use `var(--wa-color-*)` tokens
- [ ] **Zero hardcoded font families or sizes** — use `var(--wa-font-family-*)` and `var(--wa-font-size-*)` tokens

---

## Technical Specifications

- **Language**: TypeScript 5, Vue 3 SFC `<script setup lang="ts">`
- **Component library**: `@awesome.me/webawesome` custom elements — prefix `wa-`; listen to custom events with `@wa-*` Vue event syntax; import per-component (see Context)
- **HTTP**: injected `cvApi` — a `createAxiosInstance({ baseURL: new URL(import.meta.url).origin })` instance created in `Module.ts`, provided via `app.provide('cvApi', cvApi)`, injected in components with `const cvApi = inject<AxiosInstance>('cvApi')!`. Response types via inline `interface` definitions matching the OpenAPI schemas (no codegen needed on frontend side)
- **Date formatting**: `Intl.DateTimeFormat` only — no date library
- **Routing**: `vue-router` `createWebHistory(basename)` — already set up in Module.ts scaffold
- **No `v-model` on WA inputs** — use `:value` + `@wa-input` / `@wa-change`
- **API response shapes** (define these as TypeScript interfaces in a `types.ts` file in `src/apps/cv/`):

```typescript
interface GeneralInfoResponse {
  id: string;
  firstName: string;
  lastName: string;
  maritalStatus: 'SINGLE' | 'MARRIED' | 'DIVORCED' | 'WIDOWED' | 'SEPARATED' | null;
  numberOfChildren: number | null;
  dateOfBirth: string | null;   // ISO date: "yyyy-MM-dd"
  placeOfBirth: string | null;
  nationality: string | null;
  imageUrl: string | null;
  summary: string | null;
}

interface ExperienceResponse {
  id: string;
  title: string;
  companyName: string;
  startDate: string;     // "yyyy-MM-dd"
  endDate: string | null;
  location: string | null;
  description: string | null;
}

interface EducationResponse {
  id: string;
  title: string;
  schoolName: string;
  startDate: string;
  endDate: string | null;
  location: string | null;
  description: string | null;
}

interface SkillResponse   { id: string; title: string; level: number; }
interface CompetenceResponse { id: string; description: string; }
interface LanguageResponse { id: string; name: string; level: number; }
interface HobbyResponse    { id: string; name: string; }
```

---

## Constraints

- **No custom colors** — never write a hex or `rgb()` value in `styles.css` or inline styles; always use `var(--wa-color-*)` tokens
- **No extra npm packages** — do not install a date library, a chart library, or a UI framework beyond what's already in `package.json`
- **No Vue UI component libraries** (Vuetify, PrimeVue, etc.) — Web Awesome only
- **Reactive**: use `ref`/`computed` from Vue Composition API; no Options API
- **No SSR concerns** — this is a client-side-only module federation remote
- **Do not add `wa-theme-portal` to `App.vue`** — it is already applied to the host document by the portal; double-nesting the class causes incorrect CSS variable scoping

---

## Success Criteria

- [ ] `.\gradlew :apps:cv:bootRun` starts on port `8081` and `GET /api/cv/general-info` returns Jane Doe's data
- [ ] `pnpm run dev` starts the Vite dev server on port `4203`; `http://localhost:4203/remoteEntry.js` is served
- [ ] Navigating to the CV app (via portal or direct iframe) shows `GeneralInfoPanel` with name, avatar placeholder (initials), summary, languages, and hobby tags
- [ ] Three tabs ("Experience", "Education", "Skills & Competences") are visible and clickable; clicking each updates the URL and renders the correct view
- [ ] Experience and Education views show at least one card from the demo data, with correctly formatted date ranges (`"Jan 2020 – present"`)
- [ ] Skills view shows level indicators (filled/empty icons or progress bar) for each skill; Competences section shows badge/tag chips
- [ ] Refreshing the page with `/experience`, `/education`, or `/skills` in the URL lands on the correct tab (deep-link works)
- [ ] No hardcoded hex colors or pixel font sizes appear in `styles.css` or `<style>` blocks
- [ ] `<wa-spinner>` appears briefly on initial data load; no perpetual spinner after data arrives
- [ ] `<wa-callout variant="danger">` appears when the backend is unreachable (e.g. stop the Spring Boot server and reload)
- [ ] Layout is readable at `1280px` (two-column) and `375px` (single-column, stacked)

---

## Examples

### Marital status display helper
```typescript
const MARITAL_STATUS_LABELS: Record<string, string> = {
  SINGLE: 'Single',
  MARRIED: 'Married',
  DIVORCED: 'Divorced',
  WIDOWED: 'Widowed',
  SEPARATED: 'Separated',
};
const displayMaritalStatus = (status: string | null) =>
  status ? (MARITAL_STATUS_LABELS[status] ?? status) : null;
```

### Date formatting
```typescript
const formatMonthYear = (iso: string) =>
  new Intl.DateTimeFormat('en', { year: 'numeric', month: 'short' }).format(new Date(iso));

const formatDateRange = (start: string, end: string | null) =>
  `${formatMonthYear(start)} – ${end ? formatMonthYear(end) : 'present'}`;
```

### Tab ↔ route sync (App.vue)
```vue
<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router';
import { watch, ref } from 'vue';

const router = useRouter();
const route  = useRoute();

const TAB_TO_ROUTE: Record<string, string> = {
  experience: 'experience',
  education:  'education',
  skills:     'skills',
};
const ROUTE_TO_TAB: Record<string, string> = {
  experience: 'experience',
  education:  'education',
  skills:     'skills',
};

const tabGroupRef = ref<HTMLElement | null>(null);

// Keep tab active when route changes (back/forward, deep-link)
watch(() => route.name, (name) => {
  const tab = ROUTE_TO_TAB[name as string];
  if (tab && tabGroupRef.value) {
    (tabGroupRef.value as any).show(tab);
  }
}, { immediate: true });

// Navigate when user clicks a tab
const onTabShow = (e: CustomEvent) => {
  const routeName = TAB_TO_ROUTE[e.detail.name];
  if (routeName && route.name !== routeName) {
    router.push({ name: routeName });
  }
};
</script>
```

---

## Additional Notes

- **`wa-tab-group` quirk**: `show(panelName)` is a method on the element, not a prop — you need a template ref (`ref="tabGroupRef"`) and call it imperatively. The `wa-tab-show` event fires with `event.detail.name` equal to the `panel` attribute of the activated `<wa-tab>`.
- **WA custom elements in Vue**: TypeScript may not know the element's properties; cast to `any` or use `declare global { interface HTMLElementTagNameMap { 'wa-tab-group': ...; } }` in a `vue-shims.d.ts` — following the pattern in `apps/portal/src/main/resources/static/src/vue-shims.d.ts`
- **`cvApi` origin resolution**: `new URL(import.meta.url).origin` evaluates at runtime to the origin of the CV chunk — `http://localhost:8081` in production (Spring Boot) and `http://localhost:4203` in dev (Vite). This works correctly for both because the chunk is always served from the CV host. It would break only if assets were moved to a CDN at a different origin than the API.
- **Vite proxy** — add to `vite.config.mts` if not already present, so `/api` calls made during dev reach the Spring Boot backend:
  ```ts
  server: {
    proxy: {
      '/api': 'http://localhost:8081',
    },
  }
  ```
- **Web Awesome per-component imports**: each SFC imports `webawesome.css` and only the specific component JS files it uses (e.g. `dist/components/card/card.js`). After imports, call `await allDefined()` from `@awesome.me/webawesome/dist/utilities/defined.js` at the top level of `<script setup>` to ensure custom elements are upgraded before the template renders. Do **not** import the full `webawesome.js` bundle anywhere.
- **`CvView.vue`**: can be deleted or repurposed — the three new view components replace it
- **`styles.css`**: is currently empty; add only structural layout rules here; import it in `Module.ts` (or it may already be imported — check)
- **WA icons**: verify available icon names at `https://webawesome.com/docs/icons/` — prefer icons in the `regular` or `solid` style; fallback gracefully if an icon is missing
- **Level indicator implementation**: the simplest approach is `<wa-progress-bar value="${level * 10}">` — it uses brand colors automatically in the portal theme; the dot/icon approach is more custom and requires more CSS
