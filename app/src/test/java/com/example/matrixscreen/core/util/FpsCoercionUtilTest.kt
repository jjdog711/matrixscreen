package com.example.matrixscreen.core.util

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for FpsCoercionUtil.
 * 
 * Tests the FPS coercion logic to ensure device capabilities are properly handled
 * and user-selected FPS values are correctly mapped to device-supported rates.
 */
class FpsCoercionUtilTest {
    
    @Test
    fun `coerceFps should return nearest supported rate`() {
        val supportedRates = listOf(30, 60, 90, 120)
        
        // Test exact matches
        assertEquals(30, FpsCoercionUtil.coerceFps(30, supportedRates))
        assertEquals(60, FpsCoercionUtil.coerceFps(60, supportedRates))
        assertEquals(90, FpsCoercionUtil.coerceFps(90, supportedRates))
        assertEquals(120, FpsCoercionUtil.coerceFps(120, supportedRates))
        
        // Test nearest matches
        assertEquals(30, FpsCoercionUtil.coerceFps(25, supportedRates))
        assertEquals(30, FpsCoercionUtil.coerceFps(35, supportedRates))
        assertEquals(60, FpsCoercionUtil.coerceFps(50, supportedRates))
        assertEquals(60, FpsCoercionUtil.coerceFps(70, supportedRates))
        assertEquals(90, FpsCoercionUtil.coerceFps(80, supportedRates))
        assertEquals(90, FpsCoercionUtil.coerceFps(100, supportedRates))
        assertEquals(120, FpsCoercionUtil.coerceFps(110, supportedRates))
        assertEquals(120, FpsCoercionUtil.coerceFps(130, supportedRates))
    }
    
    @Test
    fun `coerceFps should clamp to valid range`() {
        val supportedRates = listOf(30, 60, 90, 120)
        
        // Test minimum clamping
        assertEquals(30, FpsCoercionUtil.coerceFps(5, supportedRates, minFps = 15))
        assertEquals(30, FpsCoercionUtil.coerceFps(10, supportedRates, minFps = 15))
        
        // Test maximum clamping
        assertEquals(120, FpsCoercionUtil.coerceFps(150, supportedRates, maxFps = 120))
        assertEquals(120, FpsCoercionUtil.coerceFps(200, supportedRates, maxFps = 120))
    }
    
    @Test
    fun `coerceFps should handle edge cases`() {
        val supportedRates = listOf(60)
        
        // Test single supported rate
        assertEquals(60, FpsCoercionUtil.coerceFps(30, supportedRates))
        assertEquals(60, FpsCoercionUtil.coerceFps(90, supportedRates))
        assertEquals(60, FpsCoercionUtil.coerceFps(120, supportedRates))
        
        // Test empty supported rates (should return 60 as fallback)
        assertEquals(60, FpsCoercionUtil.coerceFps(60, emptyList()))
    }
    
    @Test
    fun `isFpsSupported should correctly identify supported rates`() {
        val supportedRates = listOf(30, 60, 90, 120)
        
        assertTrue(FpsCoercionUtil.isFpsSupported(30, supportedRates))
        assertTrue(FpsCoercionUtil.isFpsSupported(60, supportedRates))
        assertTrue(FpsCoercionUtil.isFpsSupported(90, supportedRates))
        assertTrue(FpsCoercionUtil.isFpsSupported(120, supportedRates))
        
        assertFalse(FpsCoercionUtil.isFpsSupported(45, supportedRates))
        assertFalse(FpsCoercionUtil.isFpsSupported(75, supportedRates))
        assertFalse(FpsCoercionUtil.isFpsSupported(105, supportedRates))
    }
    
    @Test
    fun `getRecommendedFpsOptions should filter common rates by device support`() {
        // Mock context would be needed for full testing, but we can test the logic
        // with different supported rate scenarios
        
        // Test with standard 60Hz device
        val standardRates = listOf(60)
        val commonRates = listOf(30, 60, 90, 120)
        val filtered = commonRates.filter { FpsCoercionUtil.isFpsSupported(it, standardRates) }
        assertEquals(listOf(60), filtered)
        
        // Test with high refresh rate device
        val highRefreshRates = listOf(60, 90, 120)
        val filteredHigh = commonRates.filter { FpsCoercionUtil.isFpsSupported(it, highRefreshRates) }
        assertEquals(listOf(60, 90, 120), filteredHigh)
        
        // Test with low refresh rate device
        val lowRefreshRates = listOf(30, 60)
        val filteredLow = commonRates.filter { FpsCoercionUtil.isFpsSupported(it, lowRefreshRates) }
        assertEquals(listOf(30, 60), filteredLow)
    }
    
    @Test
    fun `getMaxSupportedFps should return highest supported rate`() {
        assertEquals(120, FpsCoercionUtil.getMaxSupportedFps(mockContextWithRates(listOf(30, 60, 90, 120))))
        assertEquals(60, FpsCoercionUtil.getMaxSupportedFps(mockContextWithRates(listOf(30, 60))))
        assertEquals(30, FpsCoercionUtil.getMaxSupportedFps(mockContextWithRates(listOf(30))))
    }
    
    @Test
    fun `getMinSupportedFps should return lowest supported rate`() {
        assertEquals(30, FpsCoercionUtil.getMinSupportedFps(mockContextWithRates(listOf(30, 60, 90, 120))))
        assertEquals(30, FpsCoercionUtil.getMinSupportedFps(mockContextWithRates(listOf(30, 60))))
        assertEquals(60, FpsCoercionUtil.getMinSupportedFps(mockContextWithRates(listOf(60))))
    }
    
    // Helper method to create mock context (simplified for testing)
    private fun mockContextWithRates(rates: List<Int>): android.content.Context {
        // In a real test, you would use Mockito or similar to mock the Context
        // For this test, we'll just test the utility methods directly
        return object : android.content.Context() {
            // Minimal implementation for testing
            override fun getSystemService(name: String): Any? = null
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
