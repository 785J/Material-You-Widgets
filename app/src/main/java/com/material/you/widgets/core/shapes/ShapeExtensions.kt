package com.material.you.widgets.core.shapes

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import com.material.you.widgets.data.models.IconShape

/**
 * Universal Shape Extensions
 * Provides extension functions to use shapes across UI and widgets
 */

// ============ Compose UI Extensions ============

/**
 * Convert RoundedPolygon to Compose Shape
 * Used in Compose UI for clipping, borders, backgrounds
 */
class RoundedPolygonShape(private val polygon: RoundedPolygon) : Shape {
    
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = ShapeRenderer.renderToPath(
            polygon = polygon,
            width = size.width,
            height = size.height,
            maintainAspectRatio = true
        )
        return Outline.Generic(path.asComposePath())
    }
}

/**
 * Extension: Convert RoundedPolygon to Compose Shape
 */
fun RoundedPolygon.toComposeShape(): Shape = RoundedPolygonShape(this)

// ============ Glance Widget Extensions ============

/**
 * Extension: Render RoundedPolygon to Bitmap for Glance ImageProvider
 */
fun RoundedPolygon.toBitmap(
    context: Context,
    sizeDp: Int,
    color: Int,
    stretchToFill: Boolean = false
): Bitmap {
    val density = context.resources.displayMetrics.density
    val sizePx = (sizeDp * density).toInt()
    return ShapeRenderer.renderToBitmap(
        polygon = this,
        widthPx = sizePx,
        heightPx = sizePx,
        color = color,
        stretchToFill = stretchToFill
    )
}

/**
 * Extension: Render RoundedPolygon to Bitmap with custom dimensions
 */
fun RoundedPolygon.toBitmap(
    context: Context,
    widthDp: Int,
    heightDp: Int,
    color: Int,
    stretchToFill: Boolean = false
): Bitmap {
    val density = context.resources.displayMetrics.density
    val widthPx = (widthDp * density).toInt()
    val heightPx = (heightDp * density).toInt()
    return ShapeRenderer.renderToBitmap(
        polygon = this,
        widthPx = widthPx,
        heightPx = heightPx,
        color = color,
        stretchToFill = stretchToFill
    )
}

// ============ IconShape Mapping ============

/**
 * Universal IconShape to Compose Shape converter
 * Single source of truth for all shape definitions
 */
fun IconShape.toShape(): Shape {
    return when (this) {
        IconShape.CIRCLE -> CircleShape
        IconShape.ROUNDED_RECTANGLE -> RoundedCornerShape(28.dp)
        IconShape.SQUARE -> RoundedCornerShape(8.dp)
        IconShape.OVAL -> ShapeDefinitions.oval.toComposeShape()
        IconShape.PILL -> ShapeDefinitions.pill.toComposeShape()
        IconShape.GHOSTISH -> ShapeDefinitions.ghostish.toComposeShape()
        IconShape.PENTAGON -> ShapeDefinitions.pentagon.toComposeShape()
        IconShape.HEXAGON -> ShapeDefinitions.regularPolygon(6).toComposeShape()
        IconShape.HEPTAGON -> ShapeDefinitions.regularPolygon(7).toComposeShape()
        IconShape.OCTAGON -> ShapeDefinitions.regularPolygon(8).toComposeShape()
        IconShape.COOKIE -> ShapeDefinitions.cookie6Sided.toComposeShape()
        IconShape.BUN -> ShapeDefinitions.bun.toComposeShape()
    }
}

/**
 * Get RoundedPolygon for IconShape
 * Used when you need the actual polygon (e.g., for Glance widgets)
 */
fun IconShape.toRoundedPolygon(): RoundedPolygon {
    return when (this) {
        IconShape.CIRCLE -> ShapeDefinitions.circle()
        IconShape.ROUNDED_RECTANGLE -> RoundedPolygon(
            numVertices = 4,
            rounding = CornerRounding(0.4f)
        )
        IconShape.SQUARE -> RoundedPolygon(
            numVertices = 4,
            rounding = CornerRounding(0.1f)
        )
        IconShape.OVAL -> ShapeDefinitions.oval
        IconShape.PILL -> ShapeDefinitions.pill
        IconShape.GHOSTISH -> ShapeDefinitions.ghostish
        IconShape.PENTAGON -> ShapeDefinitions.pentagon
        IconShape.HEXAGON -> ShapeDefinitions.regularPolygon(6)
        IconShape.HEPTAGON -> ShapeDefinitions.regularPolygon(7)
        IconShape.OCTAGON -> ShapeDefinitions.regularPolygon(8)
        IconShape.COOKIE -> ShapeDefinitions.cookie6Sided
        IconShape.BUN -> ShapeDefinitions.bun
    }
}

/**
 * Check if shape should be shown in appearance settings
 */
fun IconShape.isSelectableInSettings(): Boolean {
    return when (this) {
        IconShape.ROUNDED_RECTANGLE,
        IconShape.SQUARE -> false // Hide basic shapes
        else -> true // Show all other shapes
    }
}

/**
 * Get display name for IconShape
 */
fun IconShape.displayName(): String {
    return when (this) {
        IconShape.CIRCLE -> "Circle"
        IconShape.ROUNDED_RECTANGLE -> "Rounded Rectangle"
        IconShape.GHOSTISH -> "Ghostish"
        IconShape.SQUARE -> "Square"
        IconShape.PENTAGON -> "Pentagon"
        IconShape.HEXAGON -> "Hexagon"
        IconShape.HEPTAGON -> "Heptagon"
        IconShape.OCTAGON -> "Octagon"
        IconShape.OVAL -> "Oval"
        IconShape.PILL -> "Pill"
        IconShape.COOKIE -> "Cookie"
        IconShape.BUN -> "Bun"
    }
}
