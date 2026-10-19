package com.material.you.widgets.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.material.you.widgets.core.theme.ThemeManager
import com.material.you.widgets.core.theme.collectIconShapeAsState
import com.material.you.widgets.ui.components.Material3ExpressiveSettingsGroup
import com.material.you.widgets.ui.components.ModernInfoItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetsListScreen(
    onWidgetClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val iconShape by ThemeManager.collectIconShapeAsState()
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = "YOU",
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 32.sp),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp),
                )
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = "Settings"
                    )
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        item {
            Text(
                text = "AVAILABLE WIDGETS",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        item {
            Material3ExpressiveSettingsGroup(
                modifier = Modifier.fillMaxWidth(),
                items = listOf(
                    {
                        ModernInfoItem(
                            icon = { Icon(Icons.Rounded.Event, null, modifier = Modifier.size(22.dp)) },
                            title = "Calendar Widget",
                            subtitle = "Your schedule at a glance",
                            onClick = { onWidgetClick("calendar") },
                            showArrow = true,
                            iconBackgroundColor = MaterialTheme.colorScheme.primaryContainer,
                            iconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            iconShape = iconShape,
                        )
                    },
                    {
                        ModernInfoItem(
                            icon = { Icon(Icons.Rounded.Battery4Bar, null, modifier = Modifier.size(22.dp)) },
                            title = "Battery Widget",
                            subtitle = "Battery level and charging status",
                            onClick = { onWidgetClick("battery") },
                            showArrow = true,
                            iconBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                            iconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            iconShape = iconShape,
                        )
                    },
                    {
                        ModernInfoItem(
                            icon = { Icon(Icons.Rounded.WatchLater, null, modifier = Modifier.size(22.dp)) },
                            title = "Away Time",
                            subtitle = "Track phone-free moments",
                            onClick = { onWidgetClick("watchtime") },
                            showArrow = true,
                            iconBackgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                            iconContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            iconShape = iconShape,
                        )
                    },
                    {
                        ModernInfoItem(
                            icon = { Icon(Icons.Rounded.CalendarMonth, null, modifier = Modifier.size(22.dp)) },
                            title = "Year Progress",
                            subtitle = "365 dots showing year completion",
                            onClick = { onWidgetClick("yearprogress") },
                            showArrow = true,
                            iconBackgroundColor = MaterialTheme.colorScheme.errorContainer,
                            iconContentColor = MaterialTheme.colorScheme.onErrorContainer,
                            iconShape = iconShape,
                        )
                    }
                )
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        item {
            Text(
                text = "COMING SOON",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        item {
            Material3ExpressiveSettingsGroup(
                modifier = Modifier.fillMaxWidth(),
                items = listOf(
                    {
                        ModernInfoItem(
                            icon = { Icon(Icons.Rounded.MusicNote, null, modifier = Modifier.size(22.dp)) },
                            title = "Music Player Widget",
                            subtitle = "Control your music playback",
                            onClick = { },
                            showArrow = false,
                            iconBackgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                            iconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            iconShape = iconShape,
                        )
                    },
                    {
                        ModernInfoItem(
                            icon = { Icon(Icons.Rounded.Bookmark, null, modifier = Modifier.size(22.dp)) },
                            title = "Quick Notes Widget",
                            subtitle = "Quick access to your notes",
                            onClick = { },
                            showArrow = false,
                            iconBackgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                            iconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            iconShape = iconShape,
                        )
                    },
                    {
                        ModernInfoItem(
                            icon = { Icon(Icons.Rounded.Favorite, null, modifier = Modifier.size(22.dp)) },
                            title = "Health Widget",
                            subtitle = "Track your health metrics",
                            onClick = { },
                            showArrow = false,
                            iconBackgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                            iconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            iconShape = iconShape,
                        )
                    }
                )
            )
        }
    }
}
