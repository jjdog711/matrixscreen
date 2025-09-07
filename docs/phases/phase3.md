# Phase 3 — Overlay & Navigation Shell

## Session 1 — QuickSettingsPanel Scaffold

### System prompt

```
Guardrails apply. Engine untouched. Material 3. Trailing lambdas.
```

### Task prompt

```
Create ui/overlay/QuickSettingsPanel.kt as bottom dock with Presets, Speed, Columns, Glow, Grain Opacity, FPS; use RenderSetting; Confirm/Cancel; live background visible.
```

### Touch-only paths

- app/src/main/java/com/example/matrixscreen/ui/overlay/**
- app/src/main/java/com/example/matrixscreen/ui/**

### Acceptance

- Panel edits draft; confirm/cancel work; background visible

## Session 2 — SettingsNavGraph + SettingsHome

### System prompt

```
Same rules.
```

### Task prompt

```
Create SettingsNavGraph with routes, SettingsHome with category cards and Manage Presets link; link from QuickSettingsPanel.
```

### Touch-only paths

- app/src/main/java/com/example/matrixscreen/ui/settings/**

### Acceptance

- Nav functional; SettingsHome accessible

## Session 3 — Overlay Previews & Accessibility

### System prompt

```
Same rules.
```

### Task prompt

```
Add previews and a11y labels/focus for buttons and controls.
```

### Touch-only paths

- app/src/main/java/com/example/matrixscreen/ui/overlay/**

### Acceptance

- Previews compile; a11y labels present
