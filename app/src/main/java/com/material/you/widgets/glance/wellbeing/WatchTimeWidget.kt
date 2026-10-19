package com.material.you.widgets.glance.wellbeing

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.appwidget.AppWidgetManager
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ColorFilter
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.material.you.widgets.R
import com.material.you.widgets.glance.common.WidgetAppearanceHelper
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Away Time Widget (3x2)
 * 
 * Simple focus: How long have you been away from your phone?
 * Shows the current break duration and longest break today.
 * 
 * Philosophy: Celebrate phone-free moments, not track usage.
 */
class WatchTimeWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val awayStats = calculateAwayTime(context)
        provideContent {
            GlanceTheme {
                WatchTimeContent(context, awayStats)
            }
        }
    }

    data class AwayStats(
        val currentBreakMinutes: Long,
        val longestBreakMinutes: Long,
        val hasUsageAccess: Boolean
    )

    companion object {
        private const val TAG = "WatchTimeWidget"
        
        // Threshold: screen off for more than 5 minutes = "away"
        private const val AWAY_THRESHOLD_MS = 5 * 60 * 1000L

        fun hasUsageAccess(context: Context): Boolean {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager
                ?: return false
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOps.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName,
                )
            } else {
                @Suppress("DEPRECATION")
                appOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName,
                )
            }
            return mode == AppOpsManager.MODE_ALLOWED
        }

        fun calculateAwayTime(context: Context): AwayStats {
            val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager

            if (!hasUsageAccess(context)) {
                return AwayStats(0, 0, false)
            }

            val calendar = Calendar.getInstance()
            val endTime = calendar.timeInMillis
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startTime = calendar.timeInMillis

            if (usageStatsManager == null) {
                return AwayStats(0, 0, false)
            }

            // Query events
            val events = usageStatsManager.queryEvents(startTime, endTime)
            
            if (events == null || !events.hasNextEvent()) {
                return AwayStats(0, 0, true)
            }

            var longestBreak = 0L
            var currentBreak = 0L
            var lastScreenOffTime = 0L
            var screenIsOff = false

            // SCREEN_INTERACTIVE / SCREEN_NON_INTERACTIVE were added in API 30.
            // On API 24-29 fall back to foreground/background transitions as a
            // proxy for screen state (background ≈ screen off).
            val isModernApi = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
            val screenOnEvent = if (isModernApi) UsageEvents.Event.SCREEN_INTERACTIVE
            else @Suppress("DEPRECATION") UsageEvents.Event.MOVE_TO_FOREGROUND
            val screenOffEvent = if (isModernApi) UsageEvents.Event.SCREEN_NON_INTERACTIVE
            else @Suppress("DEPRECATION") UsageEvents.Event.MOVE_TO_BACKGROUND

            val event = UsageEvents.Event()
            while (events.hasNextEvent()) {
                events.getNextEvent(event)

                when (event.eventType) {
                    screenOffEvent -> {
                        // Screen turned off
                        lastScreenOffTime = event.timeStamp
                        screenIsOff = true
                    }
                    screenOnEvent -> {
                        // Screen turned on
                        if (screenIsOff && lastScreenOffTime > 0) {
                            val breakDuration = event.timeStamp - lastScreenOffTime
                            if (breakDuration > AWAY_THRESHOLD_MS) {
                                longestBreak = maxOf(longestBreak, breakDuration)
                            }
                        }
                        screenIsOff = false
                        lastScreenOffTime = 0
                    }
                }
            }

            // Calculate current break if screen is still off
            if (screenIsOff && lastScreenOffTime > 0) {
                currentBreak = System.currentTimeMillis() - lastScreenOffTime
                if (currentBreak > AWAY_THRESHOLD_MS) {
                    longestBreak = maxOf(longestBreak, currentBreak)
                }
            }

            return AwayStats(
                currentBreakMinutes = TimeUnit.MILLISECONDS.toMinutes(currentBreak),
                longestBreakMinutes = TimeUnit.MILLISECONDS.toMinutes(longestBreak),
                hasUsageAccess = true
            )
        }

        fun formatTime(minutes: Long): String {
            val hours = minutes / 60
            val mins = minutes % 60
            return when {
                hours == 0L -> "${mins}m"
                mins == 0L -> "${hours}h"
                else -> "${hours}h ${mins}m"
            }
        }
    }

    class Receiver : GlanceAppWidgetReceiver() {
        override val glanceAppWidget = WatchTimeWidget()

        override fun onUpdate(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray
        ) {
            super.onUpdate(context, appWidgetManager, appWidgetIds)
        }
    }
}

@Composable
private fun WatchTimeContent(context: Context, stats: WatchTimeWidget.AwayStats) {
    val appearance = WidgetAppearanceHelper.getWidgetAppearance(context)
    val settingsIntent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(appearance.backgroundColor)
            .cornerRadius(appearance.cornerRadius.dp)
            .padding(16.dp)
            .clickable(actionStartActivity(settingsIntent)),
        contentAlignment = Alignment.Center
    ) {
        if (!stats.hasUsageAccess) {
            // Permission not granted
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    provider = ImageProvider(R.drawable.settings_24px),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(GlanceTheme.colors.primary),
                    modifier = GlanceModifier.size(32.dp),
                )
                Spacer(modifier = GlanceModifier.height(8.dp))
                Text(
                    text = "Usage Access Required",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = "Tap to grant permission",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }
        } else {
            // Permission granted - show away time
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Header
                Text(
                    text = "Away Time",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = GlanceModifier.height(16.dp))

                // Main metric: Current break
                if (stats.currentBreakMinutes >= 5) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🌱",
                            style = TextStyle(fontSize = 32.sp)
                        )
                        Spacer(modifier = GlanceModifier.height(8.dp))
                        Text(
                            text = WatchTimeWidget.formatTime(stats.currentBreakMinutes),
                            style = TextStyle(
                                color = GlanceTheme.colors.primary,
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        )
                        Spacer(modifier = GlanceModifier.height(4.dp))
                        Text(
                            text = "Current Break",
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurfaceVariant,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                } else {
                    // Not currently on a break
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📱",
                            style = TextStyle(fontSize = 32.sp)
                        )
                        Spacer(modifier = GlanceModifier.height(8.dp))
                        Text(
                            text = "Using Phone",
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurface,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }

                Spacer(modifier = GlanceModifier.height(20.dp))

                // Secondary metric: Longest break today
                if (stats.longestBreakMinutes > 0) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Longest Today",
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        )
                        Spacer(modifier = GlanceModifier.height(2.dp))
                        Text(
                            text = WatchTimeWidget.formatTime(stats.longestBreakMinutes),
                            style = TextStyle(
                                color = GlanceTheme.colors.tertiary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }
    }
}
