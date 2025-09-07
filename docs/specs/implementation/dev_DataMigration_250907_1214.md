# Data Migration Documentation - SharedPreferences → Proto DataStore

**Created:** 2025-09-07 12:14 UTC  
**Session:** Phase 1, Session 3  
**Purpose:** Document migration decisions from legacy SharedPreferences to Proto DataStore

## Migration Overview

The migration from SharedPreferences to Proto DataStore is implemented as a one-time data migration that runs when the DataStore is first created. This ensures backward compatibility for existing users while providing the benefits of typed, versioned storage.

## Migration Strategy

### When Migration Occurs
- Migration runs only when:
  1. Legacy SharedPreferences exist (`matrix_screen_prefs`)
  2. No existing Proto DataStore data is present
  3. DataStore is first initialized

### Migration Process
1. **Detection**: Check for legacy SharedPreferences with data
2. **Migration**: Map legacy keys to proto fields with default fallbacks
3. **Cleanup**: Remove legacy SharedPreferences after successful migration
4. **Validation**: Proto data is validated and clamped by existing mappers

## Key Mapping Decisions

### Legacy SharedPreferences Keys → Proto Fields

| Legacy Key | Proto Field | Default Value | Notes |
|------------|-------------|---------------|-------|
| `fall_speed` | `fallSpeed` | 2.0f | Motion setting |
| `column_count` | `columnCount` | 150 | Motion setting |
| `line_spacing` | `lineSpacing` | 0.9f | Motion setting |
| `active_percentage` | `activePercentage` | 0.4f | Motion setting |
| `speed_variance` | `speedVariance` | 0.01f | Motion setting |
| `glow_intensity` | `glowIntensity` | 2.0f | Effects setting |
| `jitter_amount` | `jitterAmount` | 2.0f | Effects setting |
| `flicker_amount` | `flickerAmount` | 0.2f | Effects setting |
| `mutation_rate` | `mutationRate` | 0.08f | Effects setting |
| `grain_density` | `grainDensity` | 200 | Background setting |
| `grain_opacity` | `grainOpacity` | 0.03f | Background setting |
| `target_fps` | `targetFps` | 60 | Background setting |
| `background_color` | `backgroundColor` | 0xFF000000L | Color setting |
| `head_color` | `headColor` | 0xFF00FF00L | Color setting |
| `bright_trail_color` | `brightTrailColor` | 0xFF00CC00L | Color setting |
| `trail_color` | `trailColor` | 0xFF008800L | Color setting |
| `dim_color` | `dimColor` | 0xFF004400L | Color setting |
| `ui_accent` | `uiAccent` | 0xFF00CC00L | UI theme setting |
| `ui_overlay_bg` | `uiOverlayBg` | 0x80000000L | UI theme setting |
| `ui_selection_bg` | `uiSelectionBg` | 0x4000FF00L | UI theme setting |
| `font_size` | `fontSize` | 14 | Character setting |

### Default Values
All default values match the current `MatrixSettings.DEFAULT` values to ensure consistency. If a legacy key is missing, the corresponding default value is used.

### Data Validation
After migration, all data is validated and clamped by the existing `toDomain()` mapper, ensuring that any out-of-range legacy values are corrected to valid ranges.

## Implementation Details

### Migration Class
- **Name**: `SharedPreferencesMigration`
- **Location**: `data/store/ProtoModule.kt`
- **Scope**: Private to the module

### Key Methods
1. **`shouldMigrate()`**: Determines if migration is needed
2. **`migrate()`**: Performs the actual data migration
3. **`cleanUp()`**: Removes legacy SharedPreferences after successful migration

### Error Handling
- Migration failures are handled by the DataStore's corruption handler
- If migration fails, default proto values are used
- Legacy data is preserved until successful migration

## Testing Strategy

### Unit Tests
- Test migration with various legacy data scenarios
- Test migration with missing legacy data (no-op)
- Test migration with existing proto data (no-op)
- Test cleanup after successful migration

### Integration Tests
- Test end-to-end migration in test environment
- Verify data integrity after migration
- Test rollback scenarios

## Future Considerations

### Schema Versioning
- Current schema version: 1
- Future schema changes will require new migration classes
- Migration classes should be additive and backward-compatible

### Performance
- Migration runs only once per user
- Minimal performance impact after initial migration
- Legacy SharedPreferences are cleaned up to save space

## Rollback Plan

If issues are discovered after deployment:
1. Remove migration from DataStoreFactory
2. Users with migrated data continue using Proto DataStore
3. Users without migration continue using legacy SharedPreferences
4. Fix migration issues and redeploy

## Security Considerations

- Legacy SharedPreferences are cleared after successful migration
- No sensitive data is exposed during migration
- Migration runs in the same security context as the app
