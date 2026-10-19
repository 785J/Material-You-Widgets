package com.material.you.widgets.glance.common

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Widget Update Coordinator
 *
 * Solves the "rapid-fire update" problem: when multiple state changes arrive in
 * quick succession (e.g. several preference keys change during a theme reset, or
 * the ringer mode changes and the system broadcast fires within milliseconds of
 * the user's tap) naïve code calls `updateAll()` once per event, which causes:
 *  - Redundant RemoteViews inflation and IPC round-trips to the launcher.
 *  - Multiple back-to-back recompositions visible as a flicker.
 *  - Unnecessary battery drain.
 *
 * ### Mechanism — debounce + deduplication
 * Requests are accumulated in a **pending set** (keyed by widget class).
 * A **debounce window** (default 150 ms) starts (or resets) on every new request.
 * When the window expires, all pending widget types are updated **once each**,
 * in parallel.  Duplicate requests for the same widget type within the window are
 * collapsed into a single update.
 *
 * ### Usage
 * ```kotlin
 * // Request an update; it will be batched with any other pending requests
 * WidgetUpdateCoordinator.request(context, SoundProfileWidget())
 *
 * // Request several at once (theme change)
 * WidgetUpdateCoordinator.request(
 *     context,
 *     MediumCalendarWidget(), LargeCalendarWidget()
 * )
 *
 * // Force an immediate flush (e.g. when the app is going to the background)
 * WidgetUpdateCoordinator.flushNow(context)
 * ```
 */
object WidgetUpdateCoordinator {

    /** How long to wait after the last request before flushing (ms). */
    private const val DEBOUNCE_MS = 150L

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mutex = Mutex()

    /** Pending updates keyed by widget class name to deduplicate. */
    private val pending = mutableMapOf<String, GlanceAppWidget>()

    /** The currently-scheduled debounce job; cancelled and replaced on each new request. */
    private var debounceJob: Job? = null

    // -------------------------------------------------------------------------

    /**
     * Request an update for one or more widget types.
     * Requests within [DEBOUNCE_MS] of each other are coalesced.
     */
    fun request(context: Context, vararg widgets: GlanceAppWidget) {
        scope.launch {
            mutex.withLock {
                for (widget in widgets) {
                    val key = widget::class.java.name
                    if (!pending.containsKey(key)) {
                        pending[key] = widget
                        android.util.Log.d(TAG, "Queued: ${widget::class.simpleName}")
                    }
                }
                // Cancel any existing debounce timer and start a fresh one
                debounceJob?.cancel()
                debounceJob = scope.launch {
                    delay(DEBOUNCE_MS)
                    flush(context)
                }
            }
        }
    }

    /**
     * Immediately flush all pending updates without waiting for the debounce window.
     * Useful when the app is about to go to the background.
     */
    fun flushNow(context: Context) {
        scope.launch {
            mutex.withLock { debounceJob?.cancel() }
            flush(context)
        }
    }

    // ---- private ------------------------------------------------------------

    private suspend fun flush(context: Context) {
        val toUpdate = mutex.withLock {
            val snapshot = pending.values.toList()
            pending.clear()
            snapshot
        }

        if (toUpdate.isEmpty()) return

        android.util.Log.d(TAG, "Flushing ${toUpdate.size} widget type(s)")

        // Fan out all updates in parallel
        val jobs = toUpdate.map { widget ->
            scope.launch {
                try {
                    widget.updateAll(context)
                    android.util.Log.d(TAG, "Updated: ${widget::class.simpleName}")
                } catch (e: Exception) {
                    android.util.Log.e(TAG, "Failed: ${widget::class.simpleName}", e)
                }
            }
        }
        jobs.forEach { it.join() }
        android.util.Log.d(TAG, "Flush complete")
    }

    private const val TAG = "WidgetUpdateCoordinator"
}
