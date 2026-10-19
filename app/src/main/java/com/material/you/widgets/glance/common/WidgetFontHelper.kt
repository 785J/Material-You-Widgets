package com.material.you.widgets.glance.common

import android.content.Context
import com.material.you.widgets.R
import com.material.you.widgets.data.models.AppFontFamily

/**
 * Widget Font Helper
 * Provides font resource IDs for Glance widgets based on user preferences
 * 
 * Note: Glance widgets use font resources differently than Compose
 * We provide the font resource ID which gets applied to TextStyle
 */
object WidgetFontHelper {
    
    /**
     * Get font resource ID for the selected widget font family
     * Returns null for system default font
     */
    fun getWidgetFontResource(context: Context): Int? {
        val prefs = context.getSharedPreferences("widget_settings", Context.MODE_PRIVATE)
        val fontFamilyOrdinal = prefs.getInt("widget_font_family", AppFontFamily.DEFAULT.ordinal)
        val fontFamily = AppFontFamily.entries.getOrNull(fontFamilyOrdinal) ?: AppFontFamily.DEFAULT
        
        return when (fontFamily) {
            AppFontFamily.DEFAULT -> null // Use system default
            AppFontFamily.BITCOUNT -> R.font.bitcount_prop_single_regular
            AppFontFamily.QUICKSAND -> R.font.quicksand_regular
            AppFontFamily.BALOO_BHAI -> R.font.baloo_bhai2_regular
        }
    }
    
    /**
     * Get the selected font family
     */
    fun getSelectedFontFamily(context: Context): AppFontFamily {
        val prefs = context.getSharedPreferences("widget_settings", Context.MODE_PRIVATE)
        val fontFamilyOrdinal = prefs.getInt("widget_font_family", AppFontFamily.DEFAULT.ordinal)
        return AppFontFamily.entries.getOrNull(fontFamilyOrdinal) ?: AppFontFamily.DEFAULT
    }
    
    /**
     * Check if custom font is being used (not system default)
     */
    fun isUsingCustomFont(context: Context): Boolean {
        return getSelectedFontFamily(context) != AppFontFamily.DEFAULT
    }
}
