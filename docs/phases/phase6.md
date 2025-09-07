# Phase 6 — Background

## Session 1 — BackgroundSettingsScreen + FPS Coercion

### System prompt

```
Same rules.
```

### Task prompt

```
Implement BACKGROUND_SPECS and device FPS coercion util; display effective FPS.
```

### Touch-only paths

- app/src/main/java/com/example/matrixscreen/ui/settings/background/**
- app/src/main/java/com/example/matrixscreen/core/util/**

### Acceptance

- Unsupported FPS coerced; UI shows effective value

## Session 2 — Renderer Params Mapper

### System prompt

```
Same rules.
```

### Task prompt

```
Map effective FPS and grain params into RendererParams; engine untouched.
```

### Touch-only paths

- app/src/main/java/com/example/matrixscreen/domain/usecase/**
- app/src/main/java/com/example/matrixscreen/engine/uniforms/**

### Acceptance

- Mapper compiles; no engine behavior change

## Session 3 — Tests (Background/FPS)

### System prompt

```
Same rules.
```

### Task prompt

```
Unit tests for coercion and mapping.
```

### Touch-only paths

- app/src/test/java/com/example/matrixscreen/**

### Acceptance

- Tests pass
