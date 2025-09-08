package com.example.matrixscreen.data.model

import com.example.matrixscreen.data.proto.MatrixSettingsProto
import org.junit.Test

/**
 * Debug test to understand the actual values.
 */
class DebugTest {
    
    @Test
    fun `debug proto defaults`() {
        val proto = MatrixSettingsProto.getDefaultInstance()
        println("Proto schema version: ${proto.schemaVersion}")
        println("Proto fall speed: ${proto.fallSpeed}")
        println("Proto column count: ${proto.columnCount}")
        println("Proto target FPS: ${proto.targetFps}")
        println("Proto background color: ${proto.backgroundColor}")
        println("Proto head color: ${proto.headColor}")
        
        val domain = proto.toDomain()
        println("Domain schema version: ${domain.schemaVersion}")
        println("Domain fall speed: ${domain.fallSpeed}")
        println("Domain column count: ${domain.columnCount}")
        println("Domain target FPS: ${domain.targetFps}")
        println("Domain background color: ${domain.backgroundColor}")
        println("Domain head color: ${domain.headColor}")
        
        val defaultDomain = MatrixSettings.DEFAULT
        println("Default domain schema version: ${defaultDomain.schemaVersion}")
        println("Default domain fall speed: ${defaultDomain.fallSpeed}")
        println("Default domain column count: ${defaultDomain.columnCount}")
        println("Default domain target FPS: ${defaultDomain.targetFps}")
        println("Default domain background color: ${defaultDomain.backgroundColor}")
        println("Default domain head color: ${defaultDomain.headColor}")
    }
}
