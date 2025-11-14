# Future Work Tracker

This document captures outstanding tasks that are **not** part of the current ship scope but need to stay visible for the next iteration.

## Advanced Colors & Linking
- Rewrite the advanced color pipeline so UI previews, renderer colors, and linking logic all derive from a single source of truth.
- Add guardrails (contrast checks, clamped alpha) before committing user-selected colors.
- Provide clearer UI affordances when linking forces UI colors (e.g., contextual info under disabled controls).

## UI Component Consolidation
- Perform one more audit pass to ensure no duplicate component implementations remain outside `ui/settings/components/`.
- Normalize imports after the audit (enforce zero wildcard threshold in ktlint/spotless).

## Flow Direction Controls
- Implement the `Flow Direction` feature referenced in `TimingSettingsScreen.kt` and the renderer (center-out, reverse, etc.).
- Add corresponding specs (`SettingId.FlowDirection`) plus renderer plumbing and persistence.

## Custom Font Import (SAF)
- Current custom symbol sets are limited to bundled fonts. Add SAF-backed import/export of `.ttf/.otf` files with validation and caching.
- Document font licensing and sandbox behavior once implemented.

## Dependency Upgrades
- Kotlin 2.0.x (K2 compiler), Compose BOM ≥ 2025.01, Compose Compiler aligned with Kotlin, Hilt ≥ 2.51, DataStore 1.1.x.
- Re-run `./gradlew test assembleDebug` after each upgrade and capture release notes.

## Testing & QA
- Add instrumentation coverage for preset application, theme linking, and custom symbol-set import/export flows.
- Keep a manual QA checklist for at least one 2023-era device (per user request) covering overlay interactions, presets, and custom set editing.

## Documentation Hygiene
- Continue archiving outdated docs under `docs/ui/` once superseded, and keep this tracker as the canonical list of deferred work.

