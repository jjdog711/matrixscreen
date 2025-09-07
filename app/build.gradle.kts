plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
}

android {
    namespace = "com.example.matrixscreen"
    
    // Target SDK 35 - Android 16, released June 2025
    // Provides access to latest Android features and optimizations
    compileSdk = 35

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
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
        // Kotlin Compiler Extension 1.5.8 - Stable with Kotlin 1.9.25
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Compose BOM 2024.10.01 - Stable and compatible with Kotlin 1.9.25
    // Manages all Compose library versions for compatibility
    implementation(platform("androidx.compose:compose-bom:2024.10.01"))
    
    // Core Compose UI components
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    
    // Material 3 Design System - Latest Material You components
    implementation("androidx.compose.material3:material3")
    
    // Activity Compose integration - Enables Compose in Activities
    implementation("androidx.activity:activity-compose:1.9.2")
    
    // Navigation Compose - For screen navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")
    
    // DataStore - For settings persistence (modern replacement for SharedPreferences)
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    
    // Kotlinx Serialization - For JSON serialization of custom symbol sets
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    
    
    // Core Android libraries
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
    
    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    
    // Debug tools
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
