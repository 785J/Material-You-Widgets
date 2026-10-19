package com.material.you.widgets.glance.calendar

/**
 * Pure grid math for month calendars (Sunday-first, 7 columns).
 * Shared by [MediumCalendarWidget] and [LargeCalendarWidget] so the row
 * calculation lives in exactly one tested place.
 */
object CalendarGridUtils {

    /**
     * Number of week rows needed to render a month in a Sunday-first grid.
     *
     * @param daysInMonth days in the month (28..31)
     * @param startDayOfWeek column index the 1st falls on, 0 = Sunday,
     *   1..6 = Monday..Saturday (i.e. `LocalDate.dayOfWeek.value % 7`)
     */
    fun requiredWeekRows(daysInMonth: Int, startDayOfWeek: Int): Int {
        require(daysInMonth in 1..31) { "daysInMonth must be 1..31, was $daysInMonth" }
        require(startDayOfWeek in 0..6) { "startDayOfWeek must be 0..6, was $startDayOfWeek" }
        return (daysInMonth + startDayOfWeek + 6) / 7
    }
}
