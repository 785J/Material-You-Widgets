package com.material.you.widgets.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar

data class CalendarEvent(
    val id: Long,
    val title: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val color: Int,
    val allDay: Boolean = false
)

/**
 * CalendarDataProvider - Optimized calendar sync with intelligent caching
 *
 * Features:
 * - Batch query: Fetches entire month in one query (not per-day)
 * - Smart caching: Caches month data for 1 hour
 * - Fast lookups: Pre-indexed events by date
 * - Background-safe: Can be called from widgets and WorkManager workers
 * - **Thread-safe**: A [Mutex] guards all cache reads and writes so concurrent
 *   widget updates cannot see a partially-written cache map.
 */
object CalendarDataProvider {

    // Cache settings
    private const val CALENDAR_CACHE_DURATION_MS = 60 * 60 * 1000L // 1 hour

    /**
     * Coroutine mutex that serialises all access to [cachedEventsByMonth].
     * This prevents race conditions when multiple widget instances (or the
     * background DataRefreshWorker) call [getMonthData] concurrently.
     */
    private val cacheMutex = Mutex()

    // Calendar event cache (keyed by month: "2026-08")
    private val cachedEventsByMonth = mutableMapOf<String, CachedMonthData>()
    
    private data class CachedMonthData(
        val events: Map<LocalDate, List<CalendarEvent>>, // Events indexed by date
        val timestamp: Long
    )
    
    fun hasCalendarPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Get events for specific date (optimized with month cache).
     * Thread-safe: delegates to the mutex-protected [getMonthData].
     * Optionally filters all-day events based on user preference.
     */
    suspend fun getEventsForDate(
        context: Context, 
        date: LocalDate,
        includeAllDay: Boolean = true
    ): List<CalendarEvent> {
        if (!hasCalendarPermission(context)) return emptyList()
        val monthData = getMonthData(context, date.year, date.monthValue)
        val events = monthData[date] ?: emptyList()
        return if (includeAllDay) {
            events
        } else {
            events.filter { !it.allDay }
        }
    }

    /**
     * Get today's events (uses cached month data for fast lookup).
     * Optionally filters all-day events based on user preference.
     */
    suspend fun getTodayEvents(
        context: Context,
        includeAllDay: Boolean = true
    ): List<CalendarEvent> {
        if (!hasCalendarPermission(context)) return emptyList()
        return getEventsForDate(context, LocalDate.now(), includeAllDay)
    }

    /**
     * Get event count for specific date (optimized with month cache).
     */
    suspend fun getEventCountForDate(context: Context, date: LocalDate): Int {
        if (!hasCalendarPermission(context)) return 0
        val monthData = getMonthData(context, date.year, date.monthValue)
        return monthData[date]?.size ?: 0
    }
    
    /**
     * Get all events for entire month (uses cache, single DB query).
     * Mutex-protected: concurrent callers queue behind each other rather than
     * racing to fetch + cache the same data simultaneously.
     */
    private suspend fun getMonthData(
        context: Context,
        year: Int,
        month: Int
    ): Map<LocalDate, List<CalendarEvent>> {
        val monthKey = "${year}-${month.toString().padStart(2, '0')}"

        // Fast path: check cache under the mutex
        cacheMutex.withLock {
            val cached = cachedEventsByMonth[monthKey]
            if (cached != null) {
                val age = System.currentTimeMillis() - cached.timestamp
                if (age < CALENDAR_CACHE_DURATION_MS) {
                    android.util.Log.d(TAG, "Cache hit for $monthKey (age: ${age / 1000}s)")
                    return cached.events
                }
                android.util.Log.d(TAG, "Cache expired for $monthKey")
            }
        }

        // Slow path: fetch from content resolver (outside the mutex so other
        // months can be served from cache while this query runs)
        android.util.Log.d(TAG, "Fetching fresh month data for $monthKey…")
        val events = fetchMonthEvents(context, year, month)

        // Write result back under the mutex
        cacheMutex.withLock {
            cachedEventsByMonth[monthKey] = CachedMonthData(
                events = events,
                timestamp = System.currentTimeMillis()
            )
        }

        android.util.Log.d(TAG, "Cached ${events.values.sumOf { it.size }} events for $monthKey")
        return events
    }
    
