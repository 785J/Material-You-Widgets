package com.material.you.widgets.glance.common

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.graphics.shapes.RoundedPolygon
import com.material.you.widgets.core.shapes.ShapeDefinitions
import com.material.you.widgets.core.shapes.ShapeRenderer

/**
 * Shape rendering utilities for widgets
 * Centralized place for all shape-related rendering logic
 */
object Shapes {
    
    /**
     * Render pill shape (for 2×2 control widgets)
     */
    fun renderPill(
        context: Context,
        isActive: Boolean,
        appearance: WidgetAppearanceHelper.WidgetAppearance,
        widthDp: Int = 110,
        heightDp: Int = 110
    ): Bitmap {
        return renderShape(
            context = context,
            shape = ShapeDefinitions.pill,
            isActive = isActive,
            appearance = appearance,
            widthDp = widthDp,
            heightDp = heightDp
        )
    }
    
    /**
     * Render oval/cookie shape (for ringer mode icons)
     */
    fun renderCookie(
        context: Context,
        appearance: WidgetAppearanceHelper.WidgetAppearance,
        sizeDp: Int = 40
    ): Bitmap {
        val density = context.resources.displayMetrics.density
        val sizePx = (sizeDp * density).toInt()
        
        val accentColor = getAccentColor(context)
        // 20% opacity for cookie background
        val cookieColor = (accentColor and 0x00FFFFFF) or (0x33 shl 24)
        
        return ShapeRenderer.renderToBitmap(
            polygon = ShapeDefinitions.oval,
            widthPx = sizePx,
            heightPx = sizePx,
            color = cookieColor,
            stretchToFill = true
        )
    }
    
    /**
     * Generic shape renderer with appearance settings
     */
    private fun renderShape(
        context: Context,
        shape: RoundedPolygon,
        isActive: Boolean,
        appearance: WidgetAppearanceHelper.WidgetAppearance,
        widthDp: Int,
        heightDp: Int
    ): Bitmap {
        val density = context.resources.displayMetrics.density
        val widthPx = (widthDp * density).toInt()
        val heightPx = (heightDp * density).toInt()
        
        val color = if (isActive) {
            getAccentColor(context)
        } else {
            getSurfaceColor(context)
        }
        
        // Apply opacity
        val alpha = (appearance.opacity * 255).toInt().coerceIn(0, 255)
        val colorWithAlpha = (color and 0x00FFFFFF) or (alpha shl 24)
        
        return ShapeRenderer.renderToBitmap(
            polygon = shape,
            widthPx = widthPx,
            heightPx = heightPx,
            color = colorWithAlpha,
            stretchToFill = true
        )
    }
    
    /**
     * Get accent color (Material You primary)
     */
    private fun getAccentColor(context: Context): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getColor(android.R.color.system_accent1_200)
        } else {
            0xFF66BB6A.toInt() // Green fallback
        }
    }
    
    /**
     * Get surface color (for inactive state)
     */
    private fun getSurfaceColor(context: Context): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getColor(android.R.color.system_neutral1_800)
        } else {
            0xFF2D2D2D.toInt() // Dark gray fallback
        }
    }
}
