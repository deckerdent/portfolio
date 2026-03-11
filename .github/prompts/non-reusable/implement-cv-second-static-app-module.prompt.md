---
agent: agent
---

# Implement Second CV Static App Module with Single Landing View

## Context
- Current frontend location: `apps/cv/src/main/resources/static/`
- Existing module pattern: `src/apps/cv/Module.ts` using Vue 3, `vue-router`, and Module Federation exposure (`./CvApp`) in `vite.config.mts`
- Existing structure includes app-local folders for `app/`, `views/`, and module bootstrap via `mount`/`unmount`
- New request: add a **second app/module** under the CV static frontend with only one static view and fixed image paths
- UI content order required for the single view:
  1. Banner placeholder
  2. Welcome text
  3. Two-column section: image placeholder on the left, text on the right
  4. Two-column section below: text on the left, image placeholder on the right

## Objective
Create a second micro-frontend app/module in the CV static project that renders a single static landing-style page matching the required section order, and expose it through Module Federation so it can be loaded independently.

## Requirements
- [ ] Create a new app/module folder under `src/apps/` (e.g., `src/apps/cv-static/` or similarly clear naming)
- [ ] Implement `Module.ts` with the same lifecycle pattern as existing modules (`mount` and `unmount`)
- [ ] Add a minimal `app/App.vue` shell that renders `<RouterView />`
- [ ] Configure router with exactly one route (`'/'`) for the static view
- [ ] Create one view component for the static page (e.g., `views/StaticLandingView.vue`)
- [ ] Build the static page layout in this exact order:
  - [ ] Banner placeholder section at top
  - [ ] Welcome text section below banner
  - [ ] First content row: image placeholder (left), text block (right)
  - [ ] Second content row: text block (left), image placeholder (right)
- [ ] Define image paths as hardcoded/static values in the view/component (no API fetch)
- [ ] Add local styling file (`styles.css`) for layout spacing, responsive columns, and placeholder visuals
- [ ] Register and expose the new module in `vite.config.mts` `federation.exposes` (e.g., `./CvStaticApp`)

## Technical Specifications
- Language/Framework: TypeScript + Vue 3 (`<script setup lang="ts">`) + `vue-router`
- Architecture pattern: follow existing module bootstrap approach from `src/apps/cv/Module.ts`
- Routing: `createRouter` + `createWebHistory(basename)` with one route only
- Styling: module-local CSS, responsive two-column sections collapsing to one column on small screens
- Assets: static image URLs from local/public assets (e.g., `/images/...`) or module-relative static paths

## Constraints
- Must not modify existing `cv` module behavior or routes
- Must contain only one visible page/view in the new module
- Must not depend on backend APIs or runtime data for this page
- Must preserve existing coding style and project structure conventions
- Keep content static and deterministic (no random/generated content)

## Success Criteria
- [ ] New second module is present in `src/apps/` with `Module.ts`, `app/App.vue`, `views/`, and `styles.css`
- [ ] App mounts/unmounts successfully using the same lifecycle contract as existing modules
- [ ] Visiting the module root route renders exactly one static page
- [ ] Section order and left/right layout match the requested structure
- [ ] Static image paths are defined directly in code and render correctly
- [ ] New Module Federation exposure is configured and build/dev startup succeeds

## Examples (Optional)
```text
Example page structure:
- Banner Placeholder
- Welcome to this page
- [Image Placeholder] | [Text paragraph...]
- [Text paragraph...] | [Image Placeholder]
```

```ts
// Example expose entry in vite.config.mts
exposes: {
  './CvApp': './src/apps/cv/Module.ts',
  './CvStaticApp': './src/apps/cv-static/Module.ts'
}
```

## Additional Notes
- If naming for the second app/module is ambiguous, use a clear intent-based name (e.g., `cv-static`, `cv-landing`, or `cv-home`) and keep naming consistent across folder, module expose key, and view/component names.
- Use semantic HTML sections (`section`, `header`, `main`) where practical for readability.
- Implement placeholders as styled containers with visible labels (e.g., “Banner Placeholder”, “Image Placeholder 1”, “Image Placeholder 2”).
- Ensure responsive behavior: desktop uses two columns for content rows; mobile stacks content vertically while preserving section order.
- If existing shared design tokens/utilities are available in CV static styles, prefer reusing them instead of introducing unrelated styling systems.
