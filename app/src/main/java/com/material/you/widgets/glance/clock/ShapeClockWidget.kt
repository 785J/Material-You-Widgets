package com.material.you.widgets.glance.clock

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.wrapContentSize
import com.material.you.widgets.glance.common.WidgetAppearanceHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ShapeClockWidget — Material You clock widget.
 *
 * Reverse-engineered from com.appslab.materialyou.widgetspro:
 *   - ShapeClock.java: renders HH:mm or hh:mm via TextBitmapCreator (Canvas text → Bitmap)
 *   - ClockNTrans.java: hour + minute bitmaps stacked with 53% margin offset,
 *     date at 10% size, font=popins_bold (index 15) for digits, fixa/minute colors
 *
 * Glance adaptation:
 *   - Clock face drawn on Canvas as a styled rounded-card bitmap (to work around
 *     Glance's lack of custom drawing within RemoteViews)
 *   - TIME_TICK / TIME_SET / DATE_CHANGED / TIMEZONE_CHANGED drive per-minute updates
 *   - Integrates with existing WidgetAppearanceHelper for Material 3 dynamic colors
 */
class ShapeClockWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val clockInfo = readClockInfo(context)
        provideContent {
            GlanceTheme {
                ClockContent(context, clockInfo)
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Data model
    // ─────────────────────────────────────────────────────────────────────────

    data class ClockInfo(
        val hours: String,
        val minutes: String,
        val dateLabel: String,
        val is24Hour: Boolean
    )

    companion object {
        private const val TAG = "ShapeClockWidget"

        /**
         * Read current time. Mirrors ShapeClock.b() + ClockNTrans.b():
         *   - Hours: "HH" or "hh"
         *   - Minutes: "mm"
         *   - Date: "EEE, dd MMM"  (same default chosen in ClockNTrans)
         */
        fun readClockInfo(context: Context): ClockInfo {
            val prefs = context.getSharedPreferences("widget_settings", Context.MODE_PRIVATE)
            val is24 = prefs.getBoolean("widget_24_hour_clock", false)

            val hourFmt = SimpleDateFormat(if (is24) "HH" else "hh", Locale.getDefault())
            val minFmt  = SimpleDateFormat("mm", Locale.getDefault())
            val dateFmt = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
            val now = Date()

            return ClockInfo(
                hours      = hourFmt.format(now),
                minutes    = minFmt.format(now),
                dateLabel  = dateFmt.format(now),
                is24Hour   = is24
            )
        }

        // ─────────────────────────────────────────────────────────────────────
        // Canvas rendering — replicates TextBitmapCreator + shape background
        // ─────────────────────────────────────────────────────────────────────

        /**
         * Draw the full clock card as a single Bitmap.
         *
         * Design decoded from decompilation:
         *   - Card: rounded square, Material You surface color
         *   - Hours digit: 90% of widget size, bold, onSurface color (top-left offset at 53%)
         *   - Minutes digit: 90% of widget size, bold, primary color   (bottom-right at 53%)
         *   - Date label: 10% of widget size, regular, onSurfaceVariant
         *   - Charging arc / secondary decoration at bottom
         */
        fun renderClockBitmap(context: Context, info: ClockInfo, widthPx: Int, heightPx: Int): Bitmap {
            val size = minOf(widthPx, heightPx).coerceAtLeast(100)
            val bmp  = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bmp)

            val isDark = WidgetAppearanceHelper.isDarkMode(context)

            // ── Background card ───────────────────────────────────────────────
            val bgColor  = if (isDark) Color.parseColor("#1C1B1F") else Color.parseColor("#F4EFF4")
            val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = bgColor }
            val cornerR  = size * 0.22f
            canvas.drawRoundRect(RectF(0f, 0f, size.toFloat(), size.toFloat()), cornerR, cornerR, cardPaint)

            // ── Accent arc decoration (mimics circular shape border) ──────────
            val accentColor = if (isDark) Color.parseColor("#D0BCFF") else Color.parseColor("#6750A4")
            val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = accentColor
                style = Paint.Style.STROKE
                strokeWidth = size * 0.028f
                alpha = 60
            }
            val arcInset = size * 0.04f
            canvas.drawRoundRect(
                RectF(arcInset, arcInset, size - arcInset, size - arcInset),
                cornerR - arcInset, cornerR - arcInset, arcPaint
            )

            // ── Hours text ────────────────────────────────────────────────────
            // Replicates: TextBitmapCreator(context, strQ, 0.9*fMin, 15/*typeface*/, 11/*color*/, true/*bold*/)
            // typeface 15 = fonts/poppins_bold via setPaintTypeface, color 11 = fontFixed
            val hourColor   = if (isDark) Color.parseColor("#E6E1E5") else Color.parseColor("#1C1B1F")
            val hourSize    = size * 0.42f   // Big bold digit
            val hourPaint   = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color    = hourColor
                textSize = hourSize
                typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)
                isFakeBoldText = true
            }
            // Position: upper-left area, offset down 53% of size (from ClockNTrans margin logic)
            val hourX = size * 0.06f
            val hourY = size * 0.55f
            canvas.drawText(info.hours, hourX, hourY, hourPaint)

            // ── Minutes text ──────────────────────────────────────────────────
            // color 14 = R.color.minute (accent/primary tinted)
            val minColor  = accentColor
            val minSize   = size * 0.42f
            val minPaint  = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color    = minColor
                textSize = minSize
                typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)
                isFakeBoldText = true
            }
            // Position: lower-right, below the hour (margin 53% from right)
            val minTextWidth = minPaint.measureText(info.minutes)
            val minX = size - minTextWidth - size * 0.06f
            val minY = size * 0.92f
            canvas.drawText(info.minutes, minX, minY, minPaint)

            // ── Separator colon ───────────────────────────────────────────────
            val colonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color    = hourColor
                textSize = size * 0.12f
                typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)
                alpha    = 180
            }
            canvas.drawText(":", size * 0.44f, size * 0.60f, colonPaint)

            // ── Date label ────────────────────────────────────────────────────
            // Replicates: TextBitmapCreator(context, strQ3, fMin*0.1, 15, 9/*buttonIcons*/, false)
            val dateColor = if (isDark) Color.parseColor("#CAC4D0") else Color.parseColor("#49454F")
            val datePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color    = dateColor
                textSize = size * 0.10f
                typeface = Typeface.create("sans-serif", Typeface.NORMAL)
            }
            val dateY = size * 0.20f
            canvas.drawText(info.dateLabel, hourX, dateY, datePaint)

            // ── Small dot separators ──────────────────────────────────────────
            val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = accentColor
                alpha = 120
            }
            canvas.drawCircle(size * 0.5f, size * 0.25f, size * 0.018f, dotPaint)

            return bmp
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Glance composable
    // ─────────────────────────────────────────────────────────────────────────

    @Composable
    fun ClockContent(context: Context, info: ClockInfo) {
        val size = LocalSize.current
        val density = context.resources.displayMetrics.density
        val widthPx  = (size.width.value  * density).toInt().coerceAtLeast(100)
        val heightPx = (size.height.value * density).toInt().coerceAtLeast(100)

        val clockBitmap = renderClockBitmap(context, info, widthPx, heightPx)
        val bitmapSizePx = minOf(widthPx, heightPx)
        val bitmapSizeDp = (bitmapSizePx / density).dp

        Box(
            modifier = GlanceModifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                provider     = ImageProvider(clockBitmap),
                contentDescription = "Clock: ${info.hours}:${info.minutes}",
                modifier     = GlanceModifier.size(bitmapSizeDp)
            )
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Receiver — mirrors ShapeClock's TIME_TICK registration pattern exactly
    // ─────────────────────────────────────────────────────────────────────────

    class Receiver : GlanceAppWidgetReceiver() {
        override val glanceAppWidget = ShapeClockWidget()

        private val updateScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        /** Dynamic receiver registered at runtime (like original) — handles per-minute ticks */
        private var timeTickReceiver: BroadcastReceiver? = null

        override fun onEnabled(context: Context) {
            super.onEnabled(context)
            registerTimeReceiver(context)
        }

        override fun onDisabled(context: Context) {
            super.onDisabled(context)
            unregisterTimeReceiver(context)
        }

        override fun onUpdate(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray
        ) {
            super.onUpdate(context, appWidgetManager, appWidgetIds)
            // Re-register in case the process restarted (matches ShapeClock.onUpdate pattern)
            registerTimeReceiver(context)
            triggerUpdate(context)
        }

        override fun onReceive(context: Context, intent: Intent) {
            super.onReceive(context, intent)
            when (intent.action) {
                Intent.ACTION_TIME_TICK,
                Intent.ACTION_TIME_CHANGED,
                Intent.ACTION_DATE_CHANGED,
                Intent.ACTION_TIMEZONE_CHANGED -> triggerUpdate(context)
            }
        }

        private fun triggerUpdate(context: Context) {
            updateScope.launch {
                try {
                    ShapeClockWidget().updateAll(context)
                } catch (e: Exception) {
                    android.util.Log.e(TAG, "Clock update failed", e)
                }
            }
        }

        private fun registerTimeReceiver(context: Context) {
            if (timeTickReceiver != null) return
            timeTickReceiver = object : BroadcastReceiver() {
                override fun onReceive(ctx: Context, intent: Intent) {
                    triggerUpdate(ctx)
                }
            }
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_TIME_TICK)
                addAction(Intent.ACTION_TIME_CHANGED)
                addAction(Intent.ACTION_DATE_CHANGED)
                addAction(Intent.ACTION_TIMEZONE_CHANGED)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.applicationContext.registerReceiver(
                    timeTickReceiver, filter, Context.RECEIVER_NOT_EXPORTED
                )
            } else {
                context.applicationContext.registerReceiver(timeTickReceiver, filter)
            }
        }

        private fun unregisterTimeReceiver(context: Context) {
            timeTickReceiver?.let {
                try {
                    context.applicationContext.unregisterReceiver(it)
                } catch (_: IllegalArgumentException) {}
                timeTickReceiver = null
            }
        }
    }
}