    /**
     * Fetch all event *instances* for a month in a single query.
     *
     * Uses [CalendarContract.Instances] rather than [CalendarContract.Events]:
     * the Events table stores only the first occurrence of a recurring event,
     * so querying it silently drops every repeat. Instances pre-expands
     * recurrences (and events defined with DURATION instead of DTEND) into
     * concrete occurrences within the requested range.
     */
    private fun fetchMonthEvents(
        context: Context,
        year: Int,
        month: Int
    ): Map<LocalDate, List<CalendarEvent>> {
        val eventsByDate = mutableMapOf<LocalDate, MutableList<CalendarEvent>>()

        try {
            val calendar = Calendar.getInstance()
            calendar.set(year, month - 1, 1, 0, 0, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startMillis = calendar.timeInMillis

            calendar.add(Calendar.MONTH, 1)
            val endMillis = calendar.timeInMillis

            val projection = arrayOf(
                CalendarContract.Instances.EVENT_ID,
                CalendarContract.Instances.TITLE,
                CalendarContract.Instances.BEGIN,
                CalendarContract.Instances.END,
                CalendarContract.Instances.EVENT_COLOR,
                CalendarContract.Instances.CALENDAR_COLOR,
                CalendarContract.Instances.ALL_DAY
            )

            // Instances URI path segments limit the query to the [begin, end] range
            val instancesUri = CalendarContract.Instances.CONTENT_URI.buildUpon()
                .appendPath(startMillis.toString())
                .appendPath(endMillis.toString())
                .build()

            val cursor: Cursor? = context.contentResolver.query(
                instancesUri,
                projection,
                null,
                null,
                "${CalendarContract.Instances.BEGIN} ASC"
            )

            cursor?.use {
                val idIndex = it.getColumnIndex(CalendarContract.Instances.EVENT_ID)
                val titleIndex = it.getColumnIndex(CalendarContract.Instances.TITLE)
                val startIndex = it.getColumnIndex(CalendarContract.Instances.BEGIN)
                val endIndex = it.getColumnIndex(CalendarContract.Instances.END)
                val eventColorIndex = it.getColumnIndex(CalendarContract.Instances.EVENT_COLOR)
                val calendarColorIndex = it.getColumnIndex(CalendarContract.Instances.CALENDAR_COLOR)
                val allDayIndex = it.getColumnIndex(CalendarContract.Instances.ALL_DAY)

                while (it.moveToNext()) {
                    val id = it.getLong(idIndex)
                    val title = it.getString(titleIndex) ?: "Untitled"
                    val beginMillis = it.getLong(startIndex)
                    val endMillisInstance = it.getLong(endIndex)
                    val allDay = if (allDayIndex >= 0) it.getInt(allDayIndex) == 1 else false

                    // All-day instances are stored at local-midnight-in-UTC; reading
                    // them in UTC preserves the calendar date. Timed events use the
                    // system timezone.
                    val zoneId = if (allDay) ZoneId.of("UTC") else ZoneId.systemDefault()

                    val startTime = LocalDateTime.ofInstant(
                        java.time.Instant.ofEpochMilli(beginMillis),
                        zoneId
                    )
                    // END can be 0/absent for degenerate rows — fall back to a
                    // single-day occurrence instead of dropping the event.
                    val endTime = if (endMillisInstance > beginMillis) {
                        LocalDateTime.ofInstant(
                            java.time.Instant.ofEpochMilli(endMillisInstance),
                            zoneId
                        )
                    } else {
                        startTime.plusDays(1)
                    }

                    // EVENT_COLOR is null when the event uses its calendar's color.
                    val color = when {
                        eventColorIndex >= 0 && !it.isNull(eventColorIndex) -> it.getInt(eventColorIndex)
                        calendarColorIndex >= 0 && !it.isNull(calendarColorIndex) -> it.getInt(calendarColorIndex)
                        else -> 0xFF1976D2.toInt()
                    }

                    val event = CalendarEvent(
                        id = id,
                        title = title,
                        startTime = startTime,
                        endTime = endTime,
                        color = color,
                        allDay = allDay
                    )

                    // Index the event on every date it spans within the month.
                    val monthStart = LocalDate.of(year, month, 1)
                    val monthEnd = monthStart.plusMonths(1).minusDays(1)

                    var currentDate = startTime.toLocalDate().let {
                        if (it.isBefore(monthStart)) monthStart else it
                    }
                    // All-day events have an EXCLUSIVE end (midnight of the day
                    // after the last day), so a 1-day all-day event on the 5th
                    // ends on the 5th, not the 6th.
                    var lastDate = endTime.toLocalDate().let {
                        if (allDay) it.minusDays(1) else it
                    }.let {
                        if (it.isAfter(monthEnd)) monthEnd else it
                    }
                    if (lastDate.isBefore(currentDate)) lastDate = currentDate

                    while (!currentDate.isAfter(lastDate)) {
                        eventsByDate.getOrPut(currentDate) { mutableListOf() }.add(event)
                        currentDate = currentDate.plusDays(1)
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error fetching month events", e)
        }

        return eventsByDate
    }
    
    /**
     * Get event count map for entire month (optimized).
     * Returns map of day → event count.
     */
    suspend fun getEventCountForMonth(context: Context, year: Int, month: Int): Map<Int, Int> {
        if (!hasCalendarPermission(context)) return emptyMap()
        val monthData = getMonthData(context, year, month)
        return monthData.mapKeys { (date, _) -> date.dayOfMonth }
            .mapValues { (_, events) -> events.size }
    }

    /**
     * Pre-fetch and cache calendar data for current and next month.
     * Call from a background coroutine for smooth widget updates.
     */
    suspend fun prefetchCalendarData(context: Context) {
        if (!hasCalendarPermission(context)) return
        val now = LocalDate.now()
        getMonthData(context, now.year, now.monthValue)
        val next = now.plusMonths(1)
        getMonthData(context, next.year, next.monthValue)
        android.util.Log.d(TAG, "Pre-fetched calendar data for current and next month")
    }

    /**
     * Clear calendar cache (useful for manual refresh).
     */
    suspend fun clearCache() {
        cacheMutex.withLock { cachedEventsByMonth.clear() }
        android.util.Log.d(TAG, "Calendar cache cleared")
    }

    /**
     * Force refresh calendar data for current month.
     */
    suspend fun refreshCalendar(context: Context) {
        val now = LocalDate.now()
        val monthKey = "${now.year}-${now.monthValue.toString().padStart(2, '0')}"
        cacheMutex.withLock { cachedEventsByMonth.remove(monthKey) }
        getMonthData(context, now.year, now.monthValue)
        android.util.Log.d(TAG, "Calendar refreshed for current month")
    }
    
    /**
     * Format event time for display
     */
    fun formatEventTime(event: CalendarEvent): String {
        if (event.allDay) return "All day"
        val formatter = java.time.format.DateTimeFormatter.ofPattern("h:mm a")
        return "${event.startTime.format(formatter)} - ${event.endTime.format(formatter)}"
    }

    private const val TAG = "CalendarDataProvider"
}
