package com.material.you.widgets.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.material.you.widgets.core.theme.ThemeManager
import com.material.you.widgets.core.theme.collectIconShapeAsState
import com.material.you.widgets.data.CalendarDataProvider
import com.material.you.widgets.data.repository.RepositoryProvider
import com.material.you.widgets.glance.common.WidgetRefreshManager
import com.material.you.widgets.glance.wellbeing.WatchTimeWidget
import com.material.you.widgets.ui.components.ModernInfoItem
import com.material.you.widgets.ui.components.PreferenceEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetSettingsScreen(
    onNavigateToCalendarDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = RepositoryProvider.preferences
    val scrollState = rememberScrollState()
    val iconShape by ThemeManager.collectIconShapeAsState()

    // Widget appearance settings (persisted via PreferencesRepository)
    var useDynamicColors by remember {
        mutableStateOf(prefs.useWidgetDynamicColors())
    }

    var pureBlack by remember {
        mutableStateOf(prefs.useWidgetPureBlack())
    }

    var widgetOpacity by remember {
        mutableFloatStateOf(prefs.getWidgetOpacity())
    }

    var widgetCornerRadius by remember {
        mutableIntStateOf(prefs.getWidgetCornerRadius())
    }
    
    // Permission states
    var hasCalendarPermission by remember {
        mutableStateOf(CalendarDataProvider.hasCalendarPermission(context))
    }

    var hasUsageAccess by remember {
        mutableStateOf(WatchTimeWidget.hasUsageAccess(context))
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    fun refreshPermissionStates() {
        hasCalendarPermission = CalendarDataProvider.hasCalendarPermission(context)
        hasUsageAccess = WatchTimeWidget.hasUsageAccess(context)
    }

    LaunchedEffect(lifecycleOwner) {
        refreshPermissionStates()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshPermissionStates()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    
    var showOpacityDialog by remember { mutableStateOf(false) }
    var showCornerRadiusDialog by remember { mutableStateOf(false) }
    
    // Permission launchers
    val calendarPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCalendarPermission = isGranted && CalendarDataProvider.hasCalendarPermission(context)
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Widget Settings",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Text(
            text = "Customize appearance and permissions for your widgets",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // APPEARANCE Section
        Text(
            text = "APPEARANCE",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column {
                PreferenceEntry(
                    title = { Text("Dynamic Colors") },
                    description = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        "Use Material You colors from your wallpaper"
                    } else {
                        "Requires Android 12 or higher"
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null
                        )
                    },
                    iconShape = iconShape,
                    isEnabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            useDynamicColors = !useDynamicColors
                            prefs.setWidgetDynamicColors(useDynamicColors)
                            WidgetRefreshManager.refreshAllWidgets(context)
                        }
                    },
                    trailingContent = {
                        Switch(
                            checked = useDynamicColors,
                            enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
                            onCheckedChange = {
                                useDynamicColors = it
                                prefs.setWidgetDynamicColors(it)
                                WidgetRefreshManager.refreshAllWidgets(context)
                            }
                        )
                    }
                )
                
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                
                PreferenceEntry(
                    title = { Text("Pure Black Background") },
                    description = "Use pure black (#000000) for OLED displays",
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Brightness2,
                            contentDescription = null
                        )
                    },
                    iconShape = iconShape,
                    onClick = {
                        pureBlack = !pureBlack
                        prefs.setWidgetPureBlack(pureBlack)
                        WidgetRefreshManager.refreshAllWidgets(context)
                    },
                    trailingContent = {
                        Switch(
                            checked = pureBlack,
                            onCheckedChange = {
                                pureBlack = it
                                prefs.setWidgetPureBlack(it)
                                WidgetRefreshManager.refreshAllWidgets(context)
                            }
                        )
                    }
                )
                
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                
                PreferenceEntry(
                    title = { Text("Widget Opacity") },
                    description = "Current: ${(widgetOpacity * 100).toInt()}% - Tap to adjust transparency",
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Opacity,
                            contentDescription = null
                        )
                    },
                    iconShape = iconShape,
                    onClick = { showOpacityDialog = true }
                )
                
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                
                PreferenceEntry(
                    title = { Text("Corner Radius") },
                    description = "Current: ${widgetCornerRadius}dp rounded corners",
                    icon = {
                        Icon(
                            imageVector = Icons.Default.RoundedCorner,
                            contentDescription = null
                        )
                    },
                    iconShape = iconShape,
                    onClick = { showCornerRadiusDialog = true }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // PERMISSIONS Section
        Text(
            text = "PERMISSIONS",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column {
                PreferenceEntry(
                    title = { Text("Calendar Access") },
                    description = if (hasCalendarPermission) "Granted - Showing events" else "Required to display events",
                    icon = {
                        Icon(
                            imageVector = if (hasCalendarPermission) Icons.Default.Check else Icons.Default.Event,
                            contentDescription = null,
                            tint = if (hasCalendarPermission) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.error
                        )
                    },
                    iconShape = iconShape,
                    iconBackgroundColor = if (hasCalendarPermission)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.errorContainer,
                    onClick = {
                        if (hasCalendarPermission) {
                            // Open app settings to manage permission
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                            context.startActivity(intent)
                        } else {
                            calendarPermissionLauncher.launch(Manifest.permission.READ_CALENDAR)
                        }
                    },
                    trailingContent = {
                        TextButton(
                            onClick = {
                                if (hasCalendarPermission) {
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = Uri.fromParts("package", context.packageName, null)
                                    }
                                    context.startActivity(intent)
                                } else {
                                    calendarPermissionLauncher.launch(Manifest.permission.READ_CALENDAR)
                                }
                            }
                        ) {
                            Text(if (hasCalendarPermission) "Settings" else "Grant")
                        }
                    }
                )
                
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                PreferenceEntry(
                    title = { Text("Usage Access (Wellbeing)") },
                    description = if (hasUsageAccess) {
                        "Granted - Wellbeing widgets can read screen activity"
                    } else {
                        "Required for screen-time and away-time widgets"
                    },
                    icon = {
                        Icon(
                            imageVector = if (hasUsageAccess) Icons.Default.Check else Icons.Default.QueryStats,
                            contentDescription = null,
                            tint = if (hasUsageAccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        )
                    },
                    iconShape = iconShape,
                    iconBackgroundColor = if (hasUsageAccess)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.errorContainer,
                    onClick = {
                        context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                    },
                    trailingContent = {
                        TextButton(
                            onClick = {
                                context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                            }
                        ) {
                            Text(if (hasUsageAccess) "Settings" else "Grant")
                        }
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // DATA SETTINGS Section
        Text(
            text = "DATA SETTINGS",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column {
                PreferenceEntry(
                    title = { Text("Calendar Settings") },
                    description = "Configure calendar sync and events display",
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null
                        )
                    },
                    iconShape = iconShape,
                    onClick = onNavigateToCalendarDetail,
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )

            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Widget changes may take a few moments to appear on your home screen. Try removing and re-adding the widget if changes don't apply.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
        
        Spacer(modifier = Modifier.height(80.dp))
    }
    
    // Opacity Dialog
    if (showOpacityDialog) {
        OpacityDialog(
            currentOpacity = widgetOpacity,
            onDismiss = { showOpacityDialog = false },
            onConfirm = { newOpacity ->
                widgetOpacity = newOpacity
                prefs.setWidgetOpacity(newOpacity)
                showOpacityDialog = false
            }
        )
    }
    
    // Corner Radius Dialog
    if (showCornerRadiusDialog) {
        CornerRadiusDialog(
            currentRadius = widgetCornerRadius,
            onDismiss = { showCornerRadiusDialog = false },
            onConfirm = { newRadius ->
                widgetCornerRadius = newRadius
                prefs.setWidgetCornerRadius(newRadius)
                showCornerRadiusDialog = false
            }
        )
    }

}

@Composable
private fun OpacityDialog(
    currentOpacity: Float,
    onDismiss: () -> Unit,
    onConfirm: (Float) -> Unit
) {
    val context = LocalContext.current
    var opacity by remember { mutableFloatStateOf(currentOpacity) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Widget Opacity") },
        text = {
            Column {
                Text("Adjust widget background transparency")
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "${(opacity * 100).toInt()}%",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Slider(
                    value = opacity,
                    onValueChange = { opacity = it },
                    valueRange = 0.0f..1.0f,
                    steps = 9
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Transparent",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Opaque",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { 
                onConfirm(opacity)
                WidgetRefreshManager.refreshAllWidgets(context)
            }) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun CornerRadiusDialog(
    currentRadius: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    val context = LocalContext.current
    var radius by remember { mutableIntStateOf(currentRadius) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Corner Radius") },
        text = {
            Column {
                Text("Adjust widget corner roundness")
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "${radius}dp",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Slider(
                    value = radius.toFloat(),
                    onValueChange = { radius = it.toInt() },
                    valueRange = 0f..40f,
                    steps = 7
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Square",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Rounded",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { 
                onConfirm(radius)
                WidgetRefreshManager.refreshAllWidgets(context)
            }) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
