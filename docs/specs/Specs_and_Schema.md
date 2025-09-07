# Specs & Schema — MatrixScreen

**Updated:** 2025-09-07 15:00 UTC

## SettingId & WidgetSpec
- `SettingId<T>`: typed keys for settings.
- `WidgetSpec`: data-only description of a UI element (Slider/IntSlider/Toggle/Color/Select).

## Bindings
- Single source of truth: `MatrixSettings.get(id)` / `MatrixSettings.with(id, value)`.

## SpecsCatalog (per category)
- `MOTION_SPECS`, `EFFECTS_SPECS`, `BACKGROUND_SPECS`, `TIMING_SPECS`, `CHARACTERS_SPECS`, `THEME_SPECS`.
- Ranges/defaults live **only** in specs (never duplicated in UI).

## Import/Export Formats
- JSON envelopes with `type`, `schema_version`, `app_version`, `created`, `payload`, `checksum`.
- Extensions: `.ms-preset.json`, `.ms-theme.json`, `.ms-symbols.json`.
- Android **SAF** for open/save (persist URI).

## FPS Coercion
- Accept requested FPS; compute effective FPS by nearest supported refresh rate within [5..120]; default 60.

## Theming
- Renderer: Head / Bright Trail / Trail / Dim / Background.
- UI: overlayBackground, selectionBackground, textPrimary/Secondary, accent.
- Optional “Link UI & Rain colors” toggle (off by default). Contrast guards for legibility.
