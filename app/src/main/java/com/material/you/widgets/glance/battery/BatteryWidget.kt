package com.material.you.widgets.glance.battery

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ColorFilter
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.material.you.widgets.R
import com.material.you.widgets.glance.common.WidgetAppearanceHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/** Shows battery state in a compact Material-toned status card. */
class BatteryWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val batteryInfo = readBatteryInfo(context)
        provideContent {
            GlanceTheme {
                BatteryContent(context, batteryInfo)
            }
        }
    }

    data class BatteryInfo(
        val percentage: Int,
        val isCharging: Boolean
    )

    companion object {
        private const val TAG = "BatteryWidget"

        fun readBatteryInfo(context: Context): BatteryInfo {
            val status: Intent? = context.registerReceiver(
                null,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            )

            val level = status?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = status?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            val pct = if (level >= 0 && scale > 0) (level * 100 / scale.toFloat()).toInt() else 0

            val chargingStatus = status?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            val isCharging = chargingStatus == BatteryManager.BATTERY_STATUS_CHARGING ||
                    chargingStatus == BatteryManager.BATTERY_STATUS_FULL

            return BatteryInfo(pct, isCharging)
        }
        
        fun getDeviceName(): String {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.startsWith(manufacturer, ignoreCase = true)) {
                model.capitalize()
            } else {
                "${manufacturer.capitalize()} $model"
            }
        }
    }

    class Receiver : GlanceAppWidgetReceiver() {
        override val glanceAppWidget = BatteryWidget()

        override fun onUpdate(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray
        ) {
            super.onUpdate(context, appWidgetManager, appWidgetIds)
            BatteryListenerManager.registerBatteryListener(context)
        }

        override fun onEnabled(context: Context) {
            super.onEnabled(context)
            BatteryListenerManager.registerBatteryListener(context)
        }

        override fun onDisabled(context: Context) {
            BatteryListenerManager.unregisterBatteryListener(context)
            super.onDisabled(context)
        }
    }
    
    /**
     * Application-scoped battery listener manager.
     * Ensures only one receiver instance is registered with the application context,
     * preventing duplicate registrations across widget updates.
     */
    private object BatteryListenerManager {
        private val updateScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        @Volatile
        private var batteryReceiver: BroadcastReceiver? = null

        /**
         * Last (percentage, charging) pair pushed to the widgets.
         * ACTION_BATTERY_CHANGED fires on every ~1% step, temperature change and
         * charge tick — recomposing the widget for identical values wastes IPC
         * and battery, so identical updates are skipped.
         */
        @Volatile
        private var lastRendered: BatteryInfo? = null

        @Synchronized
        fun registerBatteryListener(context: Context) {
            if (batteryReceiver != null) return

            try {
                val receiver = object : BroadcastReceiver() {
                    override fun onReceive(ctx: Context, intent: Intent) {
                        when (intent.action) {
                            Intent.ACTION_BATTERY_CHANGED,
                            Intent.ACTION_POWER_CONNECTED,
                            Intent.ACTION_POWER_DISCONNECTED,
                            Intent.ACTION_BATTERY_LOW,
                            Intent.ACTION_BATTERY_OKAY -> {
                                updateScope.launch {
                                    try {
                                        val info = readBatteryInfo(ctx)
                                        val last = lastRendered
                                        if (info != last) {
                                            lastRendered = info
                                            BatteryWidget().updateAll(ctx)
                                        }
                                    } catch (e: Exception) {
                                        android.util.Log.e(TAG, "Battery update failed", e)
                                    }
                                }
                            }
                        }
                    }
                }

                val filter = IntentFilter().apply {
                    addAction(Intent.ACTION_BATTERY_CHANGED)
                    addAction(Intent.ACTION_POWER_CONNECTED)
                    addAction(Intent.ACTION_POWER_DISCONNECTED)
                    addAction(Intent.ACTION_BATTERY_LOW)
                    addAction(Intent.ACTION_BATTERY_OKAY)
                }

                context.applicationContext.registerReceiver(receiver, filter)
                batteryReceiver = receiver
                android.util.Log.d(TAG, "Battery listener registered")
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Failed to register battery listener", e)
            }
        }

        @Synchronized
        fun unregisterBatteryListener(context: Context) {
            batteryReceiver?.let { receiver ->
                try {
                    context.applicationContext.unregisterReceiver(receiver)
                    batteryReceiver = null
                    lastRendered = null
                    android.util.Log.d(TAG, "Battery listener unregistered")
                } catch (e: Exception) {
                    android.util.Log.e(TAG, "Failed to unregister battery listener", e)
                }
            }
        }
    }
}

@Composable
private fun BatteryContent(context: Context, batteryInfo: BatteryWidget.BatteryInfo) {
    val appearance = WidgetAppearanceHelper.getWidgetAppearance(context)
    val percentage = batteryInfo.percentage.coerceIn(0, 100)
    val meterWidth = (LocalSize.current.width - 28.dp).coerceAtLeast(0.dp)
    val levelWidth = meterWidth * (percentage / 100f)
    val batteryColor = when {
        percentage >= 50 -> appearance.colorScheme.primary
        percentage >= 20 -> appearance.colorScheme.tertiary
        else -> appearance.colorScheme.secondary
    }
    val levelLabel = when {
        percentage <= 20 -> "Low"
        percentage >= 95 -> "Almost full"
        else -> "Battery level"
    }

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(appearance.colorScheme.surface)
            .cornerRadius(appearance.cornerRadius.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "BATTERY",
                modifier = GlanceModifier.defaultWeight(),
                style = TextStyle(
                    color = appearance.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            )
            if (batteryInfo.isCharging) {
                Image(
                    provider = ImageProvider(R.drawable.bolt_24px),
                    contentDescription = "Charging",
                    colorFilter = ColorFilter.tint(appearance.colorScheme.primary),
                    modifier = GlanceModifier.size(16.dp),
                )
                Spacer(GlanceModifier.width(4.dp))
                Text(
                    text = "Charging",
                    style = TextStyle(
                        color = appearance.colorScheme.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }
        }

        Column(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$percentage%",
                    style = TextStyle(
                        color = appearance.colorScheme.onSurface,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )
                Spacer(GlanceModifier.width(8.dp))
                Text(
                    text = levelLabel,
                    modifier = GlanceModifier.padding(bottom = 7.dp),
                    style = TextStyle(
                        color = appearance.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                    ),
                )
            }

            Spacer(GlanceModifier.height(8.dp))

            Box(
                modifier = GlanceModifier
                    .width(meterWidth)
                    .height(8.dp)
                    .background(appearance.colorScheme.surfaceVariant)
                    .cornerRadius(4.dp),
            ) {
                Box(
                    modifier = GlanceModifier
                        .width(levelWidth)
                        .fillMaxHeight()
                        .background(batteryColor)
                        .cornerRadius(4.dp),
                ) {}
            }

            Spacer(GlanceModifier.height(8.dp))
            Text(
                text = if (batteryInfo.isCharging) "Power connected" else "On battery",
                style = TextStyle(
                    color = appearance.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                ),
            )
        }

        Spacer(GlanceModifier.defaultWeight())
    }
}

private fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}
