---
agent: agent
---

# Fix Navigo Prefix Route Matching for CV App

## Context
- Current situation:
  - The CV frontend is mounted under `/cv`.
  - Navigo currently relies on exact route definitions, so direct navigation to nested paths (for example `/cv/experience`) can fail if that exact route is not declared.
  - This causes route resolution issues when users deep-link, refresh on subpages, or navigate directly via URL.
- Relevant technologies/frameworks:
  - Navigo (client-side router)
  - Vue frontend in the CV app
  - Monorepo project structure with the CV static frontend
- Dependencies or prerequisites:
  - Existing route setup for `/cv` is already in place.
  - Route handling code for the CV app is accessible and editable.

## Objective
Update the CV app’s Navigo routing so nested URLs under `/cv` resolve reliably without requiring every full path to be explicitly defined. Implement prefix/startsWith-style matching or equivalent fallback behavior to support deep links like `/cv/experience`.

## Requirements
- [ ] Ensure `/cv` continues to resolve and render as it does today.
- [ ] Ensure `/cv/experience` resolves successfully even when only `/cv` (or a parent route) is explicitly configured.
- [ ] Implement route matching behavior that supports prefix-based matching (or an equivalent fallback strategy) rather than strict full-path matching only.
- [ ] Preserve existing route handlers/components and avoid breaking current navigation behavior.
- [ ] Keep the implementation clear and maintainable, with minimal changes to unrelated routing code.
- [ ] Add or update tests (if routing tests exist) to cover nested direct URL access.

## Technical Specifications
- Language/Framework:
  - TypeScript/JavaScript for frontend routing logic
  - Vue for view rendering
  - Navigo for route matching/navigation
- Coding standards to follow:
  - Follow existing project linting/style conventions.
  - Prefer small, focused changes and descriptive naming.
  - Keep routing logic deterministic and easy to reason about.
- Design patterns or architecture:
  - Use a parent-route-first or fallback-route strategy for nested paths.
  - Prefer centralized router configuration updates over scattered ad hoc checks.

## Constraints
- Must maintain backward compatibility with current `/cv` behavior.
- Performance requirements:
  - Route matching should remain lightweight; avoid expensive per-navigation parsing.
- Resource limitations:
  - Do not introduce unnecessary new dependencies if Navigo can handle this natively.
  - Avoid broad refactors outside the routing concern.

## Success Criteria
- [ ] Visiting `/cv` directly still renders the expected CV app view.
- [ ] Visiting `/cv/experience` directly loads the CV app and displays the expected nested/experience content (or valid routed state) without a not-found failure.
- [ ] Refreshing the browser on `/cv/experience` no longer causes route resolution failure in the client router.
- [ ] Existing routing behavior for already defined routes remains unchanged.
- [ ] Tests or validation steps demonstrate successful handling of both base and nested CV paths.

## Examples (Optional)
```
Example input:
- Browser URL: /cv
- Browser URL: /cv/experience

Example output:
- /cv loads the CV app as before.
- /cv/experience also loads the CV app successfully via prefix/fallback route matching, instead of failing due to missing exact route definition.
```

## Additional Notes
- Consider edge cases such as trailing slashes (`/cv/experience/`), query strings (`/cv/experience?tab=work`), and hash fragments.
- If multiple route patterns could match, define and document clear precedence.
- If this behavior may later apply to other apps (e.g., `/portal/...`), structure the solution so it can be generalized with minimal additional work.