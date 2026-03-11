---
agent: agent
---

# Add "Certificates & References" Tab to the CV Frontend

## Context

- The CV frontend is a **Vue 3** micro-frontend using **Vite**, **Vue Router**, and **WebAwesome** (`@awesome.me/webawesome`) web components for UI.
- The app entry point is `Module.ts`, which registers routes and mounts `App.vue`.
- `App.vue` renders a two-column layout: a fixed `<aside>` sidebar (`GeneralInfoPanel.vue`) and a `<main>` area with tab navigation (`<nav class="cv-tabs">`) and a `<RouterView>`.
- Existing tabs: **Experience** (`ExperienceView.vue`), **Education** (`EducationView.vue`), **Skills & Competences** (`SkillsView.vue`).
- All views follow the same structure:
  - `wa-spinner` while loading, `wa-callout` on error, data display otherwise.
  - `inject<AxiosInstance>('cvApi')` for HTTP calls.
  - All WebAwesome components are individually imported from `@awesome.me/webawesome/dist/components/...`.
  - `await allDefined()` is called before the component logic.
- Shared CSS classes (defined in `styles.css`): `.cv-timeline`, `.cv-timeline-card`, `.cv-card-header`, `.cv-card-title`, `.cv-card-subtitle`, `.cv-card-body`, `.cv-date-location`, `.cv-date-range`, `.cv-location`, `.cv-card-footer`, `.cv-section-heading`.
- TypeScript interfaces for all API responses live in `types.ts`.
- The backend exposes:
  - `GET /api/cv/references` → `ReferenceResponse[]`
  - `GET /api/cv/certificates` → `CertificateResponse[]`

## Objective

Create a new **"Certificates & References"** tab that displays certificates as a timeline (matching the Experience/Education style) and references as a card grid, wiring both into the existing router, tab nav, and shared CSS system.

## Requirements

### `types.ts`
- [ ] Add `ReferenceResponse` interface:
  ```ts
  export interface ReferenceResponse {
      id: string;
      firstName: string;
      lastName: string;
      description: string;
      relation: 'COWORKER' | 'MANAGER' | null;
  }
  ```
- [ ] Add `CertificateResponse` interface:
  ```ts
  export interface CertificateResponse {
      id: string;
      title: string;
      issuingOrganization: string;
      startDate: string;       // "yyyy-MM-dd"
      endDate: string | null;
      location: string | null;
      description: string | null;
  }
  ```

### `CertificatesAndReferencesView.vue` (new file in `views/`)
- [ ] Fetch both `/api/cv/certificates` and `/api/cv/references` in parallel using `Promise.all`.
- [ ] Show a single `wa-spinner` while loading and a single `wa-callout[variant="danger"]` on error.
- [ ] **Certificates section** — render as a timeline using existing `.cv-timeline` / `.cv-timeline-card` classes:
  - `cv-card-title`: certificate `title`
  - `cv-card-subtitle`: `issuingOrganization`
  - `cv-date-range`: formatted date range (`startDate` – `endDate` or `"present"`)
  - `cv-location` (with a `briefcase` icon): `location` if present
  - `cv-card-footer`: `description` if present
  - Sort by `startDate` descending (most recent first)
- [ ] **References section** — render as a card grid (new layout class `.cv-references-grid`):
  - Each card shows the full name (`firstName lastName`) as the title.
  - Show the `relation` as a `wa-badge` when non-null — use `appearance="filled"` for `MANAGER`, `appearance="outlined"` for `COWORKER`.
  - Show `description` as body text.
- [ ] Use a `cv-section-heading` `<h2>` before each section ("Certificates" and "References").
- [ ] Import WebAwesome components exactly as in other views (individual JS imports + `allDefined()`).

### `styles.css`
- [ ] Add `.cv-certificates-references-view` wrapper class (flex column, `gap: var(--wa-space-xl)`).
- [ ] Add `.cv-references-grid` — a responsive CSS grid:
  - Default: `grid-template-columns: repeat(auto-fill, minmax(280px, 1fr))`
  - `gap: var(--wa-space-m)`
- [ ] Add `.cv-reference-card` — inherits the left-border style matching `.cv-timeline-card` (`border-left: 3px solid var(--wa-color-brand-border-normal)`).
- [ ] Add `.cv-reference-card-header` — flex row, `justify-content: space-between`, `align-items: flex-start`, `gap: var(--wa-space-xs)`.
- [ ] Add `.cv-reference-name` — heading style matching `.cv-card-title` (`font-family: var(--wa-font-family-heading)`, `font-weight: bold`, `font-size: var(--wa-font-size-l)`).
- [ ] Add `.cv-reference-description` — matching `.cv-card-footer` (`color: var(--wa-color-text-quiet)`, `font-size: var(--wa-font-size-s)`, `line-height: var(--wa-line-height-normal)`).

