package com.example.matrixscreen.domain.usecase

import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.engine.uniforms.RendererParams
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

/**
 * Unit tests for MapSettingsToRendererParams use case.
 * 
 * Tests the mapping from MatrixSettings to RendererParams, ensuring that
 * FPS coercion and parameter mapping work correctly.
 */
class MapSettingsToRendererParamsTest {
    
    private lateinit var useCase: MapSettingsToRendererParams
    
    @Before
    fun setUp() {
        useCase = MapSettingsToRendererParams()
    }
    
    @Test
    fun `execute should map all settings to renderer params`() {
        // Given
        val settings = MatrixSettings(
            // Motion settings
            fallSpeed = 2.5f,
            columnCount = 200,
            lineSpacing = 1.0f,
            activePercentage = 0.5f,
            speedVariance = 0.02f,
            
            // Effects settings
            glowIntensity = 3.0f,
            jitterAmount = 2.5f,
            flickerAmount = 0.3f,
            mutationRate = 0.1f,
            
            // Background settings
            grainDensity = 300,
            grainOpacity = 0.05f,
            targetFps = 90,
            
            // Color settings
            backgroundColor = 0xFF111111L,
            headColor = 0xFF00FF00L,
            brightTrailColor = 0xFF00CC00L,
            trailColor = 0xFF008800L,
            dimColor = 0xFF004400L,
            
            // Character settings
            fontSize = 16
        )
        
        val context = mockContextWithSupportedRates(listOf(30, 60, 90, 120))
        
        // When
        val result = useCase.execute(settings, context)
        
        // Then
        assertEquals(90f, result.effectiveFps)
        assertEquals(90f, result.targetFps)
        assertEquals(300, result.grainDensity)
        assertEquals(0.05f, result.grainOpacity)
        assertEquals(2.5f, result.fallSpeed)
        assertEquals(200, result.columnCount)
        assertEquals(1.0f, result.lineSpacing)
        assertEquals(0.5f, result.activePercentage)
        assertEquals(0.02f, result.speedVariance)
        assertEquals(3.0f, result.glowIntensity)
        assertEquals(2.5f, result.jitterAmount)
        assertEquals(0.3f, result.flickerAmount)
        assertEquals(0.1f, result.mutationRate)
        assertEquals(16f, result.fontSize)
        assertEquals(0xFF111111L, result.backgroundColor)
        assertEquals(0xFF00FF00L, result.headColor)
        assertEquals(0xFF00CC00L, result.brightTrailColor)
        assertEquals(0xFF008800L, result.trailColor)
        assertEquals(0xFF004400L, result.dimColor)
    }
    
    @Test
    fun `execute should coerce FPS to nearest supported rate`() {
        // Given
        val settings = MatrixSettings(targetFps = 75) // Not in supported rates
        val context = mockContextWithSupportedRates(listOf(30, 60, 90, 120))
        
        // When
        val result = useCase.execute(settings, context)
        
        // Then
        assertEquals(90f, result.effectiveFps) // Should be coerced to 90 (nearest)
        assertEquals(75f, result.targetFps) // Original target should be preserved
    }
    
    @Test
    fun `execute should handle FPS coercion to lower rate`() {
        // Given
        val settings = MatrixSettings(targetFps = 45) // Between 30 and 60
        val context = mockContextWithSupportedRates(listOf(30, 60, 90, 120))
        
        // When
        val result = useCase.execute(settings, context)
        
        // Then
        assertEquals(60f, result.effectiveFps) // Should be coerced to 60 (nearest)
        assertEquals(45f, result.targetFps) // Original target should be preserved
    }
    
    @Test
    fun `execute should handle FPS coercion to higher rate`() {
        // Given
        val settings = MatrixSettings(targetFps = 105) // Between 90 and 120
        val context = mockContextWithSupportedRates(listOf(30, 60, 90, 120))
        
        // When
        val result = useCase.execute(settings, context)
        
        // Then
        assertEquals(120f, result.effectiveFps) // Should be coerced to 120 (nearest)
        assertEquals(105f, result.targetFps) // Original target should be preserved
    }
    
    @Test
    fun `execute with pre-computed FPS should use provided effective FPS`() {
        // Given
        val settings = MatrixSettings(targetFps = 75)
        val effectiveFps = 90f
        
        // When
        val result = useCase.execute(settings, effectiveFps)
        
        // Then
        assertEquals(90f, result.effectiveFps) // Should use provided value
        assertEquals(75f, result.targetFps) // Original target should be preserved
    }
    
