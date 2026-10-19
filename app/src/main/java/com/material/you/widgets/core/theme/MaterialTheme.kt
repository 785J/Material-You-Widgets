package com.material.you.widgets.core.theme

import android.os.Build
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextDirection
import com.kyant.monet.TonalPalettes
import com.kyant.monet.TonalPalettes.Companion.toTonalPalettes

/**
 * Universal Material Theme
 * Central theme configuration for the entire app
 */

val DefaultThemeColor = Color(0xFF00897B) // Material Widgets Teal - Professional brand color

@Composable
fun WidgetsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    isHighContrastModeEnabled: Boolean = false,
    useDynamicColor: Boolean = true,
    themeColor: Color = DefaultThemeColor,
    pureBlack: Boolean = false,
    monochromeTheme: Boolean = false,
    expressive: Boolean = false,
    fontFamily: androidx.compose.ui.text.font.FontFamily? = null,
    content: @Composable () -> Unit,
) {
    val view = LocalView.current
    val context = LocalContext.current

    LaunchedEffect(darkTheme) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (darkTheme) {
                view.windowInsetsController?.setSystemBarsAppearance(
                    0,
                    APPEARANCE_LIGHT_STATUS_BARS,
                )
            } else {
                view.windowInsetsController?.setSystemBarsAppearance(
                    APPEARANCE_LIGHT_STATUS_BARS,
                    APPEARANCE_LIGHT_STATUS_BARS,
                )
            }
        }
    }

    val colorScheme =
        when {
            monochromeTheme -> ThemeColors.createMonochromeColorScheme(darkTheme)
            useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            else -> {
                ThemeColors.createCustomColorScheme(darkTheme, themeColor)
            }
        }.let { baseColorScheme ->
            if (darkTheme && pureBlack) {
                ThemeColors.pureBlack(baseColorScheme)
            } else {
                baseColorScheme
            }
        }

    val textStyle =
        LocalTextStyle.current.copy(
            lineBreak = LineBreak.Paragraph,
            textDirection = TextDirection.Content,
        )

    val shapes = if (expressive) ThemeShapes.expressive else ThemeShapes.default
    
    val typography = ThemeTypography.create(fontFamily)

    val tonalPalettes = colorScheme.toTonalPalettes()
    val fixedColorRoles = FixedColorRoles.fromTonalPalettes(tonalPalettes)

    CompositionLocalProvider(
        LocalFixedColorRoles provides fixedColorRoles,
        LocalDarkTheme provides DarkTheme(darkTheme, isHighContrastModeEnabled),
        LocalTonalPalettes provides tonalPalettes,
        LocalTextStyle provides textStyle,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = shapes,
            content = content,
        )
    }
}

@Composable
@Deprecated("Use WidgetsTheme instead", replaceWith = ReplaceWith("WidgetsTheme(content)"))
fun PreviewThemeLight(content: @Composable () -> Unit) {
    WidgetsTheme(
        darkTheme = false,
        themeColor = DefaultThemeColor,
        content = content,
    )
}
