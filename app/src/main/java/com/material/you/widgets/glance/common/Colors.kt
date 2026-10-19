package com.material.you.widgets.glance.common

import android.content.Context
import android.os.Build
import androidx.compose.ui.graphics.Color

/**
 * Color utilities for widgets
 * Centralized color management with Material You support
 */
object Colors {
    
    /**
     * Get accent color for active state
     */
    fun getAccentColor(context: Context): Color {
        val colorInt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getColor(android.R.color.system_accent1_200)
        } else {
            0xFF66BB6A.toInt()
        }
        return Color(colorInt)
    }
    
    /**
     * Get icon color based on active state
     */
    fun getIconColor(context: Context, isActive: Boolean): Color {
        return if (isActive) {
            getAccentColor(context)
        } else {
            Color(0xFF808080) // Gray for inactive
        }
    }
    
    /**
     * Get text color with opacity
     */
    fun getTextColor(context: Context, alpha: Float = 1f): Color {
        val baseColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getColor(android.R.color.system_neutral1_10)
        } else {
            0xFFFFFFFF.toInt()
        }
        return Color(baseColor).copy(alpha = alpha)
    }
    
    /**
     * Get background color for widgets
     */
    fun getBackgroundColor(context: Context): Color {
        val colorInt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getColor(android.R.color.system_neutral1_900)
        } else {
            0xFF1C1C1C.toInt()
        }
        return Color(colorInt)
    }
}
