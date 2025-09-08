# Keep Hilt test classes from being stripped by R8
-keep class com.google.dagger.hilt.android.testing.** { *; }
-keep class dagger.hilt.android.internal.testing.** { *; }
-keep class dagger.hilt.internal.** { *; }
# Explicitly keep HiltTestRunner (belt & suspenders)
-keep class com.google.dagger.hilt.android.testing.HiltTestRunner { *; }
