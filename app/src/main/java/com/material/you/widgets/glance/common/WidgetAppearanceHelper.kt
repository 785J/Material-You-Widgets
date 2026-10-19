package com.material.you.widgets.glance.common

import android.content.Context
import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.glance.unit.ColorProvider

/**
 * Universal Widget Appearance Helper
 * Standardizes all widget appearance settings across the app
 * Now integrated with WidgetThemeManager for Material 3 dynamic theming
 */
object WidgetAppearanceHelper {
    
    // Preference keys
    private const val PREFS_NAME = "widget_settings"
    private const val KEY_USE_DYNAMIC_COLORS = "widget_use_dynamic_colors"
    private const val KEY_PURE_BLACK = "widget_pure_black"
    private const val KEY_OPACITY = "widget_opacity"
    private const val KEY_CORNER_RADIUS = "widget_corner_radius"
    
    // Default values
    private const val DEFAULT_OPACITY = 1.0f
    private const val DEFAULT_CORNER_RADIUS = 28
    private const val DEFAULT_USE_DYNAMIC_COLORS = true
    
    /**
     * Universal widget appearance configuration
     */
    data class WidgetAppearance(
        val colorScheme: WidgetThemeManager.WidgetColorScheme,
        val cornerRadius: Int,
        val opacity: Float,
        val isDarkMode: Boolean,
        val useDynamicColors: Boolean,
        // Legacy properties for backward compatibility
        val backgroundColor: ColorProvider,
        val textColor: ColorProvider,
        val textColorVariant: ColorProvider
    )
    
    /**
     * Get complete widget appearance configuration
     * Now uses WidgetThemeManager for Material 3 color schemes
     */
    fun getWidgetAppearance(context: Context): WidgetAppearance {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isDarkMode = isDarkMode(context)
        val useDynamicColors = prefs.getBoolean(KEY_USE_DYNAMIC_COLORS, DEFAULT_USE_DYNAMIC_COLORS)
        val opacity = prefs.getFloat(KEY_OPACITY, DEFAULT_OPACITY)
        val cornerRadius = prefs.getInt(KEY_CORNER_RADIUS, DEFAULT_CORNER_RADIUS)
        
        // Get complete color scheme from WidgetThemeManager
        val colorScheme = WidgetThemeManager.getWidgetColorScheme(context)
        
        return WidgetAppearance(
            colorScheme = colorScheme,
            cornerRadius = cornerRadius,
            opacity = opacity,
            isDarkMode = isDarkMode,
            useDynamicColors = useDynamicColors,
            // Legacy properties mapped from color scheme
            backgroundColor = colorScheme.surface,
            textColor = colorScheme.onSurface,
            textColorVariant = colorScheme.onSurfaceVariant
        )
    }
    
    /**
     * Get the background color provider based on user preferences
     * @deprecated Use getWidgetAppearance().colorScheme.surface instead
     */
    @Deprecated("Use getWidgetAppearance().colorScheme", ReplaceWith("getWidgetAppearance(context).colorScheme.surface"))
    fun getBackgroundColor(
        context: Context,
        isDarkMode: Boolean,
        useDynamicColors: Boolean = true,
        usePureBlack: Boolean = false,
        opacity: Float = DEFAULT_OPACITY
    ): ColorProvider {
        val colorScheme = WidgetThemeManager.getWidgetColorScheme(context)
        return colorScheme.surface
    }
    
    /**
     * Get text color based on theme
     * @deprecated Use getWidgetAppearance().colorScheme.onSurface instead
     */
    @Deprecated("Use getWidgetAppearance().colorScheme", ReplaceWith("getWidgetAppearance(context).colorScheme.onSurface"))
    fun getTextColor(
        context: Context,
        isDarkMode: Boolean,
        useDynamicColors: Boolean = true
    ): ColorProvider {
        val colorScheme = WidgetThemeManager.getWidgetColorScheme(context)
        return colorScheme.onSurface
    }
    
    /**
     * Get variant text color (for secondary text)
     * @deprecated Use getWidgetAppearance().colorScheme.onSurfaceVariant instead
     */
    @Deprecated("Use getWidgetAppearance().colorScheme", ReplaceWith("getWidgetAppearance(context).colorScheme.onSurfaceVariant"))
    fun getTextColorVariant(
        context: Context,
        isDarkMode: Boolean,
        useDynamicColors: Boolean = true
    ): ColorProvider {
        val colorScheme = WidgetThemeManager.getWidgetColorScheme(context)
        return colorScheme.onSurfaceVariant
    }
    
    /**
     * Get corner radius in dp
     */
    fun getCornerRadius(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_CORNER_RADIUS, DEFAULT_CORNER_RADIUS)
    }
    
    /**
     * Get widget opacity value
     */
    fun getOpacity(context: Context): Float {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getFloat(KEY_OPACITY, DEFAULT_OPACITY)
    }
    
    /**
     * Check if dark mode is enabled
     */
    fun isDarkMode(context: Context): Boolean {
        return context.resources.configuration.uiMode and 
            android.content.res.Configuration.UI_MODE_NIGHT_MASK == 
            android.content.res.Configuration.UI_MODE_NIGHT_YES
    }
    
    /**
     * Check if dynamic colors are enabled
     */
    fun useDynamicColors(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_USE_DYNAMIC_COLORS, DEFAULT_USE_DYNAMIC_COLORS) &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }
}
