plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
    id("com.google.protobuf") version "0.9.4"
    id("com.google.dagger.hilt.android") version "2.48"
    id("kotlin-kapt")
}

kapt {
    correctErrorTypes = true
}

android {
    namespace = "com.example.matrixscreen"
    
    // Target SDK 35 - Android 16, released June 2025
    // Provides access to latest Android features and optimizations
    compileSdk = 35
    
    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.matrixscreen"
        
        // Minimum SDK 21 - Android 5.0, provides broad device compatibility
        minSdk = 21
        
        // Target SDK 35 - Android 16, ensures app uses latest platform features
        targetSdk = 35
        
        versionCode = 2
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        
        // Generate BuildConfig class
        buildConfigField("boolean", "DEBUG", "true")
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            buildConfigField("boolean", "DEBUG", "true")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            testProguardFiles("proguard-test-rules.pro")
            buildConfigField("boolean", "DEBUG", "false")
        }
    }
    
    compileOptions {
        // Java 17 - Required for Gradle 9.0+ and latest Android tooling
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        // JVM target 17 - Aligns with compile options for consistency
        jvmTarget = "17"
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        // Kotlin Compiler Extension 1.5.8 - Stable with Kotlin 1.9.22
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.24.4"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
                create("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

dependencies {
    // Compose BOM 2024.10.01 - Stable and compatible with Kotlin 1.9.22
    // Manages all Compose library versions for compatibility
    implementation(platform("androidx.compose:compose-bom:2024.10.01"))
    
    // Core Compose UI components
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    
    // Material 3 Design System - Latest Material You components
    implementation("androidx.compose.material3:material3")
    
    // Material Icons Extended - For CloudUpload/CloudDownload icons
    implementation("androidx.compose.material:material-icons-extended")
    
    // Activity Compose integration - Enables Compose in Activities
    implementation("androidx.activity:activity-compose:1.9.2")
    
    // Navigation Compose - For screen navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")

    // Compose Foundation Pager for horizontal category swipes
    implementation("androidx.compose.foundation:foundation")
    
    // DataStore - For settings persistence (modern replacement for SharedPreferences)
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.datastore:datastore:1.0.0")
    
    // Protocol Buffers - For typed settings storage
    implementation("com.google.protobuf:protobuf-javalite:3.24.4")
    implementation("com.google.protobuf:protobuf-kotlin-lite:3.24.4")
    
    // Hilt - Dependency injection
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    
    // Kotlinx Serialization - For JSON serialization of custom symbol sets
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    
    
    // Core Android libraries
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
    
    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("app.cash.turbine:turbine:1.0.0")
    testImplementation("com.google.dagger:hilt-android-testing:2.48")
    testImplementation("org.mockito:mockito-core:5.1.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    kaptTest("com.google.dagger:hilt-compiler:2.48")
    
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    
    // Be explicit about JUnit 4 for annotations like @RunWith and @Rule
    androidTestImplementation("junit:junit:4.13.2")
    
    // Coroutines test utilities used in androidTest
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    
    // Mockito for instrumentation (tests use @Mock, etc.)
    androidTestImplementation("org.mockito:mockito-android:5.1.1")
    androidTestImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    
    // Debug tools
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

// CI tasks for Phase 1
tasks.register("ciCheck") {
    group = "verification"
    description = "Run all CI checks: assemble, test, and static analysis"
    dependsOn("assembleDebug", "testDebugUnitTest")
    
    doLast {
        println("✅ CI Check completed successfully!")
        println("  - Build: ✅ SUCCESS")
        println("  - Unit Tests: ✅ SUCCESS")
        println("  - Static Analysis: ⚠️  WARN-ONLY (not configured)")
    }
}

tasks.register("ciCheckRelease") {
    group = "verification"
    description = "Run all CI checks for release build"
    dependsOn("assembleRelease", "testReleaseUnitTest")
    
    doLast {
        println("✅ Release CI Check completed successfully!")
        println("  - Release Build: ✅ SUCCESS")
        println("  - Release Unit Tests: ✅ SUCCESS")
    }
}

tasks.register("noLegacyMatrixSettingsCheck") {
    group = "verification"
    description = "Fail if legacy com.example.matrixscreen.data.MatrixSettings is referenced"
    doLast {
        val forbidden = "import com.example.matrixscreen.data.MatrixSettings"
        val offenders = fileTree("src") {
            include("**/*.kt")
            exclude("**/SettingsViewModel.kt") // Legacy ViewModel still uses legacy repository
        }.files.filter { it.readText().contains(forbidden) }
        if (offenders.isNotEmpty()) {
            error("Legacy MatrixSettings import found in:\n" + offenders.joinToString("\n") { it.path })
        }
    }
}

tasks.register("forbidStarGet") {
    group = "verification"
    description = "Fail if currentSettings.get(spec.id) pattern is used (should use typed SettingId)"
    doLast {
        val src = fileTree("src/main/java") { include("**/*.kt") }
        val offenders = src.files.filter { it.readText().contains("get(spec.id)") }
        if (offenders.isNotEmpty()) {
            error("Do not call currentSettings.get(spec.id); use typed SettingId. Offenders:\n" +
                  offenders.joinToString("\n") { "- ${it.path}" })
        }
    }
}

tasks.register("noPreferencesStoreInMain") {
    group = "verification"
    description = "Fail if Preferences DataStore is used in main code (should use Proto DataStore only)"
    doLast {
        val offenders = fileTree("src/main/java") {
            include("**/*.kt")
        }.files.filter { it.readText().contains("datastore.preferences") }
        if (offenders.isNotEmpty()) {
            error("Do not use Preferences DataStore in main. Offenders:\n" +
                  offenders.joinToString("\n") { "- ${it.path}" })
        }
    }
}

tasks.named("preBuild").configure { 
    dependsOn("forbidStarGet", "noPreferencesStoreInMain") 
}

tasks.named("ciCheck").configure {
    dependsOn("noLegacyMatrixSettingsCheck", "forbidStarGet", "noPreferencesStoreInMain")
}