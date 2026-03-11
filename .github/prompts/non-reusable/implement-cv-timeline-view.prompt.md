---
agent: agent
---

# Implement Unified Timeline View in CV App

## Context
- Current situation: The CV frontend needs a timeline-style section that combines `Experience`, `Education`, and `Certificates` into one chronological view.
- Relevant technologies/frameworks: Vue single-file components (`.vue`), Vite-based frontend, existing `wa-card` UI components in the CV app.
- Dependencies or prerequisites: Existing data models/objects for experience, education, and certificates are available and rendered in the CV app.

## Objective
Create a unified, static timeline view in the CV app that renders mixed career and education-related entries in date order (newest first), with vertical timeline connectors and side-by-side placement for overlapping timeframes.

## Requirements
- [ ] Build a timeline view that merges `Experience`, `Education`, and `Certificates` into a single ordered collection.
- [ ] Sort entries by timeframe so the newest period appears at the top.
- [ ] Render each entry in a `wa-card` and stack timeline rows vertically.
- [ ] Draw vertical connector lines that visually connect timeline rows from top to bottom.
- [ ] Detect overlapping/same timeframes and render grouped entries side by side in the same row.
- [ ] Use fixed column semantics for overlap rows: work experience on the left; education and certificates on the right.
- [ ] Support multiple left-side work entries sharing one right-side education/certificate timeframe (e.g., PO/Developer + Working Student left, University right).
- [ ] Keep layout readable and responsive (desktop first, with sensible behavior on narrow viewports).
- [ ] Implement as a non-interactive timeline (no draggable nodes, no graph editing controls, no zoom/pan behavior).

## Technical Specifications
- Language/Framework: Vue (SFC), TypeScript/JavaScript as already used in the CV app, CSS/SCSS styling consistent with current app conventions.
- Coding standards to follow: Reuse existing component patterns, naming conventions, and styling approach in the CV frontend; keep logic modular and testable.
- Design patterns or architecture: Use a view-model/computed transformation pipeline:
  1) normalize date ranges,
  2) merge and sort entries,
  3) group overlaps into timeline rows,
  4) map grouped rows to left/right render slots.

## Constraints
- Must maintain backward compatibility with existing CV data structures and existing card content fields.
- Performance requirements: Timeline grouping/sorting should remain efficient for typical CV-scale datasets and avoid unnecessary recomputation.
- Resource limitations: Do not add timeline/graph visualization libraries; implement connectors with existing Vue + CSS (minimal JS only for data transformation/layout state).

## Success Criteria
- [ ] A single timeline screen displays mixed `Experience`, `Education`, and `Certificates` entries in newest-first order.
- [ ] Entries are rendered as vertically stacked `wa-card` timeline rows with visible vertical connectors.
- [ ] Overlapping timeframe entries render in one row with work items left and education/certificates right.
- [ ] The overlap example is correctly representable: two work entries left and one university entry right for the same period.
- [ ] The layout remains usable on smaller screens without losing timeline readability.
- [ ] The timeline has no zoom, no node interaction, and no dependency on external timeline/graph libraries.

## Examples (Optional)
```
Example input:
Experience:
- PO / Developer (2021-01 to 2023-12)
- Working Student (2021-01 to 2022-06)
Education:
- University (2021-01 to 2024-01)
Certificates:
- AWS Practitioner (2022-03 to 2022-03)

Example output:
- Timeline sorted newest->oldest
- Row for 2021-2022 overlap:
  left: [PO / Developer, Working Student]
  right: [University]
- Vertical line connects this row with rows above/below
```

## Additional Notes
- Define clear overlap behavior for open-ended ranges (e.g., `to: present`) and partial dates.
- If exact date precision differs across entities (month vs year), normalize before overlap checks.
- Ensure connector line rendering remains aligned when a row has multiple cards in one column.
- If critical source fields are missing (e.g., start/end dates), document fallback behavior explicitly in implementation notes.
- Prefer CSS pseudo-elements and container/row-based line rendering for connector visuals.
