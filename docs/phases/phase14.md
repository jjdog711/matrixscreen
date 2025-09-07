# Phase 14 — Store Readiness

## Session 1 — Adaptive & Monochrome Icons

### System prompt

```
Same rules.
```

### Task prompt

```
Provide adaptive icons and monochrome variant; ensure densities.
```

### Touch-only paths

- app/src/main/res/**

### Acceptance

- Icons appear correctly in launcher; monochrome present

## Session 2 — About & Privacy

### System prompt

```
Same rules.
```

### Task prompt

```
Add About screen and minimal Privacy stub; link from settings.
```

### Touch-only paths

- app/src/main/java/com/example/matrixscreen/ui/about/**
- app/src/main/res/values/**

### Acceptance

- Screens accessible; strings centralized

## Session 3 — Release Config & Metadata

### System prompt

```
Same rules.
```

### Task prompt

```
Configure release build (no keystore file), semantic versioning, and a README section for Play assets.
```

### Touch-only paths

- app/build.gradle.kts
- README.md

### Acceptance

- Release assembles; metadata guide present
