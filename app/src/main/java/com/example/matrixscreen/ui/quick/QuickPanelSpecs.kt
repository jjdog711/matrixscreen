package com.example.matrixscreen.ui.quick

import com.example.matrixscreen.ui.settings.model.*

object QuickPanelSpecs {
    val SPECS: List<WidgetSpec<*>> = listOf(
        SliderSpec(
            id = Speed,
            label = "Speed",
            range = 0.5f..10.0f, // Aligned with main MOTION_SPECS
            step = 0.1f,
            default = 2.0f,
            unit = "×",
            affectsPerf = true,
            help = "Speed of the falling rain effect"
        ),
        IntSliderSpec(
            id = Columns,
            label = "Columns",
            range = 50..500, // Aligned with main MOTION_SPECS
            step = 10,
            default = 150,
            unit = "cols",
            affectsPerf = true,
            help = "Number of rain columns on screen"
        ),
        SliderSpec(
            id = Glow,
            label = "Glow",
            range = 0.0f..5.0f, // Aligned with main EFFECTS_SPECS
            step = 0.1f, // Consistent step with main specs
            default = 2.0f,
            affectsPerf = true,
            help = "Intensity of the glow effect around characters"
        ),
        SliderSpec(
            id = Jitter,
            label = "Jitter",
            range = 0.0f..5.0f, // Aligned with main EFFECTS_SPECS
            step = 0.1f,
            default = 2.0f,
            unit = "px",
            help = "Random horizontal movement of characters"
        ),
        ColorSpec(
            id = BgColor,
            label = "Background",
            help = "Background color behind the rain"
        ),
        SelectSpec(
            id = Fps,
            label = "FPS",
            options = listOf(15, 30, 45, 60, 90, 120),
            toLabel = { "$it fps" },
            default = 60,
            help = "Target frames per second for the animation"
        )
    )
}
