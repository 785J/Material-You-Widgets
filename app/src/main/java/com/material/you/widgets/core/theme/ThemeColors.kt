package com.material.you.widgets.core.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.kyant.monet.TonalPalettes
import com.kyant.monet.TonalPalettes.Companion.toTonalPalettes
import io.material.hct.Hct

/**
 * Universal Color System
 * Single source of truth for all colors across UI and widgets
 */
object ThemeColors {
    
    // ============ Brand Colors ============
    
    // Primary: Modern Teal-Cyan for trust, reliability, and freshness
    val primary = Color(0xFF00897B) // Teal 600
    val primaryLight = Color(0xFF4DB6AC) // Teal 300
    val primaryDark = Color(0xFF004D40) // Teal 900
    
    // Secondary: Warm accent for action and energy
    val secondary = Color(0xFFFF7043) // Deep Orange 400
    val secondaryLight = Color(0xFFFFAB91) // Deep Orange 200
    val secondaryDark = Color(0xFFBF360C) // Deep Orange 900
    
    // Tertiary: Complementary purple for depth
    val tertiary = Color(0xFF7E57C2) // Deep Purple 400
    val tertiaryLight = Color(0xFFB39DDB) // Deep Purple 200
    val tertiaryDark = Color(0xFF4527A0) // Deep Purple 900
    
    // ============ Semantic Colors ============
    
    val success = Color(0xFF2E7D32) // Green 800
    val warning = Color(0xFFF9A825) // Yellow 800
    val error = Color(0xFFC62828) // Red 800
    val info = Color(0xFF1565C0) // Blue 800
    
    // ============ Surface Colors ============
    
    val surfaceLight = Color(0xFFFAFAFA)
    val surfaceDark = Color(0xFF121212)
    val surfaceContainerLight = Color(0xFFF5F5F5)
    val surfaceContainerDark = Color(0xFF1E1E1E)
    
    // ============ Preset Colors (from Seal app) ============
    
    object Presets {
        val color0 = Color(0xFF9C4146) // Red
        val color1 = Color(0xFF9C5D41) // Orange-Red
        val color2 = Color(0xFF9C7A41) // Orange
        val color3 = Color(0xFF9C9641) // Yellow
        val color4 = Color(0xFF829C41) // Yellow-Green
        val color5 = Color(0xFF669C41) // Green
        val color6 = Color(0xFF419C46) // Green-Cyan
        val color7 = Color(0xFF419C62) // Cyan-Green
        val color8 = Color(0xFF419C7E) // Cyan
        val color9 = Color(0xFF419C9A) // Cyan-Blue
        
        val all = listOf(
            color0, color1, color2, color3, color4,
            color5, color6, color7, color8, color9
        )
        
        val defaultSeed = Color(0xFF6750A4) // Purple (Material You default)
        val dynamic = Color(0x00000000) // Special value for dynamic colors
        val monochrome = Color(0xFF000000) // Black for monochrome theme
    }
    
    // ============ Color Scheme Creation ============
    
