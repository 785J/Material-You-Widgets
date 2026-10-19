package com.material.you.widgets.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Universal Shape System for Material3 Shapes
 * Note: For custom polygon shapes (Oval, Pill, etc.), use core.shapes module
 */
object ThemeShapes {
    
    /**
     * Expressive shapes - Modern, playful UI with larger corner radii
     */
    val expressive = Shapes(
        extraSmall = RoundedCornerShape(8.dp), // Tags, chips, small buttons
        small = RoundedCornerShape(12.dp), // Cards, text fields
        medium = RoundedCornerShape(16.dp), // Bottom sheets, dialogs
        large = RoundedCornerShape(24.dp), // FABs, large cards
        extraLarge = RoundedCornerShape(28.dp), // Full screen sheets
    )
    
    /**
     * Default shapes - Professional, subtle rounding
     */
    val default = Shapes(
        extraSmall = RoundedCornerShape(4.dp), // Tags, chips
        small = RoundedCornerShape(8.dp), // Cards, text fields
        medium = RoundedCornerShape(12.dp), // Bottom sheets, dialogs
        large = RoundedCornerShape(16.dp), // FABs, large cards
        extraLarge = RoundedCornerShape(20.dp), // Full screen sheets
    )
}
