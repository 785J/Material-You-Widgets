package com.material.you.widgets.worker

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.material.you.widgets.data.CalendarDataProvider
import com.material.you.widgets.glance.calendar.LargeCalendarWidget
import com.material.you.widgets.glance.calendar.MediumCalendarWidget
import com.material.you.widgets.glance.battery.BatteryWidget
import com.material.you.widgets.glance.wellbeing.WatchTimeWidget
import com.material.you.widgets.glance.yearprogresss.YearProgressWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * DataRefreshWorker - Background worker for periodic data updates
 * 
 * Features:
 * - Pre-fetches calendar data every hour
 * - Updates all active widgets after refresh
 * - Battery-efficient background execution
 */
class DataRefreshWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    
    companion object {
        const val WORK_NAME = "data_refresh_worker"
        const val TAG_CALENDAR = "calendar_refresh"
        const val TAG_WELLBEING = "wellbeing_refresh"
    }
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("DataRefreshWorker", "Starting background data refresh...")
            
            val refreshType = inputData.getString("refresh_type") ?: "all"
            
            when (refreshType) {
                "calendar" -> refreshCalendar()
                "wellbeing" -> refreshWellbeing()
                "all" -> {
                    refreshCalendar()
                    refreshWellbeing()
                }
            }
            
            android.util.Log.d("DataRefreshWorker", "Background refresh completed successfully")
            Result.success()
            
        } catch (e: Exception) {
            android.util.Log.e("DataRefreshWorker", "Error during background refresh", e)
            // Return retry on failure so WorkManager can apply backoff
            Result.retry()
        }
    }
    
    /**
     * Pre-fetch calendar data and update calendar widgets.
     * Throws exception on failure to signal retry to doWork().
     */
    private suspend fun refreshCalendar() {
        android.util.Log.d("DataRefreshWorker", "Pre-fetching calendar data...")
        
        // Pre-fetch current and next month (batch query)
        CalendarDataProvider.prefetchCalendarData(context)
        
        // Update all calendar widgets
        MediumCalendarWidget().updateAll(context)
        LargeCalendarWidget().updateAll(context)
        
        android.util.Log.d("DataRefreshWorker", "Calendar refresh complete")
    }
    
    /**
     * Refresh wellbeing and battery widgets.
     * Throws exception on failure to signal retry to doWork().
     */
    private suspend fun refreshWellbeing() {
        android.util.Log.d("DataRefreshWorker", "Refreshing wellbeing widgets...")
        
        // Update wellbeing and utility widgets
        WatchTimeWidget().updateAll(context)
        YearProgressWidget().updateAll(context)
        BatteryWidget().updateAll(context)
        
        android.util.Log.d("DataRefreshWorker", "Wellbeing refresh complete")
    }

}
