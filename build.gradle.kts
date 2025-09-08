// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Android Gradle Plugin 8.7.2 - Stable and compatible with current setup
    // Provides support for API level 35 and stable build optimizations
    id("com.android.application") version "8.7.2" apply false
    
    // Kotlin 1.9.22 - Compatible with Compose Compiler 1.5.8
    // Stable version that works with current setup
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    
    // Hilt - Dependency injection
    id("com.google.dagger.hilt.android") version "2.48" apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

/**
 * MatrixScreen Guardrails Reminder Task
 * 
 * Prints key architectural guardrails and standards to help developers
 * and AI agents stay within established boundaries.
 */
tasks.register("guardrails") {
    group = "matrixscreen"
    description = "Print MatrixScreen architectural guardrails and standards"
    
    doLast {
        println("=".repeat(80))
        println("MATRIXSCREEN GUARDRAILS & STANDARDS")
        println("=".repeat(80))
        println()
        
        println("🔒 ARCHITECTURE INVARIANTS (MUST ALWAYS HOLD):")
        println("  • Engine sacred: renderer, shaders, column logic remain intact")
        println("  • Spec-driven settings: all settings declared as data specs")
        println("  • UDF pattern: ViewModel exposes UiState(saved, draft, dirty)")
        println("  • Proto DataStore: versioned, typed MatrixSettings")
        println("  • Material 3 only: androidx.compose.material3.* components")
        println()
        
        println("🎨 COMPOSE RULES:")
        println("  • SettingsSection & SettingsScreenContainer MUST use trailing lambdas")
        println("  • No references to ui.surface (use ui.selectionBackground or ui.overlayBackground)")
        println("  • Components accept modifier: Modifier = Modifier")
        println("  • Stateless components; state hoisted")
        println()
        
        println("📁 PROTECTED PATHS (DO NOT MODIFY):")
        println("  • app/src/main/java/com/example/matrixscreen/renderer/")
        println("  • app/src/main/java/com/example/matrixscreen/shaders/")
        println("  • app/src/main/java/com/example/matrixscreen/columns/")
        println()
        
        println("📚 DOCUMENTATION STANDARDS:")
        println("  • Never edit docs/standards/original/ files")
        println("  • Copy to docs/standards/dev/ for annotations")
        println("  • Use format: dev_<origdocname>_<YYMMDD>_<HHMM>.md")
        println()
        
        println("🏷️ NAMING CONVENTIONS:")
        println("  • No marketing adjectives in code (drop 'modern', etc.)")
        println("  • Use descriptive, stable names")
        println("  • KDoc public APIs; descriptive filenames")
        println()
        
        println("📊 REPOSITORY MAP:")
        println("  • See scripts/repo_map.json for key paths and architecture")
        println("  • Overlay: QuickSettingsPanel (bottom dock)")
        println("  • Full settings: SettingsHome + category screens")
        println("  • Categories: Theme, Characters, Motion, Effects, Timing, Background")
        println()
        
        println("✅ PR ACCEPTANCE CRITERIA:")
        println("  • Build & tests pass")
        println("  • Only allowed paths changed")
        println("  • Guardrails obeyed")
        println("  • Screenshots for UI changes")
        println()
        
        println("=".repeat(80))
        println("For detailed specs, see docs/standards/original/")
        println("=".repeat(80))
    }
}