    fun createCustomColorScheme(
        darkTheme: Boolean,
        seedColor: Color,
    ): ColorScheme {
        val palettes: TonalPalettes = seedColor.toTonalPalettes()
        
        return if (darkTheme) {
            darkColorScheme(
                background = palettes neutral1 6.0,
                inverseOnSurface = palettes neutral1 20.0,
                inversePrimary = palettes accent1 40.0,
                inverseSurface = palettes neutral1 90.0,
                onBackground = palettes neutral1 90.0,
                onPrimary = palettes accent1 20.0,
                onPrimaryContainer = palettes accent1 90.0,
                onSecondary = palettes accent2 20.0,
                onSecondaryContainer = palettes accent2 90.0,
                onSurface = palettes neutral1 90.0,
                onSurfaceVariant = palettes neutral2 80.0,
                onTertiary = palettes accent3 20.0,
                onTertiaryContainer = palettes accent3 90.0,
                outline = palettes neutral2 60.0,
                outlineVariant = palettes neutral2 30.0,
                primary = palettes accent1 80.0,
                primaryContainer = palettes accent1 30.0,
                secondary = palettes accent2 80.0,
                secondaryContainer = palettes accent2 30.0,
                surface = palettes neutral1 6.0,
                surfaceVariant = palettes neutral2 30.0,
                tertiary = palettes accent3 80.0,
                tertiaryContainer = palettes accent3 30.0,
                surfaceBright = palettes neutral1 24.0,
                surfaceDim = palettes neutral1 6.0,
                surfaceContainerLowest = palettes neutral1 4.0,
                surfaceContainerLow = palettes neutral1 10.0,
                surfaceContainer = palettes neutral1 12.0,
                surfaceContainerHigh = palettes neutral1 17.0,
                surfaceContainerHighest = palettes neutral1 22.0,
            )
        } else {
            lightColorScheme(
                background = palettes neutral1 98.0,
                inverseOnSurface = palettes neutral1 95.0,
                inversePrimary = palettes accent1 80.0,
                inverseSurface = palettes neutral1 20.0,
                onBackground = palettes neutral1 10.0,
                onPrimary = palettes accent1 100.0,
                onPrimaryContainer = palettes accent1 10.0,
                onSecondary = palettes accent2 100.0,
                onSecondaryContainer = palettes accent2 10.0,
                onSurface = palettes neutral1 10.0,
                onSurfaceVariant = palettes neutral2 30.0,
                onTertiary = palettes accent3 100.0,
                onTertiaryContainer = palettes accent3 10.0,
                outline = palettes neutral2 50.0,
                outlineVariant = palettes neutral2 80.0,
                primary = palettes accent1 40.0,
                primaryContainer = palettes accent1 90.0,
                secondary = palettes accent2 40.0,
                secondaryContainer = palettes accent2 90.0,
                surface = palettes neutral1 98.0,
                surfaceVariant = palettes neutral2 90.0,
                tertiary = palettes accent3 40.0,
                tertiaryContainer = palettes accent3 90.0,
                surfaceBright = palettes neutral1 98.0,
                surfaceDim = palettes neutral1 87.0,
                surfaceContainerLowest = palettes neutral1 100.0,
                surfaceContainerLow = palettes neutral1 96.0,
                surfaceContainer = palettes neutral1 94.0,
                surfaceContainerHigh = palettes neutral1 92.0,
                surfaceContainerHighest = palettes neutral1 90.0,
            )
        }
    }
    
