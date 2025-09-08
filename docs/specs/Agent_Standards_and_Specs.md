# MatrixScreen — Agent Standards & Specs (2025)

**Updated:** 2025-09-07 15:00 UTC

> **Do not edit this or any file in `docs/standards/original/`.**  
> If you need to annotate/propose changes, **copy** into `docs/standards/dev/` as:
> `dev_<origdocname>_<YYMMDD>_<HHMM>.md` (UTC). Originals and dev copies must live in separate folders.

## Architecture Invariants (must always hold)
- **Engine sacred:** renderer, shaders, column logic remain functionally intact. UI controls it via params only.
- **Spec-driven settings:** all settings are declared as data specs; UI renders specs (no ad-hoc wiring).
- **UDF:** ViewModel exposes `UiState(saved, draft, dirty)`. UI edits **draft**, `Confirm()` persists, `Cancel()` reverts. No composable touches storage.
- **Proto DataStore:** versioned, typed `MatrixSettings` (+ migrations). Presets/themes/symbol sets are data, import/exportable.
- **Material 3 only:** `androidx.compose.material3.*` components; icons via Material Symbols or vetted vectors.
- **Compose rules:**  
  - `SettingsSection` & `SettingsScreenContainer` **must use trailing lambdas** (no `content = {}`)  
  - No references to `ui.surface` (use `ui.selectionBackground` or `ui.overlayBackground`).
- **Naming sanity:** No marketing adjectives in code. Use descriptive, stable names.
- **Single source of truth for settings:**
  - Use `com.example.matrixscreen.data.model.MatrixSettings` ONLY.
  - The legacy `com.example.matrixscreen.data.MatrixSettings` must not appear in code.
  - `MatrixColor` and `SymbolSet` live in `com.example.matrixscreen.data` as enums.
- **Theme must consume the domain model:**
  - `MatrixUIColorScheme`/`GlowEffects`/`PerformanceOptimizations` helpers accept domain `MatrixSettings` and use explicit ARGB fields
  - Use: `backgroundColor`, `headColor`, `brightTrailColor`, `trailColor`, `dimColor`, `uiAccent`, `uiOverlayBg`, `uiSelectionBg`.

## UI/UX Invariants
- **QuickSettingsPanel** (bottom dock) + **Full Settings** (SettingsHome + categories; deep links to advanced per setting)
- **Categories:** Theme, Characters, Motion, Effects, Timing, Background.
- **Immediate feedback** on draft changes.
- **Theming:** fully customizable; Basic tint + Advanced per-channel; optional “Link UI & Rain colors” toggle (default off).
- **FPS:** 5–120 target; **coerce** to device-supported rates (default 60).

## Settings Spec — Canonical Definitions
```kotlin
sealed interface SettingId<T> { val key: String }

object Speed      : SettingId<Float> { override val key = "fallSpeed" }
object Columns    : SettingId<Int>   { override val key = "columnCount" }
object LineSpace  : SettingId<Float> { override val key = "lineSpacing" }
object ActivePct  : SettingId<Float> { override val key = "activePercentage" }
object SpeedVar   : SettingId<Float> { override val key = "speedVariance" }
object Glow       : SettingId<Float> { override val key = "glowIntensity" }
object Jitter     : SettingId<Float> { override val key = "jitterAmount" }
object Flicker    : SettingId<Float> { override val key = "flickerAmount" }
object Mutation   : SettingId<Float> { override val key = "mutationRate" }
object GrainD     : SettingId<Int>   { override val key = "grainDensity" }
object GrainO     : SettingId<Float> { override val key = "grainOpacity" }
object Fps        : SettingId<Int>   { override val key = "targetFps" }
object BgColor    : SettingId<Long>  { override val key = "backgroundColor" }
object HeadColor  : SettingId<Long>  { override val key = "headColor" }
object BrightColor: SettingId<Long>  { override val key = "brightTrailColor" }
object TrailColor : SettingId<Long>  { override val key = "trailColor" }
object DimColor   : SettingId<Long>  { override val key = "dimColor" }
object UiAccent   : SettingId<Long>  { override val key = "uiAccent" }
object UiOverlay  : SettingId<Long>  { override val key = "uiOverlayBg" }
object UiSelectBg : SettingId<Long>  { override val key = "uiSelectionBg" }
object FontSize   : SettingId<Int>   { override val key = "fontSize" }
```

```kotlin
sealed interface WidgetSpec<T> {
  val id: SettingId<T>
  val label: String
  val help: String?
}

data class SliderSpec(
  override val id: SettingId<Float>,
  override val label: String,
  val range: ClosedFloatingPointRange<Float>,
  val step: Float,
  val default: Float,
  val unit: String? = null,
  val affectsPerf: Boolean = false,
  override val help: String? = null
) : WidgetSpec<Float>

data class IntSliderSpec(
  override val id: SettingId<Int>,
  override val label: String,
  val range: IntRange,
  val step: Int = 1,
  val default: Int,
  val unit: String? = null,
  val affectsPerf: Boolean = false,
  override val help: String? = null
) : WidgetSpec<Int>

data class ToggleSpec(
  override val id: SettingId<Boolean>,
  override val label: String,
  val default: Boolean,
  override val help: String? = null
) : WidgetSpec<Boolean>

data class ColorSpec(
  override val id: SettingId<Long>,
  override val label: String,
  override val help: String? = null
) : WidgetSpec<Long>

data class SelectSpec<T : Any>(
  override val id: SettingId<T>,
  override val label: String,
  val options: List<T>,
  val toLabel: (T) -> String,
  val default: T,
  override val help: String? = null
) : WidgetSpec<T>

fun <T> MatrixSettings.get(id: SettingId<T>): T
fun <T> MatrixSettings.with(id: SettingId<T>, value: T): MatrixSettings
```

## Import/Export Schemas (JSON, versioned)
```json
{
  "type": "preset|theme|symbols",
  "schema_version": 1,
  "app_version": "1.0.0",
  "created": "2025-09-07T12:00:00Z",
  "payload": {},
  "checksum": "sha256:<hex>"
}
```
- Presets: `.ms-preset.json`  
- Themes: `.ms-theme.json`  
- Symbol sets: `.ms-symbols.json`  
- Use Android SAF; persist URI; default to `Documents/MatrixScreen/`.

## FPS Policy
- UI range: **5–120**; default **60**; coerce to nearest device-supported refresh rate within [5..120].

## Compose & Code Standards
- Material 3 only. Trailing lambdas for `SettingsSection`/`SettingsScreenContainer`. No `ui.surface`.
- Components accept `modifier: Modifier = Modifier`. Stateless components; state hoisted.
- KDoc public APIs; descriptive filenames; no adjectives.

## PR Acceptance
- Build & tests pass; only allowed paths changed; guardrails obeyed; screenshots for UI changes.
- Originals untouched; any working notes live in `docs/standards/dev/dev_<name>_<YYMMDD>_<HHMM>.md` (UTC).
