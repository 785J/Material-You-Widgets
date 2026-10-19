package com.material.you.widgets

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.material.you.widgets.core.theme.ThemeManager
import com.material.you.widgets.core.theme.ThemeTypography
import com.material.you.widgets.core.theme.WidgetsTheme
import com.material.you.widgets.core.theme.collectExpressiveShapesAsState
import com.material.you.widgets.core.theme.collectFontFamilyAsState
import com.material.you.widgets.core.theme.collectPureBlackAsState
import com.material.you.widgets.core.theme.collectThemeModeAsState
import com.material.you.widgets.core.theme.collectThemeSeedColorAsState
import com.material.you.widgets.core.theme.collectUseDynamicColorAsState
import com.material.you.widgets.core.theme.collectUseMonochromeAsState
import com.material.you.widgets.data.models.AppFontFamily
import com.material.you.widgets.data.models.ThemeMode
import com.material.you.widgets.ui.screens.ThemeCustomizationScreen
import com.material.you.widgets.ui.screens.WidgetsListScreen
import com.material.you.widgets.ui.screens.WidgetsSettingsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // All theme settings come from ThemeManager's StateFlows, so any
            // change in a settings screen recomposes the app instantly — no
            // activity recreation needed.
            val themeMode by ThemeManager.collectThemeModeAsState()
            val systemInDarkTheme = isSystemInDarkTheme()
            val darkTheme = when (themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> systemInDarkTheme
            }

            val useDynamicColor by ThemeManager.collectUseDynamicColorAsState()
            val themeColor by ThemeManager.collectThemeSeedColorAsState()
            val monochrome by ThemeManager.collectUseMonochromeAsState()
            val pureBlack by ThemeManager.collectPureBlackAsState()
            val expressive by ThemeManager.collectExpressiveShapesAsState()
            val fontFamilyPref by ThemeManager.collectFontFamilyAsState()

            val selectedFont = when (fontFamilyPref) {
                AppFontFamily.DEFAULT -> null // null = platform default font
                AppFontFamily.BITCOUNT -> ThemeTypography.bitcount
                AppFontFamily.QUICKSAND -> ThemeTypography.quicksand
                AppFontFamily.BALOO_BHAI -> ThemeTypography.balooBhai
            }

            WidgetsTheme(
                darkTheme = darkTheme,
                useDynamicColor = useDynamicColor,
                themeColor = themeColor,
                monochromeTheme = monochrome,
                pureBlack = pureBlack,
                expressive = expressive,
                fontFamily = selectedFont
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation()
                }
            }
        }
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "widgets_list"
    ) {
        // Main Screen: Widgets List
        composable("widgets_list") {
            WidgetsListScreen(
                onWidgetClick = { widgetId ->
                    when (widgetId) {
                        // Calendar has its own dedicated configuration screen
                        "calendar" -> navController.navigate("calendar_detail")
                        // Battery / Away Time / Year Progress share the global
                        // widget settings screen
                        else -> navController.navigate("widget_settings")
                    }
                },
                onSettingsClick = {
                    navController.navigate("settings")
                }
            )
        }

        // Settings Screen
        composable("settings") {
            WidgetsSettingsScreen(
                onBack = {
                    navController.popBackStack()
                },
                onNavigateToAppearance = {
                    navController.navigate("theme_customization")
                },
                onNavigateToWidgetAppearance = {
                    navController.navigate("widget_settings")
                }
            )
        }

        // Widget Settings Screen
        composable("widget_settings") {
            com.material.you.widgets.ui.screens.WidgetSettingsScreen(
                onNavigateToCalendarDetail = {
                    navController.navigate("calendar_detail")
                }
            )
        }

        // Calendar Detail Screen
        composable("calendar_detail") {
            com.material.you.widgets.ui.screens.CalendarDetailScreen()
        }

        // Theme Customization Screen
        composable("theme_customization") {
            ThemeCustomizationScreen()
        }
    }
}
