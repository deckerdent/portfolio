---
agent: agent
---

# Implement `<portal-header>` Web Component with App Navigation Bar

## Context

- The host shell is a Vue + TypeScript application at `apps/portal/src/main/resources/static/`.
  Micro-frontends are loaded via Module Federation (`@module-federation/vite`).
- Routing is handled by **Navigo 8**. Navigation links must be `<portal-link>` custom elements
  (defined in `src/components/PortalLink.ts`, exposed via MF as `portal/PortalLink`), not plain
  `<a>` tags, because `RouterService` listens for `portal-link:connected` to call `router.updatePageLinks()`.
- The shared reactive store `PortalStore` lives in `@portfolio/core` and is available as an MF
  singleton. Its API:
  ```typescript
  import { PortalStore } from '@portfolio/core';

  // Reactive (RxJS Observable):
  PortalStore.apps$.subscribe(apps => { /* App[] */ });

  // Synchronous:
  const state = PortalStore.getInstance(); // → PortalState
  state.apps;    // App[]
  state.host;    // HostData | null
  state.sources; // SourceEntry[]
  ```
- `App` (from `@portfolio/core`) has these relevant properties:
  ```typescript
  app.displayName   // string — human-readable label for nav links
  app.activationUrl // string — the Navigo route to navigate to
  app.slot          // string — layout slot this app mounts into (e.g. "app", "sidebar")
  ```
- The theme is defined in `public/portal.css`. It uses the `.wa-theme-portal` class on the root
  element and exposes the full set of WA design tokens (`--wa-color-*`, `--wa-font-*`, `--wa-space-*`,
  `--wa-shadow-*`, `--wa-border-radius-*`, `--wa-transition-*`). **Do not modify this file.**
- The existing `PortalLink` component already uses `defineCustomElement` from Vue with Shadow DOM.
  `portal-header` should follow the same pattern for consistency.
- Component output location: `src/components/PortalHeader.ts`
- It must be exposed via Module Federation as `./PortalHeader` in `vite.config.mts`.

## Objective

Create a `<portal-header>` Vue custom element that renders a branded header bar (logo slot on the
left) with a `<nav>` below it. The nav subscribes to `PortalStore.apps$` and renders one
`<portal-link>` per `App` whose `slot === 'app'`. All internal structure must be targetable via
`::part()` so the host can override layout and style without touching the component source.

## Requirements

### Component Structure

- [ ] Tag name: `portal-header`
- [ ] Implemented with Vue's `defineCustomElement` in `src/components/PortalHeader.ts`
- [ ] Uses Shadow DOM (automatically applied by `defineCustomElement`)
- [ ] Registered via `customElements.define('portal-header', PortalHeader)`
- [ ] Exported as named export `PortalHeader`
- [ ] Exposed in `vite.config.mts` federation `exposes` block as `'./PortalHeader': './src/components/PortalHeader.ts'`
- [ ] `isCustomElement` guard in Vite's Vue compiler options already covers `portal-` prefix — no extra config needed

### Header Region

- [ ] The header bar is rendered as a `<header>` element with `part="header"`
- [ ] Contains a logo area on the left — a `<slot name="logo">` wrapped in a `<div part="logo">`;
  when the slot is empty a visually distinct placeholder is shown (e.g. initials "P" in a rounded square)
- [ ] No other content in the header bar by default — it is intentionally minimal
- [ ] The header uses `--wa-color-surface-raised` as background and `--wa-color-surface-border` for
  its bottom border, consuming tokens from the active portal theme

### Navigation Bar

- [ ] Rendered as a `<nav>` element immediately below the header with `part="nav"`
- [ ] Subscribes to `PortalStore.apps$` using `onMounted` + RxJS `subscribe`; unsubscribes
  `onUnmounted` (use `Subscription` from `rxjs`)
- [ ] Filters `apps` to only those where `app.slot === 'app'`
- [ ] Renders one `<portal-link>` per filtered app:
  - `href` set to `app.activationUrl`
  - Inner text set to `app.displayName`
  - `part` forwarded as `nav-link` on each `<portal-link>` so the host can do
    `portal-header::part(nav-link) { }` — note: `::part()` does not pierce Shadow DOM of nested
    custom elements, so this applies to the `<portal-link>` element itself, not its inner anchor
- [ ] When `apps$` emits an empty array (before bootstrap completes), the nav renders nothing
  (no skeleton, no spinner — simplicity first)
- [ ] Nav uses `--wa-color-surface-default` as background and `--wa-color-surface-border` for
  its bottom border

### Styling Rules

- [ ] **Do not import or reference `portal.css`** — consume only WA design tokens via CSS custom
  properties already available through the theme on the host document