    @Test
    fun `execute should use default values for missing parameters`() {
        // Given
        val settings = MatrixSettings() // Default settings
        val context = mockContextWithSupportedRates(listOf(60))
        
        // When
        val result = useCase.execute(settings, context)
        
        // Then
        assertEquals(60f, result.effectiveFps)
        assertEquals(60f, result.targetFps)
        assertEquals(200, result.grainDensity) // Default from MatrixSettings
        assertEquals(0.03f, result.grainOpacity) // Default from MatrixSettings
        assertEquals(2.0f, result.fallSpeed) // Default from MatrixSettings
        assertEquals(150, result.columnCount) // Default from MatrixSettings
        assertEquals(0.9f, result.lineSpacing) // Default from MatrixSettings
        assertEquals(0.4f, result.activePercentage) // Default from MatrixSettings
        assertEquals(0.01f, result.speedVariance) // Default from MatrixSettings
        assertEquals(2.0f, result.glowIntensity) // Default from MatrixSettings
        assertEquals(2.0f, result.jitterAmount) // Default from MatrixSettings
        assertEquals(0.2f, result.flickerAmount) // Default from MatrixSettings
        assertEquals(0.08f, result.mutationRate) // Default from MatrixSettings
        assertEquals(14f, result.fontSize) // Default from MatrixSettings
        assertEquals(0xFF000000L, result.backgroundColor) // Default from MatrixSettings
        assertEquals(0xFF00FF00L, result.headColor) // Default from MatrixSettings
        assertEquals(0xFF00CC00L, result.brightTrailColor) // Default from MatrixSettings
        assertEquals(0xFF008800L, result.trailColor) // Default from MatrixSettings
        assertEquals(0xFF004400L, result.dimColor) // Default from MatrixSettings
    }
    
    @Test
    fun `execute should handle edge case with single supported rate`() {
        // Given
        val settings = MatrixSettings(targetFps = 120)
        val context = mockContextWithSupportedRates(listOf(60)) // Only 60Hz supported
        
        // When
        val result = useCase.execute(settings, context)
        
        // Then
        assertEquals(60f, result.effectiveFps) // Should be coerced to only supported rate
        assertEquals(120f, result.targetFps) // Original target should be preserved
    }
    
    @Test
    fun `execute should handle edge case with no supported rates`() {
        // Given
        val settings = MatrixSettings(targetFps = 90)
        val context = mockContextWithSupportedRates(emptyList()) // No supported rates
        
        // When
        val result = useCase.execute(settings, context)
        
        // Then
        assertEquals(60f, result.effectiveFps) // Should fallback to 60
        assertEquals(90f, result.targetFps) // Original target should be preserved
    }
    
