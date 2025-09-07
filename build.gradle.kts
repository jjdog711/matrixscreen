// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Android Gradle Plugin 8.7.2 - Stable and compatible with current setup
    // Provides support for API level 35 and stable build optimizations
    id("com.android.application") version "8.7.2" apply false
    
    // Kotlin 1.9.22 - Compatible with Compose Compiler 1.5.8
    // Stable version that works with current setup
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
