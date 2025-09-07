# Phase 10 — Presets as Data

## Session 1 — Preset Model & Built-ins

### System prompt

```
Same rules.
```

### Task prompt

```
Add Preset data model and built-in preset definitions.
```

### Touch-only paths

- app/src/main/java/com/example/matrixscreen/data/model/**
- app/src/main/java/com/example/matrixscreen/data/presets/**

### Acceptance

- Built-ins available; versioned

## Session 2 — Apply & Clamp Use-Cases

### System prompt

```
Same rules.
```

### Task prompt

```
Add ApplyPresetUseCase and ClampSettingsUseCase with tests.
```

### Touch-only paths

- app/src/main/java/com/example/matrixscreen/domain/usecase/**
- app/src/test/java/com/example/matrixscreen/**

### Acceptance

- Unit tests pass

## Session 3 — Manage Presets Screen + JSON Import/Export

### System prompt

```
Same rules.
```

### Task prompt

```
List/apply/export/import presets via SAF (.ms-preset.json).
```

### Touch-only paths

- app/src/main/java/com/example/matrixscreen/ui/settings/presets/**
- app/src/main/java/com/example/matrixscreen/data/**

### Acceptance

- Round-trip works; applying updates draft
