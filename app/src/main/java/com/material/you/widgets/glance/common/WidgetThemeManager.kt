package com.material.you.widgets.glance.common

import android.content.Context
import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.glance.unit.ColorProvider
import com.kyant.monet.TonalPalettes
import com.kyant.monet.TonalPalettes.Companion.toTonalPalettes
import com.material.you.widgets.core.theme.ThemeColors
import com.material.you.widgets.core.theme.ThemeManager

/**
 * Widget Theme Manager
 * Bridges ThemeManager (app-level) with widget rendering (Glance)
 * Provides Material 3 dynamic colors for widgets based on app theme preferences
 */
object WidgetThemeManager {
    
    /**
     * Complete widget color scheme
     */
    data class WidgetColorScheme(
        val primary: ColorProvider,
        val onPrimary: ColorProvider,
        val primaryContainer: ColorProvider,
        val onPrimaryContainer: ColorProvider,
        val secondary: ColorProvider,
        val onSecondary: ColorProvider,
        val secondaryContainer: ColorProvider,
        val onSecondaryContainer: ColorProvider,
        val tertiary: ColorProvider,
        val onTertiary: ColorProvider,
        val tertiaryContainer: ColorProvider,
        val onTertiaryContainer: ColorProvider,
        val background: ColorProvider,
        val onBackground: ColorProvider,
        val surface: ColorProvider,
        val onSurface: ColorProvider,
        val surfaceVariant: ColorProvider,
        val onSurfaceVariant: ColorProvider,
        val outline: ColorProvider,
        val outlineVariant: ColorProvider
    )
    
    /**
     * Get complete widget color scheme based on current theme settings
     * Integrates with ThemeManager to apply app-level theme preferences to widgets
     */
    fun getWidgetColorScheme(context: Context): WidgetColorScheme {
        val isDarkMode = WidgetAppearanceHelper.isDarkMode(context)
        val themeConfig = ThemeManager.getThemeConfig()
        val widgetPrefs = context.getSharedPreferences("widget_settings", Context.MODE_PRIVATE)

        // Check if widgets should use dynamic colors
        val useDynamicColors = widgetPrefs.getBoolean("widget_use_dynamic_colors", true)
        val pureBlack = widgetPrefs.getBoolean("widget_pure_black", false)
        val opacity = widgetPrefs.getFloat("widget_opacity", 1.0f)

        val base = when {
            // Material You dynamic colors (Android 12+)
            useDynamicColors && themeConfig.useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                getDynamicColorScheme(context, isDarkMode, pureBlack)
            }
            // Monochrome theme
            themeConfig.useMonochrome -> {
                getMonochromeColorScheme(isDarkMode, pureBlack)
            }
            // Custom seed color theme
            else -> {
                getCustomColorScheme(themeConfig.seedColor, isDarkMode, pureBlack)
            }
        }

