package com.material.you.widgets.worker

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

/**
 * WorkManagerScheduler - Manages periodic background updates.
 *
 * Schedules:
 * - Calendar pre-fetch: every 1 hour
 * - Wellbeing widgets: every 30 minutes
 *
 * ### Fix: removed blocking `.get()` call
 * The previous [areUpdatesScheduled] used `ListenableFuture.get()` which blocks
 * the calling thread.  Called from the main thread this would trigger a strict-mode
 * violation and potentially an ANR.  The method is now a `suspend` function that
 * uses WorkManager's Flow / coroutine adapter instead.
 */
object WorkManagerScheduler {

    private const val WELLBEING_WORK_NAME = "wellbeing_periodic_refresh"
    private const val CALENDAR_WORK_NAME = "calendar_periodic_refresh"
    private const val AUTO_UPDATE_KEY = "auto_update_widgets"

    // ---- Scheduling ---------------------------------------------------------

    /**
     * Schedule all periodic background updates.
     * Idempotent — safe to call on every app start ([ExistingPeriodicWorkPolicy.KEEP]).
     * Honors the user's "Auto Update Widgets" preference; when disabled this is a no-op
     * so re-scheduling paths (app start, boot) cannot silently re-enable it.
     */
    fun schedulePeriodicUpdates(context: Context) {
        val autoUpdateEnabled = com.material.you.widgets.data.repository.RepositoryProvider
            .preferences
            .getBoolean(AUTO_UPDATE_KEY, true)
        if (!autoUpdateEnabled) {
            android.util.Log.d(TAG, "Auto update disabled by user — skipping scheduling")
            return
        }
        scheduleWellbeingRefresh(context)
        scheduleCalendarRefresh(context)
        android.util.Log.d(TAG, "Periodic updates scheduled")
    }

    private fun scheduleWellbeingRefresh(context: Context) {
        val request = PeriodicWorkRequestBuilder<DataRefreshWorker>(
            repeatInterval = 30,
            repeatIntervalTimeUnit = TimeUnit.MINUTES,
            flexTimeInterval = 10,
            flexTimeIntervalUnit = TimeUnit.MINUTES
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .setInputData(workDataOf("refresh_type" to "wellbeing"))
            .addTag(DataRefreshWorker.TAG_WELLBEING)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, WorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WELLBEING_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
        android.util.Log.d(TAG, "Wellbeing refresh scheduled (30 min interval)")
    }

    private fun scheduleCalendarRefresh(context: Context) {
        // Read user's preferred refresh interval
        val repository = com.material.you.widgets.data.repository.RepositoryProvider.preferences
        val intervalStr = repository.getCalendarRefreshInterval()
        val intervalMinutes = intervalStr.toLongOrNull() ?: 30L
        
        val request = PeriodicWorkRequestBuilder<DataRefreshWorker>(
            repeatInterval = intervalMinutes,
            repeatIntervalTimeUnit = TimeUnit.MINUTES,
            flexTimeInterval = (intervalMinutes / 3).coerceAtLeast(15L),
            flexTimeIntervalUnit = TimeUnit.MINUTES
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .setInputData(workDataOf("refresh_type" to "calendar"))
            .addTag(DataRefreshWorker.TAG_CALENDAR)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, WorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            CALENDAR_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE, // REPLACE to apply new interval
            request
        )
        android.util.Log.d(TAG, "Calendar refresh scheduled ($intervalMinutes min interval)")
    }

    // ---- One-shot triggers --------------------------------------------------

    /** Trigger an immediate wellbeing refresh. */
    fun refreshWellbeingNow(context: Context) {
        val request = OneTimeWorkRequestBuilder<DataRefreshWorker>()
            .setInputData(workDataOf("refresh_type" to "wellbeing"))
            .addTag("wellbeing_immediate")
            .build()

        WorkManager.getInstance(context).enqueue(request)
        android.util.Log.d(TAG, "Immediate wellbeing refresh triggered")
    }

    /** Trigger an immediate calendar refresh. */
    fun refreshCalendarNow(context: Context) {
        val request = OneTimeWorkRequestBuilder<DataRefreshWorker>()
            .setInputData(workDataOf("refresh_type" to "calendar"))
            .addTag("calendar_immediate")
            .build()

        WorkManager.getInstance(context).enqueue(request)
        android.util.Log.d(TAG, "Immediate calendar refresh triggered")
    }

    // ---- Cancellation -------------------------------------------------------

    /** Cancel all scheduled periodic background updates. */
    fun cancelAllUpdates(context: Context) {
        WorkManager.getInstance(context).apply {
            cancelUniqueWork(WELLBEING_WORK_NAME)
            cancelUniqueWork(CALENDAR_WORK_NAME)
        }
        android.util.Log.d(TAG, "All scheduled updates cancelled")
    }

    // ---- Status query -------------------------------------------------------

    /**
     * Returns `true` if both periodic work chains are ENQUEUED or RUNNING.
     *
     * **Suspend function** — must be called from a coroutine.
     * Uses WorkManager's `await()` extension instead of blocking `.get()`,
     * which previously could cause ANRs when called from the main thread.
     */
    suspend fun areUpdatesScheduled(context: Context): Boolean {
        val wm = WorkManager.getInstance(context)

        val wellbeingInfos = wm.getWorkInfosForUniqueWork(WELLBEING_WORK_NAME).await()
        val calendarInfos = wm.getWorkInfosForUniqueWork(CALENDAR_WORK_NAME).await()

        val active = setOf(WorkInfo.State.ENQUEUED, WorkInfo.State.RUNNING)
        val wellbeingOk = wellbeingInfos.any { it.state in active }
        val calendarOk = calendarInfos.any { it.state in active }

        android.util.Log.d(TAG, "areUpdatesScheduled: wellbeing=$wellbeingOk, calendar=$calendarOk")
        return wellbeingOk && calendarOk
    }

    private const val TAG = "WorkManagerScheduler"
}
