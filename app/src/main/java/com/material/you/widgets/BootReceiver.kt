package com.material.you.widgets

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.material.you.widgets.glance.battery.BatteryWidget
import com.material.you.widgets.glance.calendar.LargeCalendarWidget
import com.material.you.widgets.glance.calendar.MediumCalendarWidget
import com.material.you.widgets.glance.clock.ShapeClockWidget
import com.material.you.widgets.glance.wellbeing.WatchTimeWidget
import com.material.you.widgets.glance.yearprogresss.YearProgressWidget
import com.material.you.widgets.worker.WorkManagerScheduler

/**
 * Boot / package-replace receiver.
 *
 * Why this is needed
 * ------------------
 * Dynamically-registered BroadcastReceivers (e.g. the battery listener in
 * [BatteryWidget.Receiver]) live only as long as the process is alive.
 * After a device reboot (or an APK update) those receivers are gone.
 *
 * The AppWidget framework does NOT automatically call [onEnabled] again after a
 * reboot — it only calls [onUpdate].  However [onUpdate] in our Glance receivers
 * triggers the dynamic-registration helpers, which is all we need.
 *
 * This receiver therefore calls [AppWidgetManager.getAppWidgetIds] for every
 * widget type and fires a synthetic APPWIDGET_UPDATE broadcast so each Receiver
 * re-registers its dynamic listeners and refreshes its UI.
 *
 * It also re-schedules WorkManager periodic tasks, which are also lost on reboot
 * for older WorkManager versions (< 2.6).
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_MY_PACKAGE_REPLACED
        ) return

        android.util.Log.d(TAG, "Boot/package-replace received — re-initialising widgets")

        val awm = AppWidgetManager.getInstance(context)

        // For each Glance receiver class, send a synthetic APPWIDGET_UPDATE so
        // the receiver's onUpdate() runs and re-registers any dynamic listeners.
        val receiverClasses = listOf(
            BatteryWidget.Receiver::class.java,
            MediumCalendarWidget.Receiver::class.java,
            LargeCalendarWidget.Receiver::class.java,
            WatchTimeWidget.Receiver::class.java,
            YearProgressWidget.Receiver::class.java,
            ShapeClockWidget.Receiver::class.java
        )

        for (cls in receiverClasses) {
            try {
                val componentName = android.content.ComponentName(context, cls)
                val ids = awm.getAppWidgetIds(componentName)
                if (ids.isNotEmpty()) {
                    val updateIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).apply {
                        component = componentName
                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                    }
                    context.sendBroadcast(updateIntent)
                    android.util.Log.d(TAG, "Sent APPWIDGET_UPDATE to ${cls.simpleName} (${ids.size} instances)")
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Failed to update ${cls.simpleName}", e)
            }
        }

        // Re-schedule WorkManager periodic tasks
        WorkManagerScheduler.schedulePeriodicUpdates(context)
        android.util.Log.d(TAG, "WorkManager tasks re-scheduled")
    }

    companion object {
        private const val TAG = "BootReceiver"
    }
}
