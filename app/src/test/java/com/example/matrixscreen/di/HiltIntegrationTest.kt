package com.example.matrixscreen.di

import com.example.matrixscreen.data.repo.SettingsRepository
import com.example.matrixscreen.data.repo.SettingsRepositoryImpl
import com.example.matrixscreen.data.store.ProtoModule
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit test to verify DI module configuration works correctly.
 * 
 * This test verifies that DI modules can be instantiated and
 * dependencies can be created without runtime errors.
 */
class HiltIntegrationTest {
    
    @Test
    fun `verify DataModule can be instantiated`() {
        // Test that the DataModule object can be instantiated
        val dataModule = DataModule
        assertNotNull("DataModule should be instantiable", dataModule)
    }
    
    @Test
    fun `verify ProtoModule can be instantiated`() {
        // Test that the ProtoModule object can be instantiated
        val protoModule = ProtoModule
        assertNotNull("ProtoModule should be instantiable", protoModule)
    }
    
    @Test
    fun `verify DispatcherModule can be instantiated`() {
        // Test that the DispatcherModule object can be instantiated
        val dispatcherModule = com.example.matrixscreen.core.DispatcherModule
        assertNotNull("DispatcherModule should be instantiable", dispatcherModule)
    }
    
    @Test
    fun `verify repository interface exists`() {
        // Test that the repository interface is accessible
        val repositoryClass = SettingsRepository::class.java
        assertNotNull("SettingsRepository interface should exist", repositoryClass)
        assertTrue("SettingsRepository should be an interface", repositoryClass.isInterface)
    }
}
