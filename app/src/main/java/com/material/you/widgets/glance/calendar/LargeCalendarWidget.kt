package com.material.you.widgets.glance.calendar

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.material.you.widgets.core.shapes.ShapeDefinitions
import com.material.you.widgets.core.shapes.ShapeRenderer
import com.material.you.widgets.glance.common.WidgetAppearanceHelper
import com.material.you.widgets.data.CalendarDataProvider
import com.material.you.widgets.data.CalendarEvent
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle as JavaTextStyle
import java.util.*

/**
 * Large Calendar Widget (5x4) for Home Screen
 * Beautiful full month view with real Google Calendar events
 * Perfect layout with properly aligned week days and calendar grid
 * Click any date to view that day's events below
 * Heat map shows event density (background colors)
 */
class LargeCalendarWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact
    
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Check calendar sync setting
        val repository = com.material.you.widgets.data.repository.RepositoryProvider.preferences
        val isSyncEnabled = repository.isCalendarSyncEnabled()
        val showAllDay = repository.showAllDayEvents()
        val maxEventsStr = repository.getMaxEvents()
        val maxEvents = maxEventsStr.toIntOrNull() ?: 5
        
        val hasPermission = CalendarDataProvider.hasCalendarPermission(context)
        val today = java.time.LocalDate.now()
        val yearMonth = java.time.YearMonth.from(today)

        // Pre-fetch event counts for the month and today's events BEFORE composition
        val eventCountsMap = if (hasPermission && isSyncEnabled) {
            try {
                CalendarDataProvider.getEventCountForMonth(context, today.year, today.monthValue)
                    .mapKeys { (day, _) -> yearMonth.atDay(day) }
            } catch (e: Exception) {
                android.util.Log.e("LargeCalendarWidget", "Error pre-fetching event counts", e)
                emptyMap()
            }
        } else emptyMap()

        val todayEvents = if (hasPermission && isSyncEnabled) {
            try {
                CalendarDataProvider.getEventsForDate(
                    context, 
                    today,
                    includeAllDay = showAllDay
                ).take(maxEvents)
            } catch (e: Exception) {
                android.util.Log.e("LargeCalendarWidget", "Error fetching today's events", e)
                emptyList()
            }
        } else emptyList()

        provideContent {
            GlanceTheme {
                LargeCalendarContent(context, id, hasPermission, eventCountsMap, todayEvents)
            }
        }
    }
    
    /**
     * Widget receiver (for manifest registration)
     */
    class Receiver : GlanceAppWidgetReceiver() {
        override val glanceAppWidget = LargeCalendarWidget()
    }
}

