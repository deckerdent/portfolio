---
agent: agent
---

# Add `skills[]` and `highlights` to CV Experience (Backend + Frontend + Seed Data)

## Context
- Repository is a monorepo with self-contained apps, including `apps/cv`.
- CV app is split across:
  - Backend: Spring Boot WebFlux + JPA + Flyway + OpenAPI-first codegen.
  - Frontend: Vue 3 + Vite + WebAwesome components.
- Current experience model already supports fields like `title`, `companyName`, `startDate`, `endDate`, `location`, and `description`.
- Seed/demo content is provided in `apps/cv/src/main/resources/static/public/cv.json` and loaded by backend startup logic.
- The UI renders experiences inside WebAwesome `wa-card` components.

## Objective
Extend CV experiences to include `skills` (array of strings) and `highlights` (string), propagate changes through backend and frontend contracts/implementations, update seed data, and render both fields in the experience cards with clear visual emphasis.

## Requirements
- [ ] Add `skills` and `highlights` to the backend experience contract and persistence model.
- [ ] Ensure API responses for experience endpoints include `skills` and `highlights`.
- [ ] Ensure API requests for creating/updating experiences accept `skills` and `highlights`.
- [ ] Update backend mapping, validation, and persistence so values round-trip correctly.
- [ ] Add/adjust Flyway migration(s) for schema changes without breaking existing data.
- [ ] Update `cv.json` seed data so each experience has realistic `skills` entries and `highlights` text.
- [ ] Update frontend type definitions and data handling to include new fields.
- [ ] Render `skills` as WebAwesome badges in each experience `wa-card`.
- [ ] Render `highlights` below the description with a visually eye-catching style.
- [ ] Add/update backend tests (including E2E/integration) to verify new fields persist and return correctly.
- [ ] Add/update frontend tests to verify badges and highlights render as expected.

## Technical Specifications
- Language/Framework:
  - Backend: Java 17, Spring Boot WebFlux, JPA, Flyway, OpenAPI-generated API models.
  - Frontend: Vue 3 (`<script setup lang="ts">`), WebAwesome components.
- Backend implementation expectations:
  - Update OpenAPI schema in `apps/cv/src/main/resources/openapi/api.yml` for `ExperienceRequest` and `ExperienceResponse`.
  - Regenerate/compile generated interfaces/models via existing Gradle flow.
  - Update domain entity for experience (e.g., `ProfessionalExperience`) to persist:
    - `skills` as collection/JSON/text strategy appropriate to current architecture.
    - `highlights` as text/varchar field.
  - Add Flyway migration under `apps/cv/src/main/resources/db/migration/`.
  - Update mapper(s) (`ExperienceMapper`) and service/controller logic as needed.
- Frontend implementation expectations:
  - Update `apps/cv/src/main/resources/static/src/apps/cv/types.ts` experience type.
  - Update experience/timeline views/components that render experience cards.
  - Render `skills` via `wa-badge` list/chips inside card body/footer.
  - Render `highlights` below description with strong visual cue (e.g., callout row, icon, accent border/background).
- Styling expectations:
  - Keep design consistent with existing CV styles and WebAwesome tokens.
  - Ensure responsive behavior and no layout breakage in cards.

## Constraints
- Preserve existing endpoint paths and response compatibility for existing required fields.
- Avoid breaking existing demo-data loading and startup behavior.
- Follow repository conventions (OpenAPI-first, package-by-feature style where applicable).
- Keep JPA calls off Netty event loop (use existing boundedElastic patterns where already used).
- Do not introduce unrelated refactors.

## Success Criteria
- [ ] `GET /api/cv/experiences` returns `skills` and `highlights` for each experience.
- [ ] `POST/PUT /api/cv/experiences` can create/update `skills` and `highlights`.
- [ ] Database schema supports storing both fields and Flyway migration runs cleanly.
- [ ] `cv.json` includes populated `skills` arrays and `highlights` strings for experiences.
- [ ] Experience cards show `skills` as badges.
- [ ] Experience cards show `highlights` below description with clear visual emphasis.
- [ ] Backend tests pass with assertions for new fields.
- [ ] Frontend tests pass with assertions for badges/highlights rendering.

## Examples (Optional)
```yaml
# OpenAPI example snippet (illustrative)
ExperienceResponse:
  allOf:
    - $ref: "#/components/schemas/BaseResponse"
    - type: object
      properties:
        title:
          type: string
        companyName:
          type: string
        skills:
          type: array
          items:
            type: string
        highlights:
          type: string
```

```json
{
  "title": "Senior Developer",
  "companyName": "Acme Corp",
  "startDate": "2024-01-01",
  "description": "Built modular systems",
  "skills": ["Java", "Spring Boot", "PostgreSQL", "Vue"],
  "highlights": "Led migration to modular architecture, reducing deployment risk by 40%."
}
```

## Additional Notes
- If storage strategy for `skills` is ambiguous, document the chosen approach (join table vs array/json/text) and why it fits current schema conventions.
- Ensure null/empty handling is explicit:
  - `skills`: allow empty array (not null preferred for API response consistency).
  - `highlights`: allow blank/null only if product requirement permits; otherwise validate.
- For visual emphasis of `highlights`, prefer accessibility-friendly contrast and semantic structure.
- Update any mapping tests or serialization tests impacted by generated model changes.
- Include targeted regression checks to ensure existing timeline/experience rendering remains intact.
