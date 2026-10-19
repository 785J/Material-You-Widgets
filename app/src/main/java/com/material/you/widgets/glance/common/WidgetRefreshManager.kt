package com.material.you.widgets.glance.common

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll
import android.content.Context
import com.material.you.widgets.glance.battery.BatteryWidget
import com.material.you.widgets.glance.calendar.LargeCalendarWidget
import com.material.you.widgets.glance.calendar.MediumCalendarWidget
import com.material.you.widgets.glance.clock.ShapeClockWidget
import com.material.you.widgets.glance.wellbeing.WatchTimeWidget
import com.material.you.widgets.glance.yearprogresss.YearProgressWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

/**
 * Widget Refresh Manager
 *
 * Handles fast, parallel widget updates when theme or appearance changes.
 *
 * Design improvements over the previous version:
 * - All known widget types are stored in a typed list — no string-based `when` switch.
 * - [refreshAllWidgets] fans out updates concurrently with [async]/[awaitAll] so a
 *   slow widget type does not block the others.
 * - [refreshWidgets] accepts a vararg of *instances* rather than a Class reference,
 *   keeping the call-site type-safe and removing the fragile `simpleName` comparison.
 * - Active widget count query is a proper suspend function (no hidden blocking).
 */
object WidgetRefreshManager {

    private val refreshScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Canonical list of every widget type in the app.
     * Add new widget classes here — the rest of the manager picks them up automatically.
     * 
    * Current widgets:
    * - Calendar widgets (Medium, Large)
     * - Battery widget (enhanced with health, temperature, voltage)
     * - Watch Time widget (digital wellbeing - time saved)
    * - Year Progress widget (365 dots calendar)
     */
    private val allWidgets: List<GlanceAppWidget>
        get() = listOf(
            MediumCalendarWidget(),
            LargeCalendarWidget(),
            BatteryWidget(),
            WatchTimeWidget(),
            YearProgressWidget(),
            ShapeClockWidget()
        )

    /**
     * Public accessor so other components (e.g. [ThemeManager]) can obtain the
     * full widget list without duplicating it.
     */
    fun allWidgetInstances(): List<GlanceAppWidget> = allWidgets

    /**
     * Refresh every placed widget instance, in parallel.
     * Safe to call from any thread; the work runs on [Dispatchers.IO].
     */
    fun refreshAllWidgets(context: Context) {
        refreshScope.launch {
            android.util.Log.d(TAG, "Starting parallel refresh of all widgets")
            allWidgets
                .map { widget ->
                    async {
                        try {
                            widget.updateAll(context)
                            android.util.Log.d(TAG, "Updated: ${widget::class.simpleName}")
                        } catch (e: Exception) {
                            android.util.Log.e(TAG, "Failed: ${widget::class.simpleName}", e)
                        }
                    }
                }
                .awaitAll()
            android.util.Log.d(TAG, "All widgets refreshed")
        }
    }

    /**
     * Refresh a specific set of widget instances.
     *
     * Usage:
     * ```kotlin
    * WidgetRefreshManager.refreshWidgets(context, MediumCalendarWidget(), LargeCalendarWidget())
     * ```
     */
    fun refreshWidgets(context: Context, vararg widgets: GlanceAppWidget) {
        refreshScope.launch {
            widgets.map { widget ->
                async {
                    try {
                        widget.updateAll(context)
                        android.util.Log.d(TAG, "Refreshed: ${widget::class.simpleName}")
                    } catch (e: Exception) {
                        android.util.Log.e(TAG, "Failed to refresh ${widget::class.simpleName}", e)
                    }
                }
            }.awaitAll()
        }
    }

    /**
     * Refresh all placed instances of a single widget class.
     *
     * Usage:
     * ```kotlin
     * WidgetRefreshManager.refreshWidget(context, SoundProfileWidget())
     * ```
     */
    fun refreshWidget(context: Context, widget: GlanceAppWidget) {
        refreshWidgets(context, widget)
    }

    /**
     * Count of all currently placed widget instances across every type.
     * Suspend — safe to call from a coroutine without blocking the caller thread.
     */
    suspend fun getActiveWidgetCount(context: Context): Int {
        return try {
            val manager = GlanceAppWidgetManager(context)
            allWidgets.sumOf { widget ->
                manager.getGlanceIds(widget.javaClass).size
            }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error counting widgets", e)
            0
        }
    }

    private const val TAG = "WidgetRefreshManager"
}
