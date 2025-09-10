package com.example.matrixscreen.core.design

import androidx.compose.ui.unit.dp

/**
 * Design tokens for MatrixScreen UI components.
 * 
 * These tokens ensure consistent spacing, sizing, and visual properties
 * across all UI components in the application.
 */
object DesignTokens {
    
    // Spacing tokens
    object Spacing {
        val xs = 4.dp
        val sm = 8.dp
        val md = 12.dp
        val lg = 16.dp
        val xl = 24.dp
        val xxl = 48.dp
        
        // Component-specific spacing
        val sectionPadding = md
        val itemSpacing = sm
        val screenPadding = lg
        val overlayPadding = md
        
        // New unified spacing
        val sectionSpacing = md
        val elementSpacing = sm
    }
    
    // Border radius tokens
    object Radius {
        val xs = 4.dp
        val sm = 8.dp
        val md = 12.dp
        val lg = 16.dp
        val xl = 24.dp
        val full = 999.dp
        
        // Component-specific radii
        val card = md // Increased for more pronounced corners
        val button = 6.dp
        val overlay = lg
        val colorPicker = md
        val previewCard = lg // Special radius for preview cards
        
        // New unified radius
        val pill = 24.dp
    }
    
    // Elevation tokens
    object Elevation {
        val none = 0.dp
        val xs = 1.dp
        val sm = 2.dp
        val md = 4.dp
        val lg = 8.dp
        val xl = 16.dp
        
        // Component-specific elevations
        val card = md // Increased for floating glass effect
        val overlay = lg
        val floatingButton = md
        val previewCard = lg // Special elevation for preview cards
    }
    
    // Outline tokens
    object Outline {
        val none = 0.dp
        val thin = 1.dp
        val medium = 2.dp
        val thick = 4.dp
        
        // Component-specific outlines
        val focus = thin
        val selection = medium
        val divider = thin
    }
    
    // Focus glow tokens
    object FocusGlow {
        val radius = 8.dp
        val intensity = 0.3f
        val duration = 200 // milliseconds
    }
    
    // Animation tokens
    object Animation {
        val fast = 150 // milliseconds
        val normal = 300 // milliseconds
        val slow = 500 // milliseconds
    }
    
    // Component sizing tokens
    object Sizing {
        val touchTarget = 48.dp
        val iconSize = 24.dp
        val smallIconSize = 16.dp
        val largeIconSize = 32.dp
        
        // Overlay sizing
        val overlayMinHeight = 200.dp
        val overlayMaxHeight = 400.dp
        val quickPanelHeight = 120.dp
        
        // Settings components
        val sliderHeight = 40.dp
        val toggleSize = 48.dp
        val colorSwatchSize = 32.dp
        
        // New unified sizing
        val minTouchTarget = 48.dp
        val buttonHeight = 36.dp
    }
    
    // Grid and layout tokens
    object Layout {
        val maxContentWidth = 340.dp
        val gridColumns = 2
        val settingsColumns = 1
        
        // Breakpoints for responsive design
        val compactWidth = 600.dp
        val mediumWidth = 840.dp
        val expandedWidth = 1200.dp
    }
    
    // Scrolling and grid behavior tokens
    object Scrolling {
        // Scrollable content tokens
        val maxContentHeight = 400.dp  // Maximum height before scrolling kicks in
        val minContentHeight = 100.dp  // Minimum height for content areas
        val scrollablePadding = Spacing.md  // Consistent padding for scrollable areas
        
        // Grid spacing
        val gridItemSpacing = Spacing.sm
        val gridLineSpacing = Spacing.sm
        
        // Scroll behavior
        val enableSmartScrolling = true
        val scrollAnimationDuration = Animation.normal
    }
    
    // Grid layout configuration
    object Grid {
        // Columns per row for different content types
        val themePresetsPerRow = 2
        val symbolSetsPerRow = 2
        val customSetsPerRow = 1
        
        // Button and tile sizing
        val presetButtonMinWidth = 120.dp
        val symbolSetTileMinWidth = 120.dp
        val customSetItemHeight = 80.dp
    }
}
