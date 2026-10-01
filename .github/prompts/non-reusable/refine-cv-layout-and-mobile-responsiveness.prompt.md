---
agent: agent
---

# Refine CV Section Header Layout and Mobile Responsiveness

## Context
- Current situation:
  - The CV frontend has layout and responsiveness issues across multiple tabs/sections (`experience`, `education`, `certificates`, `references`, tab navigation, and general information panel).
  - Date/location metadata is currently placed in a lower container and should be moved up near company/institution names.
  - On mobile, some containers/cards exceed viewport width and horizontal overflow occurs.
- Relevant technologies/frameworks:
  - Frontend in the CV app (`apps/cv`) using HTML templates/components with CSS styling.
  - Existing reusable card/header patterns already used in `skills` and `certificates` views.
- Dependencies or prerequisites:
  - Reuse existing icon system/assets (calendar icon if already available in project styles/components).
  - Preserve current data model and JSON structure (e.g., `cv.json`) unless absolutely necessary.

## Objective
Refactor the CV UI layout so section headers are consistent and metadata placement is improved, while fixing mobile responsiveness and horizontal overflow across cards and tab navigation.

## Requirements
- [ ] Move `start/end date` and `location` in `experience`, `education`, and `certificates` to the top header area next to company/institution/title.
- [ ] Keep the existing lower metadata container in those cards, but repurpose it for descriptions/content text.
- [ ] Insert a calendar icon between company/institution name and the date range in section headers.
- [ ] Add a small margin around the root part of each card header for better spacing.
- [ ] Apply the same headline/header pattern used by `skills` and `certificates` to `experience` and `education` views.
- [ ] In references, set `.cv-reference-card-header` to use centered cross-axis alignment (`align-items: center`).
- [ ] Increase badge padding in references for improved readability/tap target.
- [ ] Remove pixel-based `max-width` constraints from the general information panel that break mobile centering.
- [ ] Ensure all cards shrink/wrap correctly on small screens and never exceed viewport width.
- [ ] Set tab buttons container/items to align content vertically centered (`align-items: center`).
- [ ] Add horizontal scrolling behavior for the tab buttons row only when overflow occurs.
- [ ] Prevent the rest of the page content from horizontal overflow; only tab row may scroll horizontally.

## Technical Specifications
- Language/Framework:
  - HTML/CSS/TypeScript (or existing frontend stack conventions in `apps/cv/src/main/resources/static`).
- Coding standards to follow:
  - Preserve naming conventions and existing component/CSS architecture.
  - Prefer fluid sizing (`width: 100%`, `max-width: 100%`, `min-width: 0`, `flex-wrap`, `overflow-x` control) over fixed pixel constraints.
  - Keep accessibility in mind (icon decorative where appropriate via `aria-hidden`, maintain readable spacing).
- Design patterns or architecture:
  - Reuse existing section header/card patterns from `skills` and `certificates` rather than introducing new divergent structures.
  - Keep changes scoped to CV frontend templates/styles; avoid backend/API changes.

## Constraints
- Must maintain backward compatibility with current CV data schema and rendering logic.
- Performance requirements:
  - Avoid heavy DOM restructuring or JS layout hacks when CSS can solve responsiveness.
- Resource limitations:
  - No redesign of unrelated sections; implement minimal, targeted layout/style updates.
  - Do not introduce fixed-width layouts that cause overflow on narrow viewports.

## Success Criteria
- [ ] In `experience`, `education`, and `certificates`, date/location is visible in the header next to title/company and includes a calendar icon separator.
- [ ] Description content appears in the previously used metadata container without losing existing information.
- [ ] `experience` and `education` visually match the established headline pattern used elsewhere.
- [ ] References header content is vertically centered and badges have visibly increased padding.
- [ ] General information panel remains centered on mobile without pixel max-width constraints.
- [ ] On screens down to 320px width, no card causes page-level horizontal scrolling.
- [ ] Tab button row scrolls horizontally when needed; main page body does not overflow horizontally.
- [ ] Visual regression check confirms desktop layout remains intact while mobile behavior improves.

## Examples (Optional)
```text
Before:
- Company Name
  Date/Location shown in a lower metadata row

After:
- Company Name  [calendar-icon]  Jan 2022 – Present · Berlin
  (lower row now contains description text)
```

## Additional Notes
- If critical implementation details are unclear, inspect existing `skills` and `certificates` card/header markup and mirror that structure for consistency.
- Ensure tab scrolling is isolated to the tab strip container (e.g., `overflow-x: auto; overflow-y: hidden;`) and that page/root containers use `overflow-x: hidden` only where appropriate.
- Validate behavior in both desktop and mobile breakpoints, including long titles and long badge text wrapping.
