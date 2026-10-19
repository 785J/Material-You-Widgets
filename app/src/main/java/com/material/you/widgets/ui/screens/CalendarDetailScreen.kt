package com.material.you.widgets.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.material.you.widgets.data.repository.RepositoryProvider
import com.material.you.widgets.ui.components.Material3ExpressiveSettingsGroup
import com.material.you.widgets.ui.components.dialogs.CompactDialog
import com.material.you.widgets.ui.components.ActionSettingItem
import com.material.you.widgets.ui.components.SelectionSettingItem
import com.material.you.widgets.ui.components.ToggleSettingItem
import com.material.you.widgets.ui.screens.base.BaseDetailScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CalendarDetailScreen() {
    val context = LocalContext.current
    val repository = RepositoryProvider.preferences
    val coroutineScope = rememberCoroutineScope()
    
    // State management using repository
    var calendarSync by remember { mutableStateOf(repository.isCalendarSyncEnabled()) }
    var refreshInterval by remember { mutableStateOf(repository.getCalendarRefreshInterval()) }
    var showAllDay by remember { mutableStateOf(repository.showAllDayEvents()) }
    var maxEvents by remember { mutableStateOf(repository.getMaxEvents()) }
    
    var showRefreshDialog by remember { mutableStateOf(false) }
    var showMaxEventsDialog by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    
    BaseDetailScreen(
        title = "Calendar",
        subtitle = "Manage calendar events display"
    ) { iconShape ->
        
        // Sync Settings
        item {
            Material3ExpressiveSettingsGroup(
                items = listOf(
                    {
                        ToggleSettingItem(
                            icon = Icons.Outlined.Sync,
                            title = "Calendar Sync",
                            subtitle = if (calendarSync) "Enabled" else "Disabled",
                            checked = calendarSync,
                            onCheckedChange = {
                                calendarSync = it
                                repository.setCalendarSyncEnabled(it)
                                // Update widgets to reflect sync state
                                com.material.you.widgets.glance.common.WidgetRefreshManager.refreshWidgets(
                                    context,
                                    com.material.you.widgets.glance.calendar.MediumCalendarWidget(),
                                    com.material.you.widgets.glance.calendar.LargeCalendarWidget()
                                )
                            },
                            iconShape = iconShape
                        )
                    },
                    {
                        ActionSettingItem(
                            icon = Icons.Outlined.Refresh,
                            title = "Resync Events",
                            subtitle = if (isRefreshing) "Refreshing..." else "Tap to refresh calendar events",
                            onClick = if (calendarSync && !isRefreshing) {{
                                isRefreshing = true
                                coroutineScope.launch {
                                    try {
                                        // Clear cache and force re-fetch from content provider
                                        com.material.you.widgets.data.CalendarDataProvider.clearCache()
                                        com.material.you.widgets.data.CalendarDataProvider.prefetchCalendarData(context)
                                        
                                        // Update all placed calendar widgets
                                        com.material.you.widgets.glance.common.WidgetRefreshManager.refreshWidgets(
                                            context,
                                            com.material.you.widgets.glance.calendar.MediumCalendarWidget(),
                                            com.material.you.widgets.glance.calendar.LargeCalendarWidget()
                                        )
                                        
                                        android.util.Log.d("CalendarRefresh", "Calendar cache cleared and widgets updated")
                                        delay(500)
                                    } catch (e: Exception) {
                                        android.util.Log.e("CalendarRefresh", "Error refreshing calendar", e)
                                    } finally {
                                        isRefreshing = false
                                    }
                                }
                            }} else null,
                            iconShape = iconShape,
                            showArrow = false,
                            iconBackgroundColor = if (calendarSync) 
                                MaterialTheme.colorScheme.primaryContainer 
                            else 
                                MaterialTheme.colorScheme.surfaceVariant,
                            iconContentColor = if (calendarSync) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                    },
                    {
                        SelectionSettingItem(
                            icon = Icons.Outlined.Schedule,
                            title = "Refresh Interval",
                            subtitle = "$refreshInterval minutes",
                            onClick = { showRefreshDialog = true },
                            iconShape = iconShape,
                            enabled = calendarSync,
                            iconBackgroundColor = if (calendarSync) null else MaterialTheme.colorScheme.surfaceVariant,
                            iconContentColor = if (calendarSync) null else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                    },
                )
            )
        }
        
        // Display Settings
        item {
            Material3ExpressiveSettingsGroup(
                items = listOf(
                    {
                        ToggleSettingItem(
                            icon = Icons.Outlined.EventAvailable,
                            title = "All-Day Events",
                            subtitle = if (showAllDay) "Shown" else "Hidden",
                            checked = showAllDay,
                            onCheckedChange = {
                                showAllDay = it
                                repository.setShowAllDayEvents(it)
                                // Update widgets to reflect all-day visibility
                                com.material.you.widgets.glance.common.WidgetRefreshManager.refreshWidgets(
                                    context,
                                    com.material.you.widgets.glance.calendar.MediumCalendarWidget(),
                                    com.material.you.widgets.glance.calendar.LargeCalendarWidget()
                                )
                            },
                            iconShape = iconShape,
                            enabled = calendarSync,
                            iconBackgroundColor = if (calendarSync) null else MaterialTheme.colorScheme.surfaceVariant,
                            iconContentColor = if (calendarSync) null else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                    },
                    {
                        SelectionSettingItem(
                            icon = Icons.Outlined.FormatListNumbered,
                            title = "Max Events",
                            subtitle = "$maxEvents events shown",
                            onClick = { showMaxEventsDialog = true },
                            iconShape = iconShape,
                            enabled = calendarSync,
                            iconBackgroundColor = if (calendarSync) null else MaterialTheme.colorScheme.surfaceVariant,
                            iconContentColor = if (calendarSync) null else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                    }
                )
            )
        }
    }
    
    // Dialogs
    if (showRefreshDialog) {
        CompactDialog(
            title = "Refresh Interval",
            options = listOf("15" to "15 min", "30" to "30 min", "60" to "1 hour", "120" to "2 hours"),
            selectedValue = refreshInterval,
            onDismiss = { showRefreshDialog = false },
            onSelect = { value ->
                refreshInterval = value
                repository.setCalendarRefreshInterval(value)
                showRefreshDialog = false
                
                // Reschedule WorkManager with new interval
                coroutineScope.launch {
                    com.material.you.widgets.worker.WorkManagerScheduler.schedulePeriodicUpdates(context)
                }
            }
        )
    }
    
    if (showMaxEventsDialog) {
        CompactDialog(
            title = "Max Events",
            options = listOf("3" to "3 events", "5" to "5 events", "10" to "10 events", "15" to "15 events"),
            selectedValue = maxEvents,
            onDismiss = { showMaxEventsDialog = false },
            onSelect = { value ->
                maxEvents = value
                repository.setMaxEvents(value)
                showMaxEventsDialog = false
                
                // Update widgets to reflect new max events
                com.material.you.widgets.glance.common.WidgetRefreshManager.refreshWidgets(
                    context,
                    com.material.you.widgets.glance.calendar.MediumCalendarWidget(),
                    com.material.you.widgets.glance.calendar.LargeCalendarWidget()
                )
            }
        )
    }
}
