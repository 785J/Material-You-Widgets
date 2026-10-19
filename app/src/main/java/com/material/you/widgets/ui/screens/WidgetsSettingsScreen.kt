package com.material.you.widgets.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Autorenew
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.material.you.widgets.core.theme.ThemeManager
import com.material.you.widgets.core.theme.collectIconShapeAsState
import com.material.you.widgets.data.repository.RepositoryProvider
import com.material.you.widgets.ui.components.Material3ExpressiveSettingsGroup
import com.material.you.widgets.ui.components.ModernInfoItem
import com.material.you.widgets.ui.components.PreferenceEntry
import com.material.you.widgets.worker.WorkManagerScheduler

private const val KEY_AUTO_UPDATE_WIDGETS = "auto_update_widgets"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetsSettingsScreen(
    onBack: () -> Unit,
    onNavigateToAppearance: () -> Unit,
    onNavigateToWidgetAppearance: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val iconShape by ThemeManager.collectIconShapeAsState()

    // Persisted: gates the periodic WorkManager refresh in WidgetsApplication
    var autoUpdateEnabled by remember {
        mutableStateOf(RepositoryProvider.preferences.getBoolean(KEY_AUTO_UPDATE_WIDGETS, true))
    }

    var showAboutDialog by remember { mutableStateOf(false) }

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
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 28.sp),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 4.dp),
        )

        // Personalization Section
        Text(
            text = "PERSONALIZATION",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        Material3ExpressiveSettingsGroup(
            modifier = Modifier.fillMaxWidth(),
            items = listOf(
                {
                    ModernInfoItem(
                        icon = { Icon(Icons.Rounded.Palette, null, modifier = Modifier.size(22.dp)) },
                        title = "Appearance",
                        subtitle = "Theme, colors, and styles",
                        onClick = onNavigateToAppearance,
                        showArrow = true,
                        iconBackgroundColor = MaterialTheme.colorScheme.primaryContainer,
                        iconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        iconShape = iconShape,
                    )
                },
                {
                    ModernInfoItem(
                        icon = { Icon(Icons.Rounded.Widgets, null, modifier = Modifier.size(22.dp)) },
                        title = "Widget Settings",
                        subtitle = "Opacity, borders, and permissions",
                        onClick = onNavigateToWidgetAppearance,
                        showArrow = true,
                        iconBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                        iconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        iconShape = iconShape,
                    )
                }
            )
        )

        // Widgets Management Section
        Text(
            text = "WIDGETS",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        Material3ExpressiveSettingsGroup(
            modifier = Modifier.fillMaxWidth(),
            items = listOf(
                {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            PreferenceEntry(
                                title = { Text("Auto Update Widgets") },
                                description = "Periodically refresh widget data in the background",
                                icon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Autorenew,
                                        contentDescription = null,
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                iconShape = iconShape,
                                iconBackgroundColor = MaterialTheme.colorScheme.primaryContainer,
                                iconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                        Switch(
                            checked = autoUpdateEnabled,
                            onCheckedChange = { enabled ->
                                autoUpdateEnabled = enabled
                                RepositoryProvider.preferences.setBoolean(KEY_AUTO_UPDATE_WIDGETS, enabled)
                                if (enabled) {
                                    WorkManagerScheduler.schedulePeriodicUpdates(context)
                                } else {
                                    WorkManagerScheduler.cancelAllUpdates(context)
                                }
                            },
                            modifier = Modifier.padding(end = 20.dp)
                        )
                    }
                }
            )
        )

        // About Section
        Text(
            text = "ABOUT",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        Material3ExpressiveSettingsGroup(
            modifier = Modifier.fillMaxWidth(),
            items = listOf(
                {
                    ModernInfoItem(
                        icon = { Icon(Icons.Rounded.Info, null, modifier = Modifier.size(22.dp)) },
                        title = "About",
                        subtitle = "Version and app information",
                        onClick = { showAboutDialog = true },
                        showArrow = true,
                        iconBackgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                        iconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        iconShape = iconShape,
                    )
                }
            )
        )

        Spacer(modifier = Modifier.height(80.dp))
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close")
                }
            },
            title = { Text("Material Widgets") },
            text = {
                Text(
                    "Version ${com.material.you.widgets.BuildConfig.VERSION_NAME}\n\n" +
                        "Material You widgets for your home screen: calendar, " +
                        "battery, away time and year progress."
                )
            }
        )
    }
}
