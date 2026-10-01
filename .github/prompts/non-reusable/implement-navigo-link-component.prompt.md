---
agent: agent
---

# Implement `<portal-link>` Web Component in Host Frontend

## Context

- The host shell is a vanilla TypeScript application at `apps/portal/src/main/resources/static/src/host/`.
  It has no framework — it loads Vue micro-frontends via Module Federation.
- Routing is handled by **Navigo 8** (`navigo@8.11.1`). Navigo intercepts `<a>` elements that carry
  the `data-navigo` attribute; after adding such links to the DOM, `router.updatePageLinks()` must be
  called to make Navigo aware of them.
- Currently `src/host/` contains only: `main.ts`, `styles.css`, `types.d.ts`, `assets/`.
  There is no `components/` folder yet.
- Micro-frontends and other host modules that want to navigate the host router (e.g. triggering a slot
  swap) need a reusable, framework-agnostic way to emit a Navigo-aware link — without importing Navigo
  or knowing about `RouterService` directly.
- A `components/` folder already exists at `apps/portal/src/main/resources/static/src/components/`.
  It contains `HelloComponent.ts` which uses Vue's `defineCustomElement` pattern. The `PortalLink`
  component may follow the same `defineCustomElement` pattern **or** be a native `HTMLElement` subclass
  — either approach is acceptable. Prefer whichever keeps the implementation simpler.
- The existing coding style uses **`const` arrow functions** for free functions and **TypeScript strict
  mode** throughout. No `any` types.

Relevant Navigo link pattern already in use:
```html
<a href="/Default" data-navigo>Default App</a>
```

## Objective

Create a `PortalLink` custom element (`<portal-link>`) in `src/host/components/PortalLink.ts` that
renders a Navigo-aware anchor, accepts a relative URL attribute, and forwards CSS class and style
attributes into the inner anchor so consumers retain full styling control.

## Requirements

### Functional
- [ ] Registers a custom element with tag name `portal-link`
- [ ] Accepts a `href` attribute (required); reflects changes via `attributeChangedCallback`
- [ ] Renders an `<a href="..." data-navigo>` anchor inside the component
- [ ] Exposes a `<slot>` so consumers can project link label content (text or child elements)
- [ ] Uses **Shadow DOM** (`attachShadow({ mode: 'open' })`) — same encapsulation approach as Web Awesome components
- [ ] Exposes the inner anchor via `part="anchor"` so consumers can target it with `portal-link::part(anchor) { }`;
  pseudo-classes can be chained: `portal-link::part(anchor):hover { }`
- [ ] Defines CSS custom properties inside the shadow stylesheet (e.g. `--portal-link-color`,
  `--portal-link-decoration`) with sensible `inherit` fallbacks so consumers can drive theming from a
  `:root`-level theme without touching `::part()` at all
- [ ] `class` attribute placed on `<portal-link>` stays on the host (light DOM); the component's shadow
  stylesheet uses `:host(.active)`, `:host(:hover)`, etc. to react to host-level state/class without
  forwarding anything into the shadow
- [ ] CSS custom properties set inline (`style="--portal-link-color: red"`) propagate into the shadow
  automatically via CSS inheritance; no special handling required in the component
- [ ] Dispatches a `portal-link:connected` custom event (bubbles, composed) on `connectedCallback`
  so `RouterService` (or `main.ts`) can call `router.updatePageLinks()` without the component
  needing to import Navigo
- [ ] Cleans up (removes inner anchor) on `disconnectedCallback`

### Non-functional
- [ ] Zero runtime dependencies — pure Web Component, no framework, no Navigo import inside the component
- [ ] Uses Shadow DOM with `mode: 'open'` — follows the same encapsulation pattern as Web Awesome
- [ ] Three-tier theming model (lowest → highest specificity):
  1. **CSS custom properties** — defined on `:root` or the host, cross the shadow boundary via inheritance
  2. **`::part(anchor)`** — direct, explicit structural overrides from outside
  3. **`:host()`** — internal shadow stylesheet reacts to host classes/pseudo-states
- [ ] TypeScript strict mode, no `any`
- [ ] `const` arrow function style for any free helper functions; class syntax for the element itself

## Technical Specifications

- **File to create**: `apps/portal/src/main/resources/static/src/components/PortalLink.ts`
- **Custom element tag**: `portal-link`
- **Observed attributes**: `href`, `class`, `style`
- **Shadow DOM**: `mode: 'open'`
- **CSS part**: inner `<a>` must have `part="anchor"`
- **CSS custom properties** (defined inside shadow stylesheet with `inherit` fallbacks):
  - `--portal-link-color` → `color`
  - `--portal-link-decoration` → `text-decoration`
  - `--portal-link-hover-color` → `color` on `:host(:hover) a`
- **`:host` selectors** (in shadow stylesheet): at minimum `:host { display: inline }` and `:host(:hover)`,
  `:host(:focus-within)` for accessible interaction states
- **Custom event**: `portal-link:connected` — `new CustomEvent('portal-link:connected', { bubbles: true, composed: true })`
- **Registration**: `customElements.define('portal-link', PortalLink)` at the bottom of the file
- **Export**: `export { PortalLink }` (named export) so it can be imported and tree-shaken
- **Import in host**: add `import './components/PortalLink'` to `src/host/main.ts`
- **RouterService hook**: in `main.ts`, listen for `portal-link:connected` on `document` and call
  `routerService.updatePageLinks()` (or the equivalent exposed method) to register the new link with Navigo
