package com.material.you.widgets.glance.yearprogresss

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.unit.ColorProvider
import com.material.you.widgets.glance.common.WidgetAppearanceHelper
import java.util.Calendar

/**
 * GitHub-style year heatmap: 7 rows (Sun..Sat) x 53 week columns.
 * Past days fill in lightest -> darkest, future days stay empty.
 */
class YearProgressWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val yearData = calculateYearProgress()
        provideContent {
            YearProgressContent(context = context, yearData = yearData)
        }
    }

    data class YearData(
        val currentYear: Int,
        val currentDayOfYear: Int,
        val totalDaysInYear: Int
    )

    companion object {
        fun calculateYearProgress(): YearData {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            return YearData(
                currentYear = year,
                currentDayOfYear = calendar.get(Calendar.DAY_OF_YEAR),
                totalDaysInYear = if (isLeapYear(year)) 366 else 365
            )
        }

        private fun isLeapYear(year: Int): Boolean =
            (year % 4 == 0 && year % 100 != 0) || year % 400 == 0
    }

    class Receiver : GlanceAppWidgetReceiver() {
        override val glanceAppWidget = YearProgressWidget()
    }
}

@Composable
private fun YearProgressContent(
    context: Context,
    yearData: YearProgressWidget.YearData
) {
    val appearance = WidgetAppearanceHelper.getWidgetAppearance(context)
    val size = LocalSize.current
    val density = context.resources.displayMetrics.density
    val padding = 8f

    val bitmap = renderYearHeatmap(
        widthPx = ((size.width.value - padding * 2) * density).toInt(),
        heightPx = ((size.height.value - padding * 2) * density).toInt(),
        yearData = yearData,
        emptyColor = resolveColor(appearance.colorScheme.surfaceVariant, context),
        lightColor = resolveColor(appearance.colorScheme.primaryContainer, context),
        darkColor = resolveColor(appearance.colorScheme.primary, context)
    )

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(appearance.colorScheme.surface)
            .cornerRadius(appearance.cornerRadius.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            provider = ImageProvider(bitmap),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = GlanceModifier.fillMaxSize().padding(padding.dp)
        )
    }
}

/** Works whether the theme gives a Compose Color or a Glance ColorProvider. */
private fun resolveColor(value: Any, context: Context): Color = when (value) {
    is Color -> value
    is ColorProvider -> value.getColor(context)
    else -> error("Unsupported color type: ${value::class}")
}

private fun renderYearHeatmap(
    widthPx: Int,
    heightPx: Int,
    yearData: YearProgressWidget.YearData,
    emptyColor: Color,
    lightColor: Color,
    darkColor: Color
): Bitmap {
    // Keep the bitmap under the RemoteViews memory limit
    val maxPixels = 600_000f
    val scale = minOf(1f, maxPixels / (widthPx.toFloat() * heightPx).coerceAtLeast(1f))
    val w = (widthPx * scale).toInt().coerceAtLeast(106)
    val h = (heightPx * scale).toInt().coerceAtLeast(14)

    val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    val columns = 53
    val rows = 7
    val gapRatio = 0.2f

    // Square cells: fit by width and by height, use the smaller one
    val cellByWidth = w / (columns + (columns - 1) * gapRatio)
    val cellByHeight = h / (rows + (rows - 1) * gapRatio)
    val cell = minOf(cellByWidth, cellByHeight)
    val gap = cell * gapRatio
    val radius = cell * 0.18f

    val gridW = columns * cell + (columns - 1) * gap
    val gridH = rows * cell + (rows - 1) * gap
    val offsetX = (w - gridW) / 2f
    val offsetY = (h - gridH) / 2f

    // Weekday of Jan 1 (Sunday = 0)
    val jan1 = Calendar.getInstance().apply {
        set(yearData.currentYear, Calendar.JANUARY, 1)
    }
    val startOffset = jan1.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY

    val rect = RectF()
    val span = (yearData.currentDayOfYear - 1).coerceAtLeast(1)

    for (week in 0 until columns) {
        for (row in 0 until rows) {
            val dayOfYear = week * 7 + row - startOffset + 1
            if (dayOfYear < 1 || dayOfYear > yearData.totalDaysInYear) continue

            val color = if (dayOfYear <= yearData.currentDayOfYear) {
                // Jan 1 = lightest, today = darkest
                lerp(lightColor, darkColor, (dayOfYear - 1f) / span)
            } else {
                emptyColor
            }

            val left = offsetX + week * (cell + gap)
            val top = offsetY + row * (cell + gap)
            rect.set(left, top, left + cell, top + cell)
            paint.color = color.toArgb()
            canvas.drawRoundRect(rect, radius, radius, paint)
        }
    }
    return bitmap
}