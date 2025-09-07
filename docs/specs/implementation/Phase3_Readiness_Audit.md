# Phase 2 → Phase 3 Readiness Audit

**Date:** 2025-09-07 18:14

## Summary

Phase 2 delivered the spec-driven system, ViewModel with draft/confirm, DataStore proto, overlay & home scaffolds.  
Before Phase 3 (overlay + quick panel), the following alignment is needed:

### 1) Specs Alignment
- The current `SettingsSpecs.kt` includes keys that don't exist in `SettingId`/`MatrixSettings` yet.
- **Unmapped spec keys (16):** `advancedColorsEnabled, colorTint, columnRestartDelay, columnStartDelay, flickerRate, initialActivePercentage, maxBrightTrailLength, maxTrailLength, rainBrightTrailColor, rainDimTrailColor, rainHeadColor, rainTrailColor, rowHeightMultiplier, speedVariationRate, symbolSet, uiColor`

**Rename suggestions:**
- `flickerRate` → `flickerAmount`
- `speedVariationRate` → `speedVariance`
- `initialActivePercentage` → `activePercentage`
- `rainHeadColor` → `headColor`
- `rainTrailColor` → `trailColor`
- `rainBrightTrailColor` → `brightTrailColor`
- `rainDimTrailColor` → `dimColor`

**Remain to define (either add fields + ids later, or remove from specs for now):**
- `advancedColorsEnabled`
- `colorTint`
- `columnRestartDelay`
- `columnStartDelay`
- `maxBrightTrailLength`
- `maxTrailLength`
- `rowHeightMultiplier`
- `symbolSet`
- `uiColor`

> To avoid binding failures now, use the provided `SettingsSpecsAligned.kt` (only includes mapped keys).

### 2) Quick Panel
- Added: `ui/quick/QuickPanelSpecs.kt` and `QuickSettingsPanel.kt` (curated 6 controls).
- These use only keys that already exist in your model.

### 3) Presets & Symbols (for later phases)
- Repos/IO are not required for Phase 3, so not included here to keep changes minimal.

---

## Action Items for Agents

1. **Use `SettingsSpecsAligned.kt` for rendering until the missing fields are implemented** (or update your existing specs to match).
2. **Integrate `QuickSettingsPanel` into the overlay** and wire category buttons to your nav routes.
3. When implementing future settings (Timing, advanced Theme, Symbols), add the new fields to:
   - `MatrixSettings`
   - `SettingId`
   - `Bindings.get/with`
   - Then re-expand the specs list accordingly.