- [ ] All layout and colour values must use `var(--wa-*)` tokens; no hardcoded colour or size values
- [ ] Component-level CSS custom properties (prefixed `--portal-header-*`) may be defined with
  `var(--wa-*)` fallbacks to allow targeted overrides without `::part()`:
  ```css
  :host {
      --portal-header-bg: var(--wa-color-surface-raised);
      --portal-header-border: var(--wa-color-surface-border);
  }
  header[part="header"] {
      background: var(--portal-header-bg);
      border-bottom: var(--wa-border-width-s) var(--wa-border-style) var(--portal-header-border);
  }
  ```
- [ ] All interactive elements (logo placeholder, nav links) show a focus ring using
  `var(--wa-focus-ring)` and `var(--wa-focus-ring-offset)`
- [ ] Transitions on hover/focus use `var(--wa-transition-normal)` and `var(--wa-transition-easing)`
- [ ] Font family inherits from the host (`:host { font-family: inherit; }`)

### Part Surface Area (for host `::part()` overrides)

| Element | `part` value |
|---|---|
| `<header>` wrapper | `header` |
| Logo container `<div>` | `logo` |
| Logo placeholder (when no slot content) | `logo-placeholder` |
| `<nav>` wrapper | `nav` |
| Each `<portal-link>` in the nav | `nav-link` |

### Module Federation

- [ ] `PortalLink` must be loaded via `loadRemote('portal/PortalLink')` inside the component
  file (module scope, fire-and-forget, plain `<script>` block pattern) before the component
  is rendered — same pattern used in `src/apps/test/app/App.vue` and `src/apps/default/app/App.vue`
- [ ] `PortalHeader` itself is exposed as `./PortalHeader` — consumers load it the same way

## Technical Specifications

- Language: TypeScript strict mode — no `any`, use `override` where applicable
- Framework: Vue 3 `defineCustomElement` (same pattern as `PortalLink.ts`)
- Reactive state: RxJS `Subscription` managed with `onMounted`/`onUnmounted` — **not** Vue `watchEffect`
  (RxJS is the source of truth, not Vue reactivity)
- Store import: `import { PortalStore } from '@portfolio/core'`
- Remote load: `import { loadRemote } from '@module-federation/enhanced/runtime'`
- Styling: Shadow DOM `styles` array inside `defineCustomElement` options

## Constraints

- Must not modify `portal.css` or any theme file
- Must not use `window`, `document.querySelector`, or any global DOM APIs outside lifecycle hooks
- `PortalStore` is a Module Federation singleton — only one instance exists across host + remotes;
  import it directly, do not pass it as a prop
- `portal-link` is loaded asynchronously via `loadRemote`; the module-scope call ensures it is
  defined before Vue renders the shadow tree

## Success Criteria

- [ ] `customElements.get('portal-header')` returns the class after the module is loaded
- [ ] The header bar renders with the logo placeholder when no `slot="logo"` content is projected
- [ ] Projecting `<img slot="logo" src="...">` into `<portal-header>` replaces the placeholder
- [ ] The nav renders one `<portal-link>` per `App` with `slot === 'app'` from `PortalStore`
- [ ] Nav updates reactively when `PortalStore.apps$` emits a new value (e.g. after bootstrap)
- [ ] `portal-header::part(header) { background: red; }` successfully overrides the header background
- [ ] `portal-header::part(nav) { padding: 0; }` successfully overrides nav padding
- [ ] `portal-header::part(nav-link) { color: green; }` targets the `<portal-link>` elements
- [ ] No TypeScript errors with `strict: true`
- [ ] Build passes (`pnpm exec vite build`) without errors or warnings related to this component

## Examples

### Usage in host HTML
```html
<portal-header>
  <img slot="logo" src="/assets/logo.svg" alt="Portal" />
</portal-header>
```

### Usage without logo (placeholder shown)
```html
<portal-header></portal-header>
```

### Host-level `::part()` override
```css
portal-header::part(header) {
    background: var(--wa-color-brand-fill-quiet);
    height: 4rem;
}

portal-header::part(nav) {
    padding-inline: var(--wa-space-xl);
}

portal-header::part(nav-link) {
    font-weight: var(--wa-font-weight-bold);
}
```

## Additional Notes

- `defineCustomElement` automatically wraps the component in a Shadow DOM — no need to call
  `attachShadow()` manually
- The `styles` array in `defineCustomElement` is injected into the shadow root; this is the correct
  place for all component CSS
- `PortalStore.apps$` starts with `[]` (empty `BehaviorSubject`) — the component must handle this
  gracefully without showing broken UI
- The `::part()` pseudo-element does **not** pierce nested Shadow DOMs: `portal-header::part(nav-link)`
  styles the `<portal-link>` host element, not its inner `<a part="anchor">`. To target the anchor,
  consumers must use `portal-link::part(anchor)` separately
- Keep the component file self-contained: one file, no sub-components, no external CSS imports