### `Module.ts`
- [ ] Import `CertificatesAndReferencesView` from `./views/CertificatesAndReferencesView.vue`.
- [ ] Add route `{ path: '/certificates-references', name: 'certificates-references', component: CertificatesAndReferencesView }`.

### `App.vue`
- [ ] Add a `<RouterLink to="/certificates-references">Certificates & References</RouterLink>` to the `cv-tabs` nav, after the existing "Skills & Competences" link.

## Technical Specifications

- **Framework**: Vue 3 with `<script setup lang="ts">` Composition API
- **UI library**: WebAwesome (`@awesome.me/webawesome`) — use `wa-card`, `wa-spinner`, `wa-callout`, `wa-icon`, `wa-badge`
- **Routing**: Vue Router (`RouterLink`, `RouterView`) — existing `createWebHistory` setup
- **HTTP**: Axios injected as `inject<AxiosInstance>('cvApi')` — no direct import
- **Styling**: Plain CSS in `styles.css` using existing WebAwesome CSS custom properties (`--wa-*`)
- **Import pattern**: Each WebAwesome component imported individually, e.g.:
  ```ts
  import '@awesome.me/webawesome/dist/components/card/card.js';
  import '@awesome.me/webawesome/dist/components/badge/badge.js';
  ```
- **`allDefined()`**: Must be awaited at the top of `<script setup>` before any component logic, consistent with other views

## Constraints

- Do **not** modify `GeneralInfoPanel.vue` or any existing view.
- Do **not** introduce new dependencies — use only what is already in `package.json`.
- The new CSS classes must **not** conflict with existing class names in `styles.css`.
- The tab label must be exactly `"Certificates & References"` to match the route name and design intent.
- Dates should be formatted using `Intl.DateTimeFormat` in `{ year: 'numeric', month: 'short' }` format, consistent with `ExperienceView.vue` and `EducationView.vue`.

## Success Criteria

- [ ] A fourth tab labeled **"Certificates & References"** appears in the tab nav and is visually consistent with the three existing tabs (font, spacing, active underline).
- [ ] Clicking the tab navigates to `/certificates-references` and renders both sections.
- [ ] The **Certificates** section renders cards with the same left-border timeline style as Experience/Education cards.
- [ ] Certificates are sorted by `startDate` descending.
- [ ] The **References** section renders a responsive grid; each card shows the name, a relation badge (or no badge if `null`), and description.
- [ ] A `MANAGER` relation badge uses `appearance="filled"`, a `COWORKER` badge uses `appearance="outlined"`.
- [ ] A single loading spinner covers both sections while data is fetching.
- [ ] A single error callout is shown if either fetch fails.
- [ ] The view compiles without TypeScript errors.
- [ ] Deep-linking directly to `/certificates-references` works correctly.

## Examples

### Certificate card structure (template excerpt)
```html
<wa-card class="cv-timeline-card">
  <div slot="header" class="cv-card-header">
    <div class="cv-card-title">AWS Certified Developer – Associate</div>
    <div class="cv-card-subtitle">Amazon Web Services</div>
  </div>
  <div class="cv-card-body">
    <div class="cv-date-location">
      <span class="cv-date-range">Apr 2023 – Apr 2026</span>
    </div>
  </div>
  <div slot="footer" class="cv-card-footer">
    Validates proficiency in developing applications on the AWS platform.
  </div>
</wa-card>
```

### Reference card structure (template excerpt)
```html
<wa-card class="cv-reference-card">
  <div class="cv-reference-card-header">
    <span class="cv-reference-name">Anna Müller</span>
    <wa-badge appearance="filled">Manager</wa-badge>
  </div>
  <p class="cv-reference-description">
    Marcel consistently delivered high-quality features...
  </p>
</wa-card>
```

### Relation badge label mapping
```ts
const RELATION_LABELS: Record<string, string> = {
  MANAGER: 'Manager',
  COWORKER: 'Coworker',
};
const RELATION_APPEARANCE: Record<string, string> = {
  MANAGER: 'filled',
  COWORKER: 'outlined',
};
```

## Additional Notes

- The `relation` field on `ReferenceResponse` is **nullable** — only render the badge when `relation !== null`.
- `CertificateResponse.endDate` is nullable (non-expiring certificates); render `"present"` in those cases, consistent with `ExperienceView.vue`.
- The view file name must be **`CertificatesAndReferencesView.vue`** so that it is consistent with the existing `PascalCase` naming convention.
- The route path must be **`/certificates-references`** (kebab-case) consistent with existing routes (`/experience`, `/education`, `/skills`).
- WebAwesome `wa-card` uses named slots (`header`, `footer`) — pass content via `slot="header"` and `slot="footer"` attributes on child elements, as done in `ExperienceView.vue`.