        return if (opacity < 1f) base.withSurfaceOpacity(context, opacity) else base
    }

    /**
     * Apply the user's widget-opacity preference to the background/surface
     * roles so widget backgrounds become translucent. Foreground roles keep
     * full alpha so text stays readable.
     */
    private fun WidgetColorScheme.withSurfaceOpacity(
        context: Context,
        @Suppress("SameParameterValue") opacity: Float
    ): WidgetColorScheme {
        fun fade(provider: ColorProvider): ColorProvider {
            val color = provider.getColor(context)
            return ColorProvider(color.copy(alpha = color.alpha * opacity))
        }
        return copy(
            background = fade(background),
            surface = fade(surface)
        )
    }
    
    /**
     * Get dynamic color scheme from system (Android 12+)
     */
    private fun getDynamicColorScheme(
        context: Context,
        isDarkMode: Boolean,
        pureBlack: Boolean
    ): WidgetColorScheme {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (isDarkMode) {
                WidgetColorScheme(
                    primary = ColorProvider(Color(context.getColor(android.R.color.system_accent1_200))),
                    onPrimary = ColorProvider(Color(context.getColor(android.R.color.system_accent1_800))),
                    primaryContainer = ColorProvider(Color(context.getColor(android.R.color.system_accent1_700))),
                    onPrimaryContainer = ColorProvider(Color(context.getColor(android.R.color.system_accent1_100))),
                    secondary = ColorProvider(Color(context.getColor(android.R.color.system_accent2_200))),
                    onSecondary = ColorProvider(Color(context.getColor(android.R.color.system_accent2_800))),
                    secondaryContainer = ColorProvider(Color(context.getColor(android.R.color.system_accent2_700))),
                    onSecondaryContainer = ColorProvider(Color(context.getColor(android.R.color.system_accent2_100))),
                    tertiary = ColorProvider(Color(context.getColor(android.R.color.system_accent3_200))),
                    onTertiary = ColorProvider(Color(context.getColor(android.R.color.system_accent3_800))),
                    tertiaryContainer = ColorProvider(Color(context.getColor(android.R.color.system_accent3_700))),
                    onTertiaryContainer = ColorProvider(Color(context.getColor(android.R.color.system_accent3_100))),
                    background = ColorProvider(if (pureBlack) Color.Black else Color(context.getColor(android.R.color.system_neutral1_900))),
                    onBackground = ColorProvider(Color(context.getColor(android.R.color.system_neutral1_100))),
                    surface = ColorProvider(if (pureBlack) Color.Black else Color(context.getColor(android.R.color.system_neutral1_900))),
                    onSurface = ColorProvider(Color(context.getColor(android.R.color.system_neutral1_100))),
                    surfaceVariant = ColorProvider(Color(context.getColor(android.R.color.system_neutral2_800))),
                    onSurfaceVariant = ColorProvider(Color(context.getColor(android.R.color.system_neutral2_200))),
                    outline = ColorProvider(Color(context.getColor(android.R.color.system_neutral2_500))),
                    outlineVariant = ColorProvider(Color(context.getColor(android.R.color.system_neutral2_700)))
                )
            } else {
                WidgetColorScheme(
                    primary = ColorProvider(Color(context.getColor(android.R.color.system_accent1_600))),
                    onPrimary = ColorProvider(Color(context.getColor(android.R.color.system_accent1_0))),
                    primaryContainer = ColorProvider(Color(context.getColor(android.R.color.system_accent1_100))),
                    onPrimaryContainer = ColorProvider(Color(context.getColor(android.R.color.system_accent1_900))),
                    secondary = ColorProvider(Color(context.getColor(android.R.color.system_accent2_600))),
                    onSecondary = ColorProvider(Color(context.getColor(android.R.color.system_accent2_0))),
                    secondaryContainer = ColorProvider(Color(context.getColor(android.R.color.system_accent2_100))),
                    onSecondaryContainer = ColorProvider(Color(context.getColor(android.R.color.system_accent2_900))),
                    tertiary = ColorProvider(Color(context.getColor(android.R.color.system_accent3_600))),
                    onTertiary = ColorProvider(Color(context.getColor(android.R.color.system_accent3_0))),
                    tertiaryContainer = ColorProvider(Color(context.getColor(android.R.color.system_accent3_100))),
                    onTertiaryContainer = ColorProvider(Color(context.getColor(android.R.color.system_accent3_900))),
                    background = ColorProvider(Color(context.getColor(android.R.color.system_neutral1_50))),
                    onBackground = ColorProvider(Color(context.getColor(android.R.color.system_neutral1_900))),
                    surface = ColorProvider(Color(context.getColor(android.R.color.system_neutral1_50))),
                    onSurface = ColorProvider(Color(context.getColor(android.R.color.system_neutral1_900))),
                    surfaceVariant = ColorProvider(Color(context.getColor(android.R.color.system_neutral2_100))),
                    onSurfaceVariant = ColorProvider(Color(context.getColor(android.R.color.system_neutral2_700))),
                    outline = ColorProvider(Color(context.getColor(android.R.color.system_neutral2_500))),
                    outlineVariant = ColorProvider(Color(context.getColor(android.R.color.system_neutral2_300)))
                )
            }
        } else {
            // Fallback for pre-Android 12
            getCustomColorScheme(ThemeColors.Presets.defaultSeed, isDarkMode, pureBlack)
        }
    }
    
    /**
     * Get custom color scheme from seed color using Material 3 HCT
     */
    private fun getCustomColorScheme(
        seedColor: Color,
        isDarkMode: Boolean,
        pureBlack: Boolean
    ): WidgetColorScheme {
        val palettes: TonalPalettes = seedColor.toTonalPalettes()
        
        return if (isDarkMode) {
            WidgetColorScheme(
                primary = ColorProvider(palettes accent1 80.0),
                onPrimary = ColorProvider(palettes accent1 20.0),
                primaryContainer = ColorProvider(palettes accent1 30.0),
                onPrimaryContainer = ColorProvider(palettes accent1 90.0),
                secondary = ColorProvider(palettes accent2 80.0),
                onSecondary = ColorProvider(palettes accent2 20.0),
                secondaryContainer = ColorProvider(palettes accent2 30.0),
                onSecondaryContainer = ColorProvider(palettes accent2 90.0),
                tertiary = ColorProvider(palettes accent3 80.0),
                onTertiary = ColorProvider(palettes accent3 20.0),
                tertiaryContainer = ColorProvider(palettes accent3 30.0),
                onTertiaryContainer = ColorProvider(palettes accent3 90.0),
                background = ColorProvider(if (pureBlack) Color.Black else palettes neutral1 6.0),
                onBackground = ColorProvider(palettes neutral1 90.0),
                surface = ColorProvider(if (pureBlack) Color.Black else palettes neutral1 6.0),
                onSurface = ColorProvider(palettes neutral1 90.0),
                surfaceVariant = ColorProvider(palettes neutral2 30.0),
                onSurfaceVariant = ColorProvider(palettes neutral2 80.0),
                outline = ColorProvider(palettes neutral2 60.0),
                outlineVariant = ColorProvider(palettes neutral2 30.0)
            )
        } else {
            WidgetColorScheme(
                primary = ColorProvider(palettes accent1 40.0),
                onPrimary = ColorProvider(palettes accent1 100.0),
                primaryContainer = ColorProvider(palettes accent1 90.0),
                onPrimaryContainer = ColorProvider(palettes accent1 10.0),
                secondary = ColorProvider(palettes accent2 40.0),
                onSecondary = ColorProvider(palettes accent2 100.0),
                secondaryContainer = ColorProvider(palettes accent2 90.0),
                onSecondaryContainer = ColorProvider(palettes accent2 10.0),
                tertiary = ColorProvider(palettes accent3 40.0),
                onTertiary = ColorProvider(palettes accent3 100.0),
                tertiaryContainer = ColorProvider(palettes accent3 90.0),
                onTertiaryContainer = ColorProvider(palettes accent3 10.0),
                background = ColorProvider(palettes neutral1 98.0),
                onBackground = ColorProvider(palettes neutral1 10.0),
                surface = ColorProvider(palettes neutral1 98.0),
                onSurface = ColorProvider(palettes neutral1 10.0),
                surfaceVariant = ColorProvider(palettes neutral2 90.0),
                onSurfaceVariant = ColorProvider(palettes neutral2 30.0),
                outline = ColorProvider(palettes neutral2 50.0),
                outlineVariant = ColorProvider(palettes neutral2 80.0)
            )
        }
    }
    
    /**
     * Get monochrome color scheme (black and white)
     */
    private fun getMonochromeColorScheme(
        isDarkMode: Boolean,
        pureBlack: Boolean
    ): WidgetColorScheme {
        return if (isDarkMode) {
            WidgetColorScheme(
                primary = ColorProvider(Color(0xFFE0E0E0)),
                onPrimary = ColorProvider(Color(0xFF121212)),
                primaryContainer = ColorProvider(Color(0xFF2A2A2A)),
                onPrimaryContainer = ColorProvider(Color(0xFFFAFAFA)),
                secondary = ColorProvider(Color(0xFFC0C0C0)),
                onSecondary = ColorProvider(Color(0xFF121212)),
                secondaryContainer = ColorProvider(Color(0xFF333333)),
                onSecondaryContainer = ColorProvider(Color(0xFFFAFAFA)),
                tertiary = ColorProvider(Color(0xFF9E9E9E)),
                onTertiary = ColorProvider(Color(0xFF121212)),
                tertiaryContainer = ColorProvider(Color(0xFF3A3A3A)),
                onTertiaryContainer = ColorProvider(Color(0xFFFAFAFA)),
                background = ColorProvider(if (pureBlack) Color.Black else Color(0xFF121212)),
                onBackground = ColorProvider(Color(0xFFE0E0E0)),
                surface = ColorProvider(if (pureBlack) Color.Black else Color(0xFF0F0F0F)),
                onSurface = ColorProvider(Color(0xFFE0E0E0)),
                surfaceVariant = ColorProvider(Color(0xFF1B1B1B)),
                onSurfaceVariant = ColorProvider(Color(0xFFB0B0B0)),
                outline = ColorProvider(Color(0xFF5A5A5A)),
                outlineVariant = ColorProvider(Color(0xFF3A3A3A))
            )
        } else {
            WidgetColorScheme(
                primary = ColorProvider(Color(0xFF2A2A2A)),
                onPrimary = ColorProvider(Color(0xFFFAFAFA)),
                primaryContainer = ColorProvider(Color(0xFFE8E8E8)),
                onPrimaryContainer = ColorProvider(Color(0xFF121212)),
                secondary = ColorProvider(Color(0xFF4A4A4A)),
                onSecondary = ColorProvider(Color(0xFFFAFAFA)),
                secondaryContainer = ColorProvider(Color(0xFFF0F0F0)),
                onSecondaryContainer = ColorProvider(Color(0xFF121212)),
                tertiary = ColorProvider(Color(0xFF6A6A6A)),
                onTertiary = ColorProvider(Color(0xFFFAFAFA)),
                tertiaryContainer = ColorProvider(Color(0xFFE0E0E0)),
                onTertiaryContainer = ColorProvider(Color(0xFF121212)),
                background = ColorProvider(Color(0xFFFAFAFA)),
                onBackground = ColorProvider(Color(0xFF1A1A1A)),
                surface = ColorProvider(Color(0xFFFAFAFA)),
                onSurface = ColorProvider(Color(0xFF1A1A1A)),
                surfaceVariant = ColorProvider(Color(0xFFF0F0F0)),
                onSurfaceVariant = ColorProvider(Color(0xFF4A4A4A)),
                outline = ColorProvider(Color(0xFF7A7A7A)),
                outlineVariant = ColorProvider(Color(0xFFD0D0D0))
            )
        }
    }
    
    /**
     * Get widget opacity from preferences
     */
    fun getWidgetOpacity(context: Context): Float {
        val prefs = context.getSharedPreferences("widget_settings", Context.MODE_PRIVATE)
        return prefs.getFloat("widget_opacity", 1.0f)
    }
    
    /**
     * Get widget corner radius from preferences
     */
    fun getWidgetCornerRadius(context: Context): Int {
        val prefs = context.getSharedPreferences("widget_settings", Context.MODE_PRIVATE)
        return prefs.getInt("widget_corner_radius", 28)
    }
}
