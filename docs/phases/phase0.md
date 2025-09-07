# Phase 0 — Foundations & Guardrails

## Session 1 — Repository Structure, Naming, Tokens, Contributor Guide

### System prompt

```
Follow MatrixScreen guardrails. Engine untouched. Material 3 only. Trailing lambdas. No ui.surface. Do not edit originals; copy to docs/standards/dev/ if needed.
```

### Task prompt

```
Create repository folders and minimal theme tokens.
Add docs/standards/original/ with Product_Tech_Intent.md, Phased_Plan.md, Agent_Standards_and_Specs.md (copy current approved text).
Add scripts/repo_map.json mapping key paths. Add core/design tokens (spacing, radii, outline, focus glow).
```

### Touch-only paths

- docs/standards/original/**
- scripts/repo_map.json
- app/src/main/java/com/example/matrixscreen/core/design/**

### Acceptance

- Project builds
- Original docs present; dev copy policy noted
- Design tokens compile and are used in previews

## Session 2 — Repo Map & Guardrails Checkers

### System prompt

```
Same rules.
```

### Task prompt

```
Add scripts/repo_map.json with overlay, nav, specs, bindings, presets, repo locations. Optional Gradle task to print guardrail reminders.
```

### Touch-only paths

- scripts/repo_map.json
- build.gradle.kts

### Acceptance

- repo_map.json accurate
- Build reminders print (optional)
