package com.material.you.widgets.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.sp
import com.material.you.widgets.R

/**
 * Universal Typography System
 * Single source of truth for all typography across the app
 */
@OptIn(ExperimentalTextApi::class)
object ThemeTypography {
    
    // ============ Font Families ============
    
    /**
     * Bitcount Prop Single - Monospace coding font
     */
    val bitcount = FontFamily(
        Font(R.font.bitcount_prop_single_regular, FontWeight.Normal),
        Font(R.font.bitcount_prop_single_regular, FontWeight.W400),
        Font(R.font.bitcount_prop_single_regular, FontWeight.Bold),
        Font(R.font.bitcount_prop_single_regular, FontWeight.W600),
        Font(R.font.bitcount_prop_single_regular, FontWeight.W500),
        Font(R.font.bitcount_prop_single_regular, FontWeight.W700),
        Font(R.font.bitcount_prop_single_regular, FontWeight.W800),
        Font(R.font.bitcount_prop_single_regular, FontWeight.W900),
    )
    
    /**
     * Quicksand - Friendly rounded font
     */
    val quicksand = FontFamily(
        Font(R.font.quicksand_regular, FontWeight.Normal),
        Font(R.font.quicksand_regular, FontWeight.W400),
        Font(R.font.quicksand_bold, FontWeight.Bold),
        Font(R.font.quicksand_bold, FontWeight.W600),
        Font(R.font.quicksand_bold, FontWeight.W500),
        Font(R.font.quicksand_bold, FontWeight.W700),
    )
    
    /**
     * Baloo Bhai 2 - Playful decorative font
     */
    val balooBhai = FontFamily(
        Font(R.font.baloo_bhai2_regular, FontWeight.Normal),
        Font(R.font.baloo_bhai2_regular, FontWeight.W400),
        Font(R.font.baloo_bhai2_bold, FontWeight.Bold),
        Font(R.font.baloo_bhai2_bold, FontWeight.W600),
        Font(R.font.baloo_bhai2_bold, FontWeight.W500),
        Font(R.font.baloo_bhai2_bold, FontWeight.W700),
    )
    
    // ============ Typography Creation ============
    
    /**
     * Create typography with the given font family.
     *
     * @param fontFamily The family to apply, or null for the platform default
     *   (i.e. the "System Default" preference).
     */
    fun create(fontFamily: FontFamily? = null): Typography {
        val selectedFont = fontFamily ?: FontFamily.Default
        
        return Typography().run {
            copy(
                // Display - Large headlines, hero text
                displayLarge = displayLarge.copy(
                    fontFamily = selectedFont,
                    fontSize = 57.sp,
                    fontWeight = FontWeight.W400,
                    lineHeight = 64.sp,
                    letterSpacing = (-0.25).sp,
                    lineBreak = LineBreak.Heading,
                    textDirection = TextDirection.Content
                ),
                displayMedium = displayMedium.copy(
                    fontFamily = selectedFont,
                    fontSize = 45.sp,
                    fontWeight = FontWeight.W400,
                    lineHeight = 52.sp,
                    letterSpacing = 0.sp,
                    lineBreak = LineBreak.Heading,
                    textDirection = TextDirection.Content
                ),
                displaySmall = displaySmall.copy(
                    fontFamily = selectedFont,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.W400,
                    lineHeight = 44.sp,
                    letterSpacing = 0.sp,
                    lineBreak = LineBreak.Heading,
                    textDirection = TextDirection.Content
                ),
                // Headline - Section headers
                headlineLarge = headlineLarge.copy(
                    fontFamily = selectedFont,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.W500,
                    lineHeight = 40.sp,
                    letterSpacing = 0.sp,
                    lineBreak = LineBreak.Heading,
                    textDirection = TextDirection.Content
                ),
                headlineMedium = headlineMedium.copy(
                    fontFamily = selectedFont,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.W500,
                    lineHeight = 36.sp,
                    letterSpacing = 0.sp,
                    lineBreak = LineBreak.Heading,
                    textDirection = TextDirection.Content
                ),
                headlineSmall = headlineSmall.copy(
                    fontFamily = selectedFont,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.W500,
                    lineHeight = 32.sp,
                    letterSpacing = 0.sp,
                    lineBreak = LineBreak.Heading,
                    textDirection = TextDirection.Content
                ),
                // Title - Card titles, app bar titles
                titleLarge = titleLarge.copy(
                    fontFamily = selectedFont,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.W500,
                    lineHeight = 28.sp,
                    letterSpacing = 0.sp,
                    lineBreak = LineBreak.Heading,
                    textDirection = TextDirection.Content
                ),
                titleMedium = titleMedium.copy(
                    fontFamily = selectedFont,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W600,
                    lineHeight = 24.sp,
                    letterSpacing = 0.15.sp,
                    lineBreak = LineBreak.Heading,
                    textDirection = TextDirection.Content
                ),
                titleSmall = titleSmall.copy(
                    fontFamily = selectedFont,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W600,
                    lineHeight = 20.sp,
                    letterSpacing = 0.1.sp,
                    lineBreak = LineBreak.Heading,
                    textDirection = TextDirection.Content
                ),
                // Body - Main content text
                bodyLarge = bodyLarge.copy(
                    fontFamily = selectedFont,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W400,
                    lineHeight = 24.sp,
                    letterSpacing = 0.5.sp,
                    lineBreak = LineBreak.Paragraph,
                    textDirection = TextDirection.Content
                ),
                bodyMedium = bodyMedium.copy(
                    fontFamily = selectedFont,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W400,
                    lineHeight = 20.sp,
                    letterSpacing = 0.25.sp,
                    lineBreak = LineBreak.Paragraph,
                    textDirection = TextDirection.Content
                ),
                bodySmall = bodySmall.copy(
                    fontFamily = selectedFont,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W400,
                    lineHeight = 16.sp,
                    letterSpacing = 0.4.sp,
                    lineBreak = LineBreak.Paragraph,
                    textDirection = TextDirection.Content
                ),
                // Label - Buttons, tabs, captions
                labelLarge = labelLarge.copy(
                    fontFamily = selectedFont,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W500,
                    lineHeight = 20.sp,
                    letterSpacing = 0.1.sp,
                    textDirection = TextDirection.Content
                ),
                labelMedium = labelMedium.copy(
                    fontFamily = selectedFont,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W500,
                    lineHeight = 16.sp,
                    letterSpacing = 0.5.sp,
                    textDirection = TextDirection.Content
                ),
                labelSmall = labelSmall.copy(
                    fontFamily = selectedFont,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.W500,
                    lineHeight = 16.sp,
                    letterSpacing = 0.5.sp,
                    textDirection = TextDirection.Content
                ),
            )
        }
    }
    
    /**
     * Default typography using Bitcount font
     */
    val default: Typography = create(bitcount)
}