    // Helper method to create mock context with supported rates
    private fun mockContextWithSupportedRates(supportedRates: List<Int>): android.content.Context {
        // In a real test, you would use Mockito or similar to mock the Context
        // For this test, we'll create a minimal mock that returns the supported rates
        return object : android.content.Context() {
            override fun getSystemService(name: String): Any? {
                if (name == android.content.Context.WINDOW_SERVICE) {
                    return object : android.view.WindowManager {
                        override fun getDefaultDisplay(): android.view.Display {
                            return object : android.view.Display() {
                                override fun getSupportedModes(): Array<android.view.Display.Mode> {
                                    return supportedRates.map { rate ->
                                        object : android.view.Display.Mode(0, 0, rate.toFloat()) {
                                            override fun getRefreshRate(): Float = rate.toFloat()
                                        }
                                    }.toTypedArray()
                                }
                            }
                        }
                        override fun addView(view: android.view.View, params: android.view.WindowManager.LayoutParams) {}
                        override fun updateViewLayout(view: android.view.View, params: android.view.WindowManager.LayoutParams) {}
                        override fun removeView(view: android.view.View) {}
                        override fun removeViewImmediate(view: android.view.View) {}
                        override fun getCurrentWindowMetrics(): android.view.WindowMetrics? = null
                        override fun getMaximumWindowMetrics(): android.view.WindowMetrics? = null
                        override fun getCurrentWindowMetrics(): android.view.WindowMetrics? = null
                        override fun getMaximumWindowMetrics(): android.view.WindowMetrics? = null
                    }
                }
                return null
            }
            
            // Minimal implementation for other required methods
            override fun getApplicationContext(): android.content.Context = this
            override fun getPackageName(): String = "com.example.matrixscreen"
            override fun getPackageManager(): android.content.pm.PackageManager? = null
            override fun getContentResolver(): android.content.ContentResolver? = null
            override fun getMainLooper(): android.os.Looper? = null
            override fun getMainExecutor(): java.util.concurrent.Executor? = null
            override fun getSystemService(serviceClass: Class<*>): Any? = null
            override fun getSystemServiceName(serviceClass: Class<*>): String? = null
            override fun getClassLoader(): ClassLoader? = null
            override fun getResources(): android.content.res.Resources? = null
            override fun getTheme(): android.content.res.Resources.Theme? = null
            override fun setTheme(resid: Int) {}
            override fun getAssets(): android.content.res.AssetManager? = null
            override fun getObbDir(): java.io.File? = null
            override fun getCacheDir(): java.io.File? = null
            override fun getCodeCacheDir(): java.io.File? = null
            override fun getDatabasePath(name: String): java.io.File? = null
            override fun getDir(name: String, mode: Int): java.io.File? = null
            override fun openFileInput(name: String): java.io.FileInputStream? = null
            override fun openFileOutput(name: String, mode: Int): java.io.FileOutputStream? = null
            override fun deleteFile(name: String): Boolean = false
            override fun getFileStreamPath(name: String): java.io.File? = null
            override fun fileList(): Array<String>? = null
            override fun getExternalFilesDir(type: String?): java.io.File? = null
            override fun getExternalFilesDirs(type: String?): Array<java.io.File>? = null
            override fun getObbDirs(): Array<java.io.File>? = null
            override fun getExternalCacheDir(): java.io.File? = null
            override fun getExternalCacheDirs(): Array<java.io.File>? = null
            override fun getExternalMediaDirs(): Array<java.io.File>? = null
            override fun getFilesDir(): java.io.File? = null
            override fun getNoBackupFilesDir(): java.io.File? = null
            override fun getSharedPreferences(name: String, mode: Int): android.content.SharedPreferences? = null
            override fun moveSharedPreferencesFrom(sourceContext: android.content.Context, name: String): Boolean = false
            override fun deleteSharedPreferences(name: String): Boolean = false
            override fun openOrCreateDatabase(name: String, mode: Int, factory: android.database.sqlite.SQLiteDatabase.CursorFactory?): android.database.sqlite.SQLiteDatabase? = null
            override fun openOrCreateDatabase(name: String, mode: Int, factory: android.database.sqlite.SQLiteDatabase.CursorFactory?, errorHandler: android.database.DatabaseErrorHandler?): android.database.sqlite.SQLiteDatabase? = null
            override fun moveDatabaseFrom(sourceContext: android.content.Context, name: String): Boolean = false
            override fun deleteDatabase(name: String): Boolean = false
            override fun getDatabasePath(name: String): java.io.File? = null
            override fun getDatabaseList(): Array<String>? = null
            override fun getWallpaper(): android.graphics.drawable.Drawable? = null
            override fun peekWallpaper(): android.graphics.drawable.Drawable? = null
            override fun getWallpaperDesiredMinimumWidth(): Int = 0
            override fun getWallpaperDesiredMinimumHeight(): Int = 0
            override fun setWallpaper(bitmap: android.graphics.Bitmap) {}
            override fun setWallpaper(data: java.io.InputStream) {}
            override fun clearWallpaper() {}
            override fun startActivity(intent: android.content.Intent) {}
            override fun startActivity(intent: android.content.Intent, options: android.os.Bundle?) {}
            override fun startActivities(intents: Array<android.content.Intent>) {}
            override fun startActivities(intents: Array<android.content.Intent>, options: android.os.Bundle?) {}
            override fun startIntentSender(intent: android.content.IntentSender, fillInIntent: android.content.Intent?, flagsMask: Int, flagsValues: Int, extraFlags: Int) {}
            override fun startIntentSender(intent: android.content.IntentSender, fillInIntent: android.content.Intent?, flagsMask: Int, flagsValues: Int, extraFlags: Int, options: android.os.Bundle?) {}
            override fun sendBroadcast(intent: android.content.Intent) {}
            override fun sendBroadcast(intent: android.content.Intent, receiverPermission: String?) {}
            override fun sendOrderedBroadcast(intent: android.content.Intent, receiverPermission: String?) {}
            override fun sendOrderedBroadcast(intent: android.content.Intent, receiverPermission: String?, resultReceiver: android.content.BroadcastReceiver?, scheduler: android.os.Handler?, initialCode: Int, initialData: String?, initialExtras: android.os.Bundle?) {}
            override fun sendBroadcastAsUser(intent: android.content.Intent, user: android.os.UserHandle) {}
            override fun sendBroadcastAsUser(intent: android.content.Intent, user: android.os.UserHandle, receiverPermission: String?) {}
            override fun sendOrderedBroadcastAsUser(intent: android.content.Intent, user: android.os.UserHandle, receiverPermission: String?, resultReceiver: android.content.BroadcastReceiver?, scheduler: android.os.Handler?, initialCode: Int, initialData: String?, initialExtras: android.os.Bundle?) {}
            override fun registerReceiver(receiver: android.content.BroadcastReceiver, filter: android.content.IntentFilter): android.content.Intent? = null
            override fun registerReceiver(receiver: android.content.BroadcastReceiver, filter: android.content.IntentFilter, flags: Int): android.content.Intent? = null
            override fun registerReceiver(receiver: android.content.BroadcastReceiver, filter: android.content.IntentFilter, broadcastPermission: String?, scheduler: android.os.Handler?): android.content.Intent? = null
            override fun registerReceiver(receiver: android.content.BroadcastReceiver, filter: android.content.IntentFilter, broadcastPermission: String?, scheduler: android.os.Handler?, flags: Int): android.content.Intent? = null
            override fun unregisterReceiver(receiver: android.content.BroadcastReceiver) {}
            override fun startService(service: android.content.Intent): android.content.ComponentName? = null
            override fun startForegroundService(service: android.content.Intent): android.content.ComponentName? = null
            override fun stopService(service: android.content.Intent): Boolean = false
            override fun bindService(service: android.content.Intent, conn: android.content.ServiceConnection, flags: Int): Boolean = false
            override fun bindService(service: android.content.Intent, flags: Int, executor: java.util.concurrent.Executor, callback: android.content.ServiceConnection): Boolean = false
            override fun bindIsolatedService(service: android.content.Intent, flags: Int, instanceName: String, executor: java.util.concurrent.Executor, callback: android.content.ServiceConnection): Boolean = false
            override fun unbindService(conn: android.content.ServiceConnection) {}
            override fun startInstrumentation(className: android.content.ComponentName, profileFile: String?, arguments: android.os.Bundle?): Boolean = false
            override fun getSystemService(serviceClass: Class<*>): Any? = null
            override fun getSystemServiceName(serviceClass: Class<*>): String? = null
            override fun checkPermission(permission: String, pid: Int, uid: Int): Int = 0
            override fun checkCallingPermission(permission: String): Int = 0
            override fun checkCallingOrSelfPermission(permission: String): Int = 0
            override fun checkSelfPermission(permission: String): Int = 0
            override fun enforcePermission(permission: String, pid: Int, uid: Int, message: String?) {}
            override fun enforceCallingPermission(permission: String, message: String?) {}
            override fun enforceCallingOrSelfPermission(permission: String, message: String?) {}
            override fun grantUriPermission(toPackage: String, uri: android.net.Uri, modeFlags: Int) {}
            override fun revokeUriPermission(uri: android.net.Uri, modeFlags: Int) {}
            override fun revokeUriPermission(toPackage: String, uri: android.net.Uri, modeFlags: Int) {}
            override fun checkUriPermission(uri: android.net.Uri, pid: Int, uid: Int, modeFlags: Int): Int = 0
            override fun checkCallingUriPermission(uri: android.net.Uri, modeFlags: Int): Int = 0
            override fun checkCallingOrSelfUriPermission(uri: android.net.Uri, modeFlags: Int): Int = 0
            override fun checkUriPermission(uri: android.net.Uri, readPermission: String?, writePermission: String?, pid: Int, uid: Int, modeFlags: Int): Int = 0
            override fun enforceUriPermission(uri: android.net.Uri, pid: Int, uid: Int, modeFlags: Int, message: String?) {}
            override fun enforceCallingUriPermission(uri: android.net.Uri, modeFlags: Int, message: String?) {}
            override fun enforceCallingOrSelfUriPermission(uri: android.net.Uri, modeFlags: Int, message: String?) {}
            override fun enforceUriPermission(uri: android.net.Uri, readPermission: String?, writePermission: String?, pid: Int, uid: Int, modeFlags: Int, message: String?) {}
            override fun createPackageContext(packageName: String, flags: Int): android.content.Context? = null
            override fun createContextForSplit(splitName: String): android.content.Context? = null
            override fun createConfigurationContext(overrideConfiguration: android.content.res.Configuration): android.content.Context? = null
            override fun createDisplayContext(display: android.view.Display): android.content.Context? = null
            override fun createWindowContext(type: Int, options: android.os.Bundle?): android.content.Context? = null
            override fun createWindowContext(display: android.view.Display, type: Int, options: android.os.Bundle?): android.content.Context? = null
            override fun createAttributionContext(attributionTag: String?): android.content.Context? = null
            override fun createDeviceProtectedStorageContext(): android.content.Context? = null
            override fun createCredentialProtectedStorageContext(): android.content.Context? = null
            override fun isDeviceProtectedStorage(): Boolean = false
            override fun isCredentialProtectedStorage(): Boolean = false
            override fun canLoadUnsafeResources(): Boolean = false
            override fun getDisplay(): android.view.Display? = null
            override fun getUserId(): Int = 0
            override fun isUserUnlocked(): Boolean = true
            override fun isUserRunningAndUnlocked(): Boolean = true
            override fun getOpPackageName(): String? = null
            override fun getAttributionTag(): String? = null
            override fun getParams(): android.content.ContextParams? = null
        }
    }
}
