# Phase 1 — Persistence & ViewModel

## Session 1 — Proto DataStore Skeleton & Schema

### System prompt

```
You are contributing to MatrixScreen (Android, Compose, 2025).
- Engine/shaders are sacred; do not modify them.
- Material 3 only; icons via Material Symbols or approved vectors.
- Settings are spec-driven (future phases) but this session is persistence-only.
- Use Proto DataStore for MatrixSettings with versioning.
- UI never writes storage directly; ViewModel will own draft/commit later.
- Do not edit docs in docs/standards/original; dev notes go in docs/standards/dev/ as dev_<name>_<YYMMDD>_<HHMM>.md (UTC).
- Touch only the paths listed in the task.
```

### Task prompt

```
Goal: Add Proto DataStore for MatrixSettings with a versioned proto schema and basic repository interface.
Create:
1) data/proto/matrix_settings.proto — include all known fields (floats/ints/bools/colors as int64), and an int32 schema_version.
2) data/store/ProtoModule.kt — DataStore<MatrixSettingsProto> provider (filename: matrix_settings.pb).
3) data/repo/SettingsRepository.kt — interface + default implementation using the Proto DataStore:
   - fun observe(): Flow<MatrixSettings>
   - suspend fun save(settings: MatrixSettings)
   - mapping between proto and domain model.
4) data/model/MatrixSettings.kt — immutable Kotlin data class mirroring proto (domain layer model).
5) data/model/Mappers.kt — proto <-> domain mapping.

No UI code. No engine edits. Provide KDoc on public APIs.
```

### Touch-only paths

- app/src/main/proto/**
- app/src/main/java/com/example/matrixscreen/data/**

### Acceptance

- Gradle assemble succeeds
- matrix_settings.proto exists with schema_version
- SettingsRepository.observe() emits defaults
- No UI/engine changes

## Session 2 — Repository Defaults & Mapping Validations

### System prompt

```
(same as previous session)
```

### Task prompt

```
Goal: Fill out sensible defaults and harden mapping.
1) Define default MatrixSettings values in data/model/MatrixSettings.kt.
2) Ensure SettingsRepository.observe() emits defaults on first run.
3) Implement robust proto <-> domain mapping in data/model/Mappers.kt:
   - Clamp out-of-range proto values to defaults (temporary clamp; full clamp use-case comes later).
   - Backward-compat guard: if a newer proto field is missing, default it.

Add simple unit tests:
- data/repo/SettingsRepositoryTest.kt — default emission, save round-trip, and mapping sanity.
Use kotlinx.coroutines test APIs and Turbine.
```

### Touch-only paths

- app/src/main/java/com/example/matrixscreen/data/**
- app/src/test/java/com/example/matrixscreen/data/**

### Acceptance

- Unit tests pass
- Defaults are emitted; saving updates the store
- Mapping clamps invalid values to safe defaults

## Session 3 — Legacy Migration Stub (Preferences → Proto)

### System prompt

```
(same as previous session)
```

### Task prompt

```
Goal: Add a DataStore migration pathway.
1) Implement a DataMigration<MatrixSettingsProto> in data/store/ProtoModule.kt:
   - read legacy SharedPreferences (if present) and map known keys to proto fields;
   - if not present, no-op with defaults.
2) Document mapping decisions in docs/standards/dev/dev_DataMigration_<YYMMDD>_<HHMM>.md (UTC).

Add test:
- data/store/ProtoMigrationTest.kt — verifies default/no-op when absent and value mapping when present.
```

### Touch-only paths

- app/src/main/java/com/example/matrixscreen/data/store/**
- app/src/test/java/com/example/matrixscreen/data/store/**
- docs/standards/dev/**

### Acceptance

- Migration compiles and runs in tests
- No changes to engine/UI
- Dev note created in docs/standards/dev/

## Session 4 — SettingsViewModel (Draft/Confirm/Cancel)

### System prompt

```
(same as previous session; ViewModel owns UiState(saved,draft,dirty))
```

### Task prompt

```
Goal: Implement a SettingsViewModel that exposes UDF state.
Create:
1) ui/SettingsViewModel.kt (package: com.example.matrixscreen.ui):
   data class SettingsUiState(val saved: MatrixSettings, val draft: MatrixSettings, val dirty: Boolean)
   class SettingsViewModel @Inject constructor(repo: SettingsRepository, dispatcher: CoroutineDispatcher) : ViewModel() {
         val state: StateFlow<SettingsUiState>
         fun <T> updateDraft(id: Any, value: Any) // placeholder signature in Phase 1
         fun commit()
         fun revert()
   }
For now, implement updateDraft with explicit field setters on MatrixSettings (generic SettingId arrives in Phase 2).
2) Expose a DI-provided dispatcher (core module).
3) Unit tests: initial state, updateDraft sets dirty, commit persists, revert resets.
```

### Touch-only paths

- app/src/main/java/com/example/matrixscreen/ui/SettingsViewModel.kt
- app/src/main/java/com/example/matrixscreen/core/**
- app/src/test/java/com/example/matrixscreen/ui/**

### Acceptance

- Unit tests pass
- No UI composables added
- VM compiles and is injected via Hilt in tests

## Session 5 — Hilt DI Wiring

### System prompt

```
(same as previous session)
```

### Task prompt

```
Goal: Provide DI modules for repository and DataStore.
1) di/DataModule.kt — @Module @InstallIn(SingletonComponent::class):
   - @Provides @Singleton DataStore<MatrixSettingsProto>
   - @Provides @Singleton SettingsRepository
   - @Provides @IoDispatcher CoroutineDispatcher (Dispatchers.IO)
2) Annotate SettingsViewModel with @HiltViewModel and @Inject constructor.
3) Add Hilt test runner config (androidTest) stub for later.
```

### Touch-only paths

- app/src/main/java/com/example/matrixscreen/di/**
- app/src/main/java/com/example/matrixscreen/ui/**
- app/src/androidTest/**

### Acceptance

- App builds; Hilt graph resolves
- ViewModel obtainable via Hilt

## Session 6 — Minimal Debug Harness (Dev-only)

### System prompt

```
(same as previous session; dev-only)
```

### Task prompt

```
Goal: Add a minimal debug Composable to verify persistence & VM end-to-end.
ui/preview/DebugSettingsHarness.kt: read VM.state (saved/draft), show 2–3 fields with sliders, buttons for Confirm/Cancel.
Guard with BuildConfig.DEBUG and keep out of production nav.
```

### Touch-only paths

- app/src/main/java/com/example/matrixscreen/ui/preview/**
- app/src/main/java/com/example/matrixscreen/MainActivity.kt

### Acceptance

- Moving sliders updates draft; Confirm persists, Cancel reverts
- Release build unaffected

## Session 7 — CI Test Gate & Static Checks (Phase 1 scope)

### System prompt

```
(same as previous session)
```

### Task prompt

```
Goal: Add local gates to ensure Phase 1 stays healthy.
1) Aggregate tasks to run assemble + unit tests.
2) Wire warn-only detekt/ktlint.
3) Add dev CI doc in docs/standards/dev/ with how-to-run.
```

### Touch-only paths

- build.gradle.kts
- app/build.gradle.kts
- docs/standards/dev/**

### Acceptance

- Assemble/test tasks run
- Lint tools run (warn-only)
- Dev CI doc present
