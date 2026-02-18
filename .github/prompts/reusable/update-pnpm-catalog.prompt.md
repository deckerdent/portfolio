---
agent: agent
---

# Update pnpm Catalog Version Constraints

## Context
- Workspace uses pnpm with a monorepo structure
- Version constraints should be centralized via pnpm catalog entries
- Dependencies should align across all workspace packages
- Versions should be stable and compatible with existing tooling

## Objective
Update pnpm workspace version control by adding or refreshing catalog entries to enforce consistent, stable dependency versions across all packages.

## Requirements
- [ ] Detect existing versions and constraints in the workspace
- [ ] Add or update catalog entries for shared dependencies
- [ ] Prefer stable releases (no alpha/beta/RC unless already used)
- [ ] Avoid breaking changes by matching current major versions
- [ ] Preserve existing naming conventions and structure
- [ ] Ensure all relevant packages resolve through catalog entries

## Technical Specifications
- File: pnpm-workspace.yaml and/or package.json (catalog entries location)
- Use pnpm catalog syntax (catalog: or catalog:default as appropriate)
- Ensure consistency with any existing overrides or workspace constraints
- Keep dependency ranges aligned with Node.js and tooling versions

## Constraints
- Must not downgrade existing versions
- Must remain compatible with current Node.js and build tooling
- Avoid introducing duplicate or conflicting catalog keys

## Success Criteria
- [ ] Catalog entries exist for all shared dependencies
- [ ] Versions are stable and compatible across packages
- [ ] Workspace installs resolve through catalog entries
- [ ] No dependency conflicts are introduced
- [ ] Configuration is valid for pnpm workspace resolution

## Examples

### Example catalog entry usage
```yaml
# pnpm-workspace.yaml
packages:
  - "apps/*"
  - "libs/*"

catalogs:
  default:
    vue: ^3.4.0
    vite: ^5.1.0
```

```json
// package.json
{
  "dependencies": {
    "vue": "catalog:default",
    "vite": "catalog:default"
  }
}
```

## Additional Notes
- If multiple catalogs are already in use, align with the existing pattern
- If a dependency is only used in a single package, do not add it to the catalog unless requested
- Document any version choices that deviate from current workspace norms
