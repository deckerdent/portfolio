---
agent: agent
---

# Refine CV Color Palette — Diversify Beyond Monochromatic Green

## Context

- The CV micro-frontend lives at `apps/cv/src/main/resources/static/`.
- The CV app does **not** have its own Web Awesome theme CSS file. It inherits the portal theme (`wa-theme-portal`) defined in `apps/portal/src/main/resources/static/src/styles/portal.css`, which is applied to `<html>` by the portal host.
- All component styles for the CV views are in `apps/cv/src/main/resources/static/src/apps/cv/styles.css`. This file exclusively uses Web Awesome design tokens (`var(--wa-color-*)`, `var(--wa-space-*)`, etc.) — no hardcoded hex colors.
- The current portal theme uses **only green tones** everywhere: brand fills, surfaces, borders, and text are all green-derived. The result is a flat, tone-in-tone appearance.
- The existing green primary colors must be **kept**:
  - Primary dark: `#194430` (dark forest green)
  - Primary light: `#528C5C`
  - Primary lighter: `#6D9B6D`
  - Text on loud: `#FCE9C8` (warm cream — keep)
- The portal theme has three rule blocks in `portal.css`:
  1. **Light** — applies to `.wa-theme-portal`, `.wa-theme-portal.wa-light`, etc.
  2. **Dark** — applies to `.wa-theme-portal.wa-dark`, `.wa-dark .wa-theme-portal`, etc.
  3. **Shared / Design tokens** — fonts, spacing, borders, shadows, component vars (no color changes here).
- The `@layer wa-theme-overrides` block at the bottom controls badge, card, button component styling — it may also need accent color touches.

## Objective

Diversify the color palette in `portal.css` by introducing complementary accent colors alongside the existing greens, making the CV design visually richer and more interesting while remaining professional and polished.

## Palette Direction

Keep the primary green scale. Add **two complementary accent colors**:

| Role | Suggested value | Rationale |
|---|---|---|
| Warm amber / gold accent | `#C49A3C` (light) / `#8B6A1A` (dark) | Analogous earthy warmth, pairs well with forest green |
| Slate blue accent | `#4A6B8A` (light) / `#2E4D6B` (dark) | Cool contrast to warm green, professional |

These should be used for:
- `--wa-color-surface-raised` / `surface-default` in light mode → slightly warmer cream/ivory (already `#f7f2e9` — good, keep or refine)
- `--wa-color-surface-raised` in dark mode → deep navy-green (currently pure dark green — could shift slightly toward neutral dark)
- Timeline card left border in `styles.css` → use amber accent instead of brand green
- Section headings in `styles.css` → consider slate blue or amber
- Badge backgrounds (hobbies, competences) → amber fill instead of plain green
- Tab active indicator → keep brand green (it's the nav identity)
- Avatar initials background → keep brand green

## Requirements

- [ ] Edit **only** `apps/portal/src/main/resources/static/src/styles/portal.css` for theme token changes
- [ ] Edit `apps/cv/src/main/resources/static/src/apps/cv/styles.css` to use new accent custom properties where appropriate
- [ ] Do **not** rename or add CSS custom property keys in `portal.css` — only change their **values**
- [ ] Introduce accent color usage through **existing token slots** (e.g. reassign `--wa-color-warning-*` or `--wa-color-neutral-*` buckets to the amber palette, keeping semantic integrity)
- [ ] Keep all primary/brand green tokens as-is
- [ ] Light theme surfaces remain warm cream/off-white — no stark white
- [ ] Dark theme surfaces remain deep dark-green, not pure black
- [ ] Text contrast must remain AA compliant (readable on all backgrounds)
- [ ] `pnpm run build` in `apps/cv/src/main/resources/static/` must pass with no errors

## Technical Specifications

- File to edit (theme): `apps/portal/src/main/resources/static/src/styles/portal.css`
- File to edit (CV component styles): `apps/cv/src/main/resources/static/src/apps/cv/styles.css`
- Pattern: CSS custom properties only in `portal.css`; direct token references in `styles.css`
- Relevant token slots for accent injection:
  - Warning tokens (`--wa-color-warning-*`) → repurpose to amber/gold scale for CV badge accents
  - Neutral tokens (`--wa-color-neutral-*`) → shift toward slate/blue-gray to break green monotony
- CV-specific elements to accent:
  - `.cv-timeline-card { border-left }` — try amber `#C49A3C` or `var(--wa-color-warning-border-loud)`
  - `.cv-section-heading` — try slate text `var(--wa-color-neutral-on-loud)` or a new custom property
  - `.cv-tabs a.router-link-active { border-bottom-color }` — keep brand green
  - `.cv-hobbies-list wa-badge` and `.cv-competences-list wa-badge` — consider amber-tinted fill

## Constraints

- The portal header gradient (in `PortalHeader.ts`) uses `--portal-header-bg` — do not touch
- Do not break dark mode — both themes must be explicitly handled
- The CV is embedded in the portal via module federation — no separate theme file for CV; it inherits portal theme
- Accent colors must feel **professional**: muted, not saturated or garish

## Success Criteria

- [ ] The CV page has at least 3 visually distinct color roles visible at a glance (green, amber/warm, and a neutral/cool)
- [ ] Timeline card left borders use the amber/warm accent color
- [ ] Badges (hobbies & competences) have a warm amber tint rather than plain green
- [ ] Section headings have a visually distinct treatment (subtle color or weight differentiation)
- [ ] Light and dark modes both look balanced and professional
- [ ] `pnpm run build` passes with 0 errors in `apps/cv/src/main/resources/static/`
- [ ] No hardcoded hex values introduced into `styles.css` — only token references

## Examples

```css
/* Before — everything resolves to green */
.cv-timeline-card {
    border-left: 3px solid var(--wa-color-brand-border-normal); /* green */
}

/* After — amber accent for timeline differentiation */
.cv-timeline-card {
    border-left: 3px solid var(--wa-color-warning-border-loud); /* amber */
}
```

```css
/* Before — neutral tokens are greenish */
--wa-color-neutral-fill-quiet: #f0f5f0;
--wa-color-neutral-border-normal: #7e967f;

/* After — neutrals shifted toward blue-gray to break monotony */
--wa-color-neutral-fill-quiet: #f0f3f7;
--wa-color-neutral-border-normal: #8094a8;
```

## Additional Notes

- The `warning` token family is currently using the default palette (yellows). Repurposing it to a refined amber/gold scale is acceptable since the CV doesn't use warning states semantically.
- The `neutral` token family drifting into blue-gray is a common technique in design systems to avoid color contamination from the brand hue.
- Avoid making the dark theme look like a "military" or "hacker" theme — the amber accents should feel warm and artisanal, not aggressive.
- Prioritize changes that are visible at first glance: timeline borders, badge fills, and section headings are the highest-impact elements.