    fun createMonochromeColorScheme(darkTheme: Boolean): ColorScheme {
        return if (darkTheme) {
            darkColorScheme(
                primary = Color(0xFFE0E0E0),
                onPrimary = Color(0xFF121212),
                primaryContainer = Color(0xFF2A2A2A),
                onPrimaryContainer = Color(0xFFFAFAFA),
                secondary = Color(0xFFC0C0C0),
                onSecondary = Color(0xFF121212),
                secondaryContainer = Color(0xFF333333),
                onSecondaryContainer = Color(0xFFFAFAFA),
                tertiary = Color(0xFF9E9E9E),
                onTertiary = Color(0xFF121212),
                tertiaryContainer = Color(0xFF3A3A3A),
                onTertiaryContainer = Color(0xFFFAFAFA),
                background = Color(0xFF121212),
                onBackground = Color(0xFFE0E0E0),
                surface = Color(0xFF0F0F0F),
                onSurface = Color(0xFFE0E0E0),
                surfaceVariant = Color(0xFF1B1B1B),
                onSurfaceVariant = Color(0xFFB0B0B0),
                outline = Color(0xFF5A5A5A),
                outlineVariant = Color(0xFF3A3A3A),
                inverseSurface = Color(0xFFE0E0E0),
                inverseOnSurface = Color(0xFF121212),
                inversePrimary = Color(0xFF121212),
                surfaceBright = Color(0xFF2A2A2A),
                surfaceDim = Color(0xFF0A0A0A),
                surfaceContainerLowest = Color(0xFF060606),
                surfaceContainerLow = Color(0xFF121212),
                surfaceContainer = Color(0xFF161616),
                surfaceContainerHigh = Color(0xFF1F1F1F),
                surfaceContainerHighest = Color(0xFF2A2A2A),
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF2A2A2A),
                onPrimary = Color(0xFFFAFAFA),
                primaryContainer = Color(0xFFE8E8E8),
                onPrimaryContainer = Color(0xFF121212),
                secondary = Color(0xFF4A4A4A),
                onSecondary = Color(0xFFFAFAFA),
                secondaryContainer = Color(0xFFF0F0F0),
                onSecondaryContainer = Color(0xFF121212),
                tertiary = Color(0xFF6A6A6A),
                onTertiary = Color(0xFFFAFAFA),
                tertiaryContainer = Color(0xFFE0E0E0),
                onTertiaryContainer = Color(0xFF121212),
                background = Color(0xFFFAFAFA),
                onBackground = Color(0xFF1A1A1A),
                surface = Color(0xFFFAFAFA),
                onSurface = Color(0xFF1A1A1A),
                surfaceVariant = Color(0xFFF0F0F0),
                onSurfaceVariant = Color(0xFF4A4A4A),
                outline = Color(0xFF7A7A7A),
                outlineVariant = Color(0xFFD0D0D0),
                inverseSurface = Color(0xFF1A1A1A),
                inverseOnSurface = Color(0xFFFAFAFA),
                inversePrimary = Color(0xFFE0E0E0),
                surfaceBright = Color(0xFFFAFAFA),
                surfaceDim = Color(0xFFE6E6E6),
                surfaceContainerLowest = Color(0xFFFFFFFF),
                surfaceContainerLow = Color(0xFFF7F7F7),
                surfaceContainer = Color(0xFFF2F2F2),
                surfaceContainerHigh = Color(0xFFEEEEEE),
                surfaceContainerHighest = Color(0xFFE8E8E8),
            )
        }
    }
    
    fun pureBlack(colorScheme: ColorScheme): ColorScheme =
        colorScheme.copy(
            surface = Color.Black,
            background = Color.Black,
            surfaceContainerLowest = Color.Black,
            surfaceContainerLow = Color.Black,
            surfaceContainer = Color.Black,
            surfaceContainerHigh = Color.Black,
            surfaceContainerHighest = Color.Black,
        )
}

// ============ Color Utilities ============

@Composable
fun Number.autoDark(isDarkTheme: Boolean = LocalDarkTheme.current.isDarkTheme): Double =
    if (!isDarkTheme) {
        this.toDouble()
    } else {
        when (this.toDouble()) {
            6.0 -> 98.0
            10.0 -> 99.0
            20.0 -> 95.0
            25.0 -> 90.0
            30.0 -> 90.0
            40.0 -> 80.0
            50.0 -> 60.0
            60.0 -> 50.0
            70.0 -> 40.0
            80.0 -> 40.0
            90.0 -> 30.0
            95.0 -> 20.0
            98.0 -> 10.0
            99.0 -> 10.0
            100.0 -> 20.0
            else -> this.toDouble()
        }
    }

/**
 * Generate label color using HCT algorithm
 */
@Composable
@ReadOnlyComposable
fun Int.generateLabelColor(): Color = Color(
    Hct.from(hue = (this % 360).toDouble(), chroma = 36.0, tone = 80.0).toInt()
)

/**
 * Generate on-label color using HCT algorithm
 */
@ReadOnlyComposable
fun Int.generateOnLabelColor(): Color = Color(
    Hct.from(hue = (this % 360).toDouble(), chroma = 36.0, tone = 20.0).toInt()
)

val ErrorTonalPalettes = Color.Red.toTonalPalettes()

const val DEFAULT_SEED_COLOR = 0x00897B // Material Widgets Teal brand color
