package com.example.matrixscreen.ui.settings

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.matrixscreen.ui.settings.background.BackgroundSettingsScreen
import com.example.matrixscreen.ui.settings.characters.CharactersSettingsScreen
import com.example.matrixscreen.ui.settings.effects.EffectsSettingsScreen
import com.example.matrixscreen.ui.settings.motion.MotionSettingsScreen
import com.example.matrixscreen.ui.settings.theme.ThemeSettingsScreen
import com.example.matrixscreen.ui.settings.timing.TimingSettingsScreen
import com.example.matrixscreen.ui.NewSettingsViewModel

/**
 * Navigation graph for the new settings architecture
 * Routes between the 6 main settings categories
 */
@Composable
fun SettingsNavGraph(
    navController: NavHostController,
    settingsViewModel: NewSettingsViewModel,
    onBack: () -> Unit,
    onNavigateToCustomSets: () -> Unit,
    onNavigateToUIPreview: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = "settings_home"
    ) {
        composable("settings_home") {
            SettingsHomeScreen(
                settingsViewModel = settingsViewModel,
                onNavigateToTheme = { navController.navigate("theme") },
                onNavigateToCharacters = { navController.navigate("characters") },
                onNavigateToMotion = { navController.navigate("motion") },
                onNavigateToEffects = { navController.navigate("effects") },
                onNavigateToTiming = { navController.navigate("timing") },
                onNavigateToBackground = { navController.navigate("background") },
                onNavigateToUIPreview = onNavigateToUIPreview,
                onBack = onBack
            )
        }
        
        composable("theme") {
            ThemeSettingsScreen(
                settingsViewModel = settingsViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToCustomSets = onNavigateToCustomSets
            )
        }
        
        composable("characters") {
            CharactersSettingsScreen(
                settingsViewModel = settingsViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToCustomSets = onNavigateToCustomSets
            )
        }
        
        composable("motion") {
            MotionSettingsScreen(
                settingsViewModel = settingsViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("effects") {
            EffectsSettingsScreen(
                settingsViewModel = settingsViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("timing") {
            TimingSettingsScreen(
                settingsViewModel = settingsViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("background") {
            BackgroundSettingsScreen(
                settingsViewModel = settingsViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