@Composable
private fun LargeCalendarContent(
    context: Context,
    glanceId: GlanceId,
    hasPermission: Boolean,
    eventCountsMap: Map<LocalDate, Int>,
    todayEvents: List<CalendarEvent>
) {
    val appearance = WidgetAppearanceHelper.getWidgetAppearance(context)
    val today = LocalDate.now()
    val yearMonth = YearMonth.from(today)

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(appearance.backgroundColor)
            .cornerRadius(appearance.cornerRadius.dp)
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Header with Month/Year - Centered
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${yearMonth.month.getDisplayName(JavaTextStyle.FULL, Locale.getDefault())} ${yearMonth.year}",
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            )
        }
        
        Spacer(modifier = GlanceModifier.height(12.dp))
        
        // Full month calendar grid (no clickable dates)
        CalendarGridLarge(yearMonth, today, eventCountsMap)
        
        Spacer(modifier = GlanceModifier.height(14.dp))
        
        // Divider
        Box(
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(2.dp)
                .background(GlanceTheme.colors.surfaceVariant)
                .cornerRadius(1.dp)
        ) {}
        
        Spacer(modifier = GlanceModifier.height(10.dp))
        
        // Show today's events
        if (!hasPermission) {
            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .background(GlanceTheme.colors.errorContainer)
                    .cornerRadius(12.dp)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Grant calendar permission in Settings to see events",
                    style = TextStyle(
                        color = GlanceTheme.colors.onErrorContainer,
                        fontSize = 12.sp
                    )
                )
            }
        } else if (todayEvents.isEmpty()) {
            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .background(GlanceTheme.colors.surfaceVariant)
                    .cornerRadius(12.dp)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "No events today",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                )
            }
        } else {
            Column {
                todayEvents.take(3).forEach { event ->
                    EventItemLarge(event)
                    Spacer(modifier = GlanceModifier.height(6.dp))
                }
                if (todayEvents.size > 3) {
                    Text(
                        text = "+${todayEvents.size - 3} more events",
                        style = TextStyle(
                            color = GlanceTheme.colors.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarGridLarge(
    yearMonth: YearMonth,
    today: LocalDate,
    eventCountsMap: Map<LocalDate, Int>
) {
    val firstDay = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val startDayOfWeek = firstDay.dayOfWeek.value % 7
    
    // Day names header
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT").forEach { day ->
            Column(
                modifier = GlanceModifier.defaultWeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = day,
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
    
    Spacer(modifier = GlanceModifier.height(6.dp))
    
    // Calendar grid - event counts are pre-fetched before composition
    Column(verticalAlignment = Alignment.Top) {
        val weeksNeeded = CalendarGridUtils.requiredWeekRows(daysInMonth, startDayOfWeek)

        repeat(weeksNeeded) { week ->
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                repeat(7) { dayIndex ->
                    val dayPosition = week * 7 + dayIndex
                    val actualDay = dayPosition - startDayOfWeek + 1

                    if (actualDay in 1..daysInMonth) {
                        val date = yearMonth.atDay(actualDay)
                        val isToday = date == today
                        val eventCount = eventCountsMap[date] ?: 0

                        DayCell(date, actualDay, isToday, eventCount)
                    } else {
                        Column(modifier = GlanceModifier.defaultWeight()) {
                            Spacer(modifier = GlanceModifier.size(36.dp))
                        }
                    }
                }
            }
            Spacer(modifier = GlanceModifier.height(3.dp))
        }
    }
}

@Composable
private fun RowScope.DayCell(date: LocalDate, day: Int, isToday: Boolean, eventCount: Int) {
    Column(
        modifier = GlanceModifier
            .defaultWeight()
            .padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = GlanceModifier
                .size(34.dp),
            contentAlignment = Alignment.Center
        ) {
            // Cookie shape background for today
            if (isToday) {
                val context = androidx.glance.LocalContext.current
                val cookieBitmap = remember(isToday) {
                    renderCookieBackground(context, isToday)
                }

                Image(
                    provider = ImageProvider(cookieBitmap),
                    contentDescription = null,
                    modifier = GlanceModifier.size(34.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = day.toString(),
                    style = TextStyle(
                        color = if (isToday) GlanceTheme.colors.onPrimary
                        else GlanceTheme.colors.onSurface,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                    )
                )

                // Show small dots for events (not on today)
                if (eventCount > 0 && !isToday) {
                    Spacer(modifier = GlanceModifier.height(1.dp))
                    Row(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val dotCount = if (eventCount > 3) 3 else eventCount
                        repeat(dotCount) {
                            Box(
                                modifier = GlanceModifier
                                    .size(3.dp)
                                    .background(GlanceTheme.colors.primary)
                                    .cornerRadius(2.dp)
                            ) {}
                            if (it < dotCount - 1) {
                                Spacer(modifier = GlanceModifier.width(2.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Helper function to render cookie shape background for calendar dates
 */
private fun renderCookieBackground(context: Context, isToday: Boolean): android.graphics.Bitmap {
    val density = context.resources.displayMetrics.density
    val sizePx = (34 * density).toInt()
    
    val color = if (isToday) {
        // Today uses primary color
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            context.getColor(android.R.color.system_accent1_200)
        } else {
            0xFF66BB6A.toInt() // Green fallback
        }
    } else {
        // Selected uses secondary/surface color
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            context.getColor(android.R.color.system_accent2_200)
        } else {
            0xFF90CAF9.toInt() // Blue fallback
        }
    }
    
    return ShapeRenderer.renderToBitmap(
        polygon = ShapeDefinitions.cookie6Sided,
        widthPx = sizePx,
        heightPx = sizePx,
        color = color,
        stretchToFill = true
    )
}

@Composable
private fun EventItemLarge(event: CalendarEvent) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(GlanceTheme.colors.primaryContainer)
            .cornerRadius(10.dp)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.Start
    ) {
        // Color indicator dot
        Box(
            modifier = GlanceModifier
                .size(8.dp)
                .background(GlanceTheme.colors.primary)
                .cornerRadius(4.dp)
        ) {}
        
        Spacer(modifier = GlanceModifier.width(8.dp))
        
        // Event details
        Column(
            modifier = GlanceModifier.defaultWeight()
        ) {
            Text(
                text = event.title,
                style = TextStyle(
                    color = GlanceTheme.colors.onPrimaryContainer,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1
            )
            Spacer(modifier = GlanceModifier.height(2.dp))
            Text(
                text = CalendarDataProvider.formatEventTime(event),
                style = TextStyle(
                    color = GlanceTheme.colors.onPrimaryContainer,
                    fontSize = 11.sp
                )
            )
        }
    }
}
