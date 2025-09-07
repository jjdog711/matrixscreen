# MatrixScreen — Product & Tech Intent (2025)

**Updated:** 2025-09-07 15:00 UTC

## Vision
Deliver a **playable visual instrument** for Android that feels like a sleek **Matrix console**. Users **perform** the rain by tuning motion, symbols, timing, colors, and effects. Every change responds **instantly** on a living background. The result is a calm, cinematic, endlessly tweakable “toy” that’s fun enough to keep, polished enough to ship, and modular enough to evolve.

## Player Experience
- **Immediate feedback:** Adjust → Observe → Refine in a single flow. The rain is always visible and reactive.
- **Two layers of control:**  
  - **Quick Settings Panel** (bottom dock): small, friendly set of essentials for casual play.  
  - **Full Settings**: deep, organized screens for power users, with per-setting advanced pages where it makes sense.
- **Presets as loadouts:** One tap to meaningful, named looks; easy to share, import, and export.
- **Creative ownership:** Players can theme the UI and the rain to match—basic tints for simplicity, advanced per-channel color for precision.

## Product Principles
- **Keep the engine sacred:** The renderer, shaders, and column logic remain intact (refactors only to surface parameters cleanly). All change happens in the UI/control layer.
- **Modularity first:** Every setting is a **drop-in element**. The overlay and full settings screens can be rearranged or extended without rewiring.
- **Data over code:** Settings, ranges, defaults, and presets are **expressed as data**, not scattered constants.
- **Store-ready polish:** Stable, consistent, accessible, and discoverable. Feels premium from first run to export.

## UI / UX Principles
- **Instrument panel, not forms:** Bold, minimal, responsive controls. Micro-animations that communicate function (glow glows, jitter wiggles, etc.).
- **Overlay that respects play:** A compact **Quick Settings Panel** anchored to the bottom—think smart TV or game console—never blocking the scene.
- **Clear organization:** Categories (Theme, Characters, Motion, Effects, Timing, Background) read like tracks on a mixing board.
- **Progressive disclosure:** Simple first; **Advanced** on demand (e.g., per-channel color pickers).
- **Naming sanity:** No adjectives in file names (e.g., drop “modern”). Use descriptive, stable names for files, packages, routes, and components.

## Theming & Color
- **Two coordinated palettes:**  
  - **Renderer Colors:** Head / Bright Trail / Trail / Dim / Background.  
  - **UI Theme:** overlayBackground, selectionBackground, textPrimary/Secondary, accent.
- **Basic & Advanced modes:** “Link UI & Rain colors” optional toggle (off by default). Basic hue maps sensibly across both layers; advanced offers precise per-channel edits.
- **Contrast guardrails:** Automatically maintain legibility for on-surface text and indicators.

## Settings Model
- **Spec-driven elements:** Each setting is defined by a typed **spec** (slider, toggle, color, select). Screens render **lists of specs**—reorder, move, or reuse without rewriting UI.
- **Draft / Confirm / Cancel:** UI edits update a **draft** immediately; Confirm persists, Cancel reverts. No direct persistence from composables.
- **Advanced deep links:** Any basic control can link into its detailed screen, preserving draft state.

## Data & Persistence
- **Proto DataStore:** Typed, versioned storage for `MatrixSettings`; migration-ready defaults.
- **Presets as data:** Built-ins shipped in-app; user presets can be **exported/imported**.
- **Import/Export:** JSON with versioning and checksums via standard Android document pickers; shareable and future-proof.

## Characters & Symbols
- **Integrated editor:** The symbol-set editor lives under Characters and follows the same modular, spec-driven patterns.
- **Curated and custom:** Choose curated sets or author your own; export/import symbol collections as JSON.

## Performance & Devices
- **FPS targets:** User-selectable **5–120 FPS**, default 60; requests are **safely coerced** to the device’s supported rates.
- **No premature heuristics:** We won’t show power warnings until we have real measurements; keep the path open for future profiling.

## Accessibility & Localization
- **English first, multilingual ready:** Centralized strings; clean layout scaling.
- **Keyboard & screen reader friendliness:** Focusable controls, content descriptions, contrast-aware color choices.

## Quality Bar
- **Consistency:** Material 3 components and a unified token set for spacing, radii, elevation, outlines, and focus.
- **Reliability:** Unit tests for presets/clamping/bindings; Compose tests for draft/confirm/cancel, color dialogs, and FPS coercion.
- **Maintainability:** No hidden state, no duplicated ranges/defaults, no UI-owned persistence.

## Collaboration with AI Agents
- **Strict guardrails:** Agents work **only** within declared scopes, follow established definitions and patterns, and must pass acceptance checks (build/tests/style).
- **Data-first contributions:** New settings and screens are added by extending **spec lists**, not by ad-hoc UI code.
- **Clear documentation:** Short contributor guide and a repository map ensure low-context agents still operate safely and predictably.

## Naming & Organization
- **Overlay:** `QuickSettingsPanel` (bottom dock)  
- **Full settings:** `SettingsHome` + category screens under `ui/settings/*`  
- **Engine untouched:** Renderer and timing remain, with optional parameter mapping to expose clean inputs  
- **Design tokens:** Centralized; UI look is consistent and adjustable from one place

## What We Ship
- A **feature-complete**, **stable**, **performant** interactive visual “game” that feels premium and is ready for the Play Store.
