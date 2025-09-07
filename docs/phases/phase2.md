# Phase 2 — Spec System (Settings as Elements)

## Session 1 — SettingId<T> + Bindings

### System prompt

```
You are contributing to MatrixScreen (Android, Compose, 2025).
- Do not modify engine/shaders.
- Implement typed SettingId<T> keys and centralized Bindings mapping MatrixSettings <-> ids.
- Material 3 only, no M2. Trailing lambdas policy. No ui.surface usage.
- Do not edit docs/original; dev notes go to docs/standards/dev/ as dev_<name>_<YYMMDD>_<HHMM>.md (UTC).
- Touch only the paths listed in the task.
```

### Task prompt

```
Goal: Introduce typed SettingId<T> and a single binding point for MatrixSettings mapping.

Create:
1) ui/settings/model/SettingId.kt — sealed interface SettingId<T> with concrete ids for core fields.
2) ui/settings/model/Bindings.kt — fun <T> MatrixSettings.get(id) / with(id, value) mapping all ids.
3) tests/ui/settings/BindingsTest.kt — round-trip tests and coverage (every id handled).

Notes: Colors are Long; keep it. No UI components yet.
```

### Touch-only paths

- app/src/main/java/com/example/matrixscreen/ui/settings/model/**
- app/src/test/java/com/example/matrixscreen/ui/settings/**

### Acceptance

- Build & tests pass
- All ids in SettingId.kt are covered in get/with
- No changes outside allowed paths

## Session 2 — WidgetSpec family (Slider/IntSlider/Toggle/Color/Select)

### System prompt

```
(same as previous session)
```

### Task prompt

```
Goal: Define the spec types that describe settings elements as data.
Create ui/settings/model/WidgetSpec.kt with sealed WidgetSpec<T> and data classes for SliderSpec, IntSliderSpec, ToggleSpec, ColorSpec, SelectSpec.
Add sanity tests ensuring defaults in range and labels non-blank (can be minimal until concrete specs land in Session 5).
```

### Touch-only paths

- app/src/main/java/com/example/matrixscreen/ui/settings/model/**
- app/src/test/java/com/example/matrixscreen/ui/settings/**

### Acceptance

- Compiles
- Sanity tests scaffolded/passing

## Session 3 — Base UI components (stateless rows)

### System prompt

```
(same as previous session)
```

### Task prompt

```
Goal: Implement reusable, stateless building blocks to render specs.
Create LabeledSlider, LabeledIntSlider, LabeledSwitch, ColorControlRow, OptionChips, ResetSectionButton in ui/settings/components/.
Add simple previews under ui/preview/components/ using fake settings/theme.
```

### Touch-only paths

- app/src/main/java/com/example/matrixscreen/ui/settings/components/**
- app/src/main/java/com/example/matrixscreen/ui/preview/components/**

### Acceptance

- Components compile with previews
- Stateless; callbacks hoisted

## Session 4 — RenderSetting (generic) + Section helpers

### System prompt

```
(same as previous session; trailing lambdas enforced)
```

### Task prompt

```
Goal: Map WidgetSpec<T> -> base components in a single renderer.
Create RenderSetting(spec, state, onChange, ui, optimized) and SettingsSection/SettingsScreenContainer wrappers (Material 3 surfaces). Previews use trailing lambdas.
```

### Touch-only paths

- app/src/main/java/com/example/matrixscreen/ui/settings/components/**
- app/src/main/java/com/example/matrixscreen/ui/preview/**

### Acceptance

- RenderSetting handles all spec types
- Section wrappers exist and used with trailing lambdas

## Session 5 — SpecsCatalog (category lists) + sanity tests

### System prompt

```
(same as previous session)
```

### Task prompt

```
Goal: Provide initial spec lists per category with sensible defaults/ranges:
- MOTION_SPECS, EFFECTS_SPECS, BACKGROUND_SPECS, TIMING_SPECS, CHARACTERS_SPECS, THEME_SPECS.
Add tests ensuring ids exist, defaults in range, unique keys.
```

### Touch-only paths

- app/src/main/java/com/example/matrixscreen/ui/settings/model/**
- app/src/test/java/com/example/matrixscreen/ui/settings/**

### Acceptance

- Catalog compiles
- Sanity tests pass

## Session 6 — Upgrade ViewModel to generic updateDraft(id, value)

### System prompt

```
(same as previous session)
```

### Task prompt

```
Goal: Make the ViewModel spec-aware.
Replace placeholder setters with generic updateDraft(id, value) using Bindings.with(). Add tests for representative ids.
```

### Touch-only paths

- app/src/main/java/com/example/matrixscreen/ui/SettingsViewModel.kt
- app/src/test/java/com/example/matrixscreen/ui/**

### Acceptance

- Tests pass; VM supports generic updates

## Session 7 — Unified Color Picker Dialog (hook for ColorSpec)

### System prompt

```
(same as previous session)
```

### Task prompt

```
Goal: Provide a minimal, reusable color picker dialog for ColorSpec.
Create ColorPickerDialog (RGB sliders + Hex), ColorUtils, and extend ColorControlRow to open it; previews validate selection.
```

### Touch-only paths

- app/src/main/java/com/example/matrixscreen/ui/settings/components/**
- app/src/main/java/com/example/matrixscreen/core/util/**
- app/src/main/java/com/example/matrixscreen/ui/preview/**

### Acceptance

- Dialog opens and returns Long color in preview

## Session 8 — Select renderer (OptionChips) refinements

### System prompt

```
(same as previous session)
```

### Task prompt

```
Goal: Finish SelectSpec rendering with accessible states and dropdown fallback for long lists; preview with FPS options [15,30,45,60,90,120].
```

### Touch-only paths

- app/src/main/java/com/example/matrixscreen/ui/settings/components/**
- app/src/main/java/com/example/matrixscreen/ui/preview/**

### Acceptance

- Select behaves correctly and accessibly in previews

## Session 9 — Dev docs for Spec System (copy-only)

### System prompt

```
Do not edit originals; create dev copy in docs/standards/dev/.
```

### Task prompt

```
Goal: HOWTO for adding a new setting via Spec System.
Create docs/standards/dev/dev_SpecSystem_HOWTO_<YYMMDD>_<HHMM>.md describing ids/specs/bindings/tests and guardrails.
```

### Touch-only paths

- docs/standards/dev/**

### Acceptance

- Dev doc exists; originals untouched
