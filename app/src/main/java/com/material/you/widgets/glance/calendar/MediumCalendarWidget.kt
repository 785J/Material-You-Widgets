package com.material.you.widgets.glance.calendar

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.material.you.widgets.glance.common.WidgetAppearanceHelper
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle as JavaTextStyle
import java.util.*

/**
 * Medium Calendar Widget (5x2) for Home Screen
 * Wide thin layout - full month calendar grid (5 weeks)
 * Clean design without events - just calendar visualization
 */
class MediumCalendarWidget : GlanceAppWidget() {
    
    override val sizeMode = SizeMode.Exact
    
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Check calendar sync setting
        val repository = com.material.you.widgets.data.repository.RepositoryProvider.preferences
        val isSyncEnabled = repository.isCalendarSyncEnabled()
        val showAllDay = repository.showAllDayEvents()
        val maxEventsStr = repository.getMaxEvents()
        val maxEvents = maxEventsStr.toIntOrNull() ?: 5
        
        val todayEvents = if (isSyncEnabled) {
            try {
                com.material.you.widgets.data.CalendarDataProvider.getTodayEvents(
                    context, 
                    includeAllDay = showAllDay
                ).take(maxEvents)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
        
        provideContent {
            GlanceTheme {
                MediumCalendarContent(context, todayEvents)
            }
        }
    }
    
    /**
     * Widget receiver (for manifest registration)
     */
    class Receiver : GlanceAppWidgetReceiver() {
        override val glanceAppWidget = MediumCalendarWidget()
    }
}

@Composable
private fun MediumCalendarContent(
    context: Context,
    todayEvents: List<com.material.you.widgets.data.CalendarEvent>
) {
    val appearance = WidgetAppearanceHelper.getWidgetAppearance(context)
    val today = LocalDate.now()
    val yearMonth = YearMonth.from(today)
    val eventCount = todayEvents.size
    
    // Fantastical-style month view with heat map
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(appearance.backgroundColor)
            .cornerRadius(appearance.cornerRadius.dp)
            .padding(14.dp)
    ) {
        Row(
            modifier = GlanceModifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalAlignment = Alignment.Top
        ) {
            // Left: Large date card (like Fantastical) - centered alignment
            Column(
                modifier = GlanceModifier.width(85.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.Top
            ) {
                // Accent month banner (centered)
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .background(appearance.colorScheme.primaryContainer)
                        .cornerRadius(10.dp)
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = today.month
                            .getDisplayName(JavaTextStyle.SHORT, Locale.getDefault())
                            .uppercase(),
                        style = TextStyle(
                            color = appearance.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
                
                Spacer(modifier = GlanceModifier.height(8.dp))
                
                // Big day number (centered)
                Text(
                    text = today.dayOfMonth.toString(),
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 48.sp
                    )
                )
                
                Spacer(modifier = GlanceModifier.height(2.dp))
                
                // Day name (centered)
                Text(
                    text = today.dayOfWeek
                        .getDisplayName(JavaTextStyle.SHORT, Locale.getDefault()),
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                )
                
                Spacer(modifier = GlanceModifier.height(8.dp))
                
                // Event count line
                Text(
                    text = "$eventCount Event${if (eventCount != 1) "s" else ""}",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
            
            Spacer(modifier = GlanceModifier.width(14.dp))
            
            // Right: Month calendar grid with heat map
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Month/Year header (centered)
                Text(
                    text = "${yearMonth.month.getDisplayName(JavaTextStyle.SHORT, Locale.getDefault())} ${yearMonth.year}",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                )
                
                Spacer(modifier = GlanceModifier.height(6.dp))
                
                // Day names
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                        Column(
                            modifier = GlanceModifier.defaultWeight(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = day,
                                style = TextStyle(
                                    color = GlanceTheme.colors.onSurfaceVariant,
                                    fontSize = 9.sp,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }
                }
                
                Spacer(modifier = GlanceModifier.height(4.dp))
                
                // Calendar grid with heat map dots
                CalendarGridHeatMap(yearMonth, today)
            }
        }
    }
}

@Composable
private fun CalendarGridHeatMap(yearMonth: YearMonth, today: LocalDate) {
    val firstDay = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val startDayOfWeek = firstDay.dayOfWeek.value % 7
    
    // Show enough weeks for full month coverage (some months need 6 rows)
    val weeksNeeded = CalendarGridUtils.requiredWeekRows(daysInMonth, startDayOfWeek)

    Column(verticalAlignment = Alignment.Top) {
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
                        
                        // Clean calendar cell - no event indicators
                        Column(
                            modifier = GlanceModifier.defaultWeight(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = GlanceModifier
                                    .size(20.dp)
                                    .background(
                                        if (isToday) 
                                            GlanceTheme.colors.primary
                                        else 
                                            androidx.glance.unit.ColorProvider(Color.Transparent)
                                    )
                                    .cornerRadius(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = actualDay.toString(),
                                    style = TextStyle(
                                        color = if (isToday)
                                            GlanceTheme.colors.onPrimary
                                        else
                                            GlanceTheme.colors.onSurface,
                                        fontSize = 10.sp,
                                        textAlign = TextAlign.Center,
                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    } else {
                        Column(modifier = GlanceModifier.defaultWeight()) {
                            Spacer(modifier = GlanceModifier.size(20.dp))
                        }
                    }
                }
            }
            if (week < 4) {
                Spacer(modifier = GlanceModifier.height(2.dp))
            }
        }
    }}