- **Implementation approach**: native `HTMLElement` subclass **or** `defineCustomElement` from Vue (Vue
  is already a dependency of the portal package — no new dependency required for the Vue path)

## Constraints

- Must NOT import `navigo` or any routing library inside the component file
- Must NOT depend on any specific micro-frontend framework (Vue, React, etc.)
- Shadow DOM encapsulation is required — the same model Web Awesome uses; external `.nav-item { }` rules
  do **not** reach inside the shadow (intentional), and this is fine because:
  - CSS custom properties cross the shadow boundary automatically (the primary theming path)
  - `::part(anchor)` provides an explicit escape hatch for direct structural overrides
  - `:host(.nav-item)` inside the shadow stylesheet can respond to host classes
- Shadow DOM must not break Navigo link interception — that is why the `portal-link:connected` event
  has `composed: true`: it crosses the shadow boundary so the `document`-level listener in `main.ts`
  receives it and calls `router.updatePageLinks()`
- The `data-navigo` attribute must be on the rendered `<a>` inside the shadow, not on the host element
- Do not use `innerHTML` string concatenation for construction; use `document.createElement`

## Success Criteria

- [ ] `<portal-link href="/about">About</portal-link>` renders `<a href="/about" data-navigo part="anchor">`
  inside a shadow root with the text "About" projected via `<slot>`
- [ ] Changing the `href` attribute on `<portal-link>` updates the inner anchor's `href` in real time
- [ ] `portal-link::part(anchor) { color: red }` styles the inner anchor from an external stylesheet
- [ ] `portal-link::part(anchor):hover { text-decoration: underline }` — pseudo-classes on `::part()` work
- [ ] Setting `--portal-link-color: red` on the host element (or `:root`) causes the inner anchor's
  color to change via `var(--portal-link-color)` inside the shadow stylesheet
- [ ] `class="active"` on `<portal-link>` triggers `:host(.active)` rules inside the shadow stylesheet
- [ ] `style="--portal-link-color: red"` on `<portal-link>` sets the custom property inline; shadow
  stylesheet picks it up automatically without any `attributeChangedCallback` handling
- [ ] A `portal-link:connected` event is dispatched and bubbles up to `document` on element connect
- [ ] `import './components/PortalLink'` added to `src/host/main.ts` with no compile errors
- [ ] `document.addEventListener('portal-link:connected', ...)` handler in `main.ts` calls `router.updatePageLinks()`
- [ ] TypeScript compiles with zero errors under strict mode

## Examples

### Consumer usage
```html
<!-- Plain text label -->
<portal-link href="/dashboard">Dashboard</portal-link>

<!-- Tier 1: CSS custom properties — cross the shadow boundary via inheritance -->
<style>
  :root {
    --portal-link-color: var(--wa-color-brand-600);
    --portal-link-hover-color: var(--wa-color-brand-800);
    --portal-link-decoration: none;
  }
</style>
<portal-link href="/settings">Settings</portal-link>

<!-- Tier 2: ::part() for direct structural overrides -->
<style>
  portal-link::part(anchor) {
    font-weight: 600;
    letter-spacing: 0.02em;
  }
  portal-link::part(anchor):hover {
    text-decoration: underline;
  }
</style>
<portal-link href="/profile">Profile</portal-link>

<!-- Tier 3: classes on host → :host() inside shadow stylesheet responds -->
<!-- In your shadow stylesheet: :host(.active) a { color: var(--wa-color-brand-700); } -->
<portal-link href="/dashboard" class="active">Dashboard</portal-link>

<!-- Inline custom property override (no ::part() needed) -->
<portal-link href="/admin" style="--portal-link-color: var(--wa-color-danger-600)">
  Admin
</portal-link>

<!-- Projecting child elements -->
<portal-link href="/home">
  <svg>...</svg>
  <span>Home</span>
</portal-link>
```

### Expected rendered shadow DOM
```html
<!-- shadow root -->
<a href="/dashboard" data-navigo part="anchor">
  <slot></slot>
</a>
```

### main.ts hook (to be added)
```typescript
document.addEventListener('portal-link:connected', () => {
    routerService.updatePageLinks();
});
```

## Additional Notes

- `composedPath()` traverses the shadow boundary, so the `portal-link:connected` event with
  `composed: true` is visible to the `document`-level listener in `main.ts` even though it originates
  inside (or is dispatched from) the shadow host.
- If `href` is not provided or is empty, the inner anchor should default to `href="/"` to avoid broken
  links; log a `console.warn` in development.
- The `class` attribute on `<portal-link>` stays on the host element and is **not** forwarded to the
  inner anchor. The shadow stylesheet uses `:host(.my-class)` to apply internal styles based on it.
  If consumers need to drive styles that `::part()` alone cannot express, they should use CSS custom
  properties set on the host via `style="--portal-link-color: ..."` or via a theme at `:root`.
- Web Awesome design tokens (`--wa-*`) are available in scope since `@awesome.me/webawesome` is already
  a dependency — use them as fallback values inside the shadow stylesheet
  (e.g. `color: var(--portal-link-color, var(--wa-color-brand-600, inherit))`).
- Consider whether `PortalLink` should also be exported from `libs/browser/host/src/index.ts` for
  reuse across micro-frontends — flag this as a follow-up if the component is generic enough.
