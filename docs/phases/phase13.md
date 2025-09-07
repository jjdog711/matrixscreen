# Phase 13 — Tests, QA, Accessibility

## Session 1 — Compose UI Regression Suite

### System prompt

```
Same rules.
```

### Task prompt

```
Add tests for draft→commit/cancel, color picker, FPS coercion, deep links.
```

### Touch-only paths

- app/src/androidTest/java/com/example/matrixscreen/**

### Acceptance

- Test suite passes

## Session 2 — Perf Logger Hooks

### System prompt

```
Same rules.
```

### Task prompt

```
Add debug-only perf logger around frame update/render (no UI).
```

### Touch-only paths

- app/src/main/java/com/example/matrixscreen/core/util/**

### Acceptance

- Logger compiles; disabled by default

## Session 3 — Accessibility Labels & Focus

### System prompt

```
Same rules.
```

### Task prompt

```
Add content descriptions, focus order, large-text check across key screens.
```

### Touch-only paths

- app/src/main/java/com/example/matrixscreen/ui/**

### Acceptance

- A11y checks pass
