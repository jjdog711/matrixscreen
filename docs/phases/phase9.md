# Phase 9 — Theme

## Session 1 — ThemeSettingsScreen — Basic

### System prompt

```
Same rules.
```

### Task prompt

```
Implement basic tint presets and simple UI/bg color pickers via specs.
```

### Touch-only paths

- app/src/main/java/com/example/matrixscreen/ui/settings/theme/**
- app/src/main/java/com/example/matrixscreen/ui/settings/model/**

### Acceptance

- Basic theming works; updates draft

## Session 2 — ThemeSettingsScreen — Advanced + Link Toggle

### System prompt

```
Same rules.
```

### Task prompt

```
Add per-channel ColorSpec rows and 'Link UI & Rain colors' (off by default).
```

### Touch-only paths

- app/src/main/java/com/example/matrixscreen/ui/settings/theme/**
- app/src/main/java/com/example/matrixscreen/core/util/**

### Acceptance

- Advanced colors functional; link mapping coherent

## Session 3 — Contrast Guard

### System prompt

```
Same rules.
```

### Task prompt

```
Ensure legibility with dynamic text tones; utilities in core/util.
```

### Touch-only paths

- app/src/main/java/com/example/matrixscreen/core/util/**
- app/src/main/java/com/example/matrixscreen/ui/settings/theme/**

### Acceptance

- Contrast maintained

## Session 4 — Tests (Theme)

### System prompt

```
Same rules.
```

### Task prompt

```
Unit tests for link mapping & contrast; UI tests for picker, confirm/cancel.
```

### Touch-only paths

- app/src/test/java/com/example/matrixscreen/**
- app/src/androidTest/java/com/example/matrixscreen/**

### Acceptance

- Tests pass
