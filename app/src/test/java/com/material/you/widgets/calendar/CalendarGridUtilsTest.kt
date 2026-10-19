package com.material.you.widgets.calendar

import com.material.you.widgets.glance.calendar.CalendarGridUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class CalendarGridUtilsTest {
    // All grids below are Sunday-first (column 0 = Sunday), matching the
    // widgets' day-name header which starts with "SUN".

    @Test
    fun `month with 31 days starting on Saturday needs six rows`() {
        assertEquals(6, CalendarGridUtils.requiredWeekRows(31, 6))
    }

    @Test
    fun `month with 30 days starting on Sunday fits five rows`() {
        assertEquals(5, CalendarGridUtils.requiredWeekRows(30, 0))
    }

    @Test
    fun `month with 28 days starting on Monday needs five rows in a Sunday-first grid`() {
        // Feb 2021 layout: the 28th falls on a Sunday and lands alone in the
        // fifth row (Sun is column 0, so a Monday start consumes row 0 from
        // column 1 and every 4th Sunday opens a new row).
        assertEquals(5, CalendarGridUtils.requiredWeekRows(28, 1))
    }

    @Test
    fun `month with 28 days starting on Sunday fits four rows`() {
        assertEquals(4, CalendarGridUtils.requiredWeekRows(28, 0))
    }

    @Test
    fun `month with 31 days starting on Sunday fits five rows`() {
        // Rows: 1-7, 8-14, 15-21, 22-28, 29-31.
        assertEquals(5, CalendarGridUtils.requiredWeekRows(31, 0))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invalid day count is rejected`() {
        CalendarGridUtils.requiredWeekRows(32, 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invalid start day is rejected`() {
        CalendarGridUtils.requiredWeekRows(30, 7)
    }
}
