---
agent: agent
---

# Fix CV Frontend Design Flaws

## Context

- Vue 3 micro-frontend CV module embedded in a Spring Boot portal via Module Federation
- UI uses Web Awesome component library (`wa-*` custom elements)
- Styles live in `apps/cv/src/main/resources/static/src/apps/cv/styles.css`
- Components: `GeneralInfoPanel.vue` (sidebar), `SkillsView.vue` (main tab)
- `generalInfo.imageUrl` contains a relative URL (e.g. `/image.jpeg`) served from the CV Spring Boot service on port 8081

## Objective

Fix four visual design issues in the CV frontend: broken avatar image fallback, cluttered language list, under-padded hobby badges, and a side-by-side skills/competences layout that should be vertically stacked.

## Requirements

- [ ] **Avatar**: The `<img>` tag renders but falls back to initials even when `imageUrl` is set. The relative URL `/image.jpeg` resolves to the portal origin (8080) instead of the CV service origin (8081). Prefix the URL with the CV service origin using `new URL(import.meta.url).origin` or inject the base URL so the image loads correctly.
- [ ] **Languages**: Remove the `|` separator between language entries. Each language should appear on its own line with its name and star rating side by side.
- [ ] **Hobby badges**: Increase the internal padding of the hobby `<wa-badge>` tags so they feel less cramped. Use CSS custom properties or the `size` attribute — whichever gives more breathing room.
- [ ] **Skills & Competences layout**: Change from side-by-side (2-column grid) to vertically stacked. Skills themselves should display in a 2-column grid (skill name + progress bar pairs). Competences section appears below skills, full-width, with badges wrapping.

## Technical Specifications

- Framework: Vue 3 SFC `<script setup lang="ts">`
- Styles: CSS custom properties from Web Awesome design tokens (`--wa-space-*`, `--wa-color-*`, etc.) — no hardcoded values
- No new dependencies

## Constraints

- Do not hardcode `http://localhost:8081` — derive the origin dynamically so it works in both dev and production
- Keep all existing responsive breakpoints intact
- Do not change the data-fetching logic

## Success Criteria

- [ ] Avatar image displays correctly when `imageUrl` is a relative path like `/image.jpeg`
- [ ] Each language is on its own line; no `|` character visible
- [ ] Hobby badges have noticeably more padding than before
- [ ] Skills section shows two columns of skill+bar pairs
- [ ] Competences section is below skills, full-width

## Examples

**Languages — before:**
```
German | ★★★★★★★★★★  English | ★★★★★★★☆☆☆
```

**Languages — after:**
```
German   ★★★★★★★★★★
English  ★★★★★★★☆☆☆
```

**Skills layout — before:**
```
[ Skills col ]  [ Competences col ]
```

**Skills layout — after:**
```
[ Skill A ████████ ]  [ Skill B ███████  ]
[ Skill C ██████   ]  [ Skill D █████    ]

Competences:
[Micro-frontend] [Agile] [Cloud-native] [Mentoring]
```

## Additional Notes

- The `cvApi` axios instance already has `baseURL` set to the CV service origin via `new URL(import.meta.url).origin` in `Module.ts`. The same origin value can be read in `GeneralInfoPanel.vue` via `inject('cvApi')` and accessing its `defaults.baseURL`, or by importing/injecting the base URL separately.
- `wa-badge` padding may require a CSS part selector (`::part(base)`) since Web Awesome components use shadow DOM.
