package com.material.you.widgets.ui.screens.base

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.material.you.widgets.data.models.IconShape
import com.material.you.widgets.data.repository.RepositoryProvider

/**
 * Universal Base Detail Screen
 * Provides common structure for widget detail/settings screens
 * 
 * Features:
 * - Consistent LazyColumn layout
 * - Standard header (title + subtitle)
 * - Automatic iconShape retrieval
 * - Standardized padding and spacing
 * 
 * Usage:
 * ```kotlin
 * @Composable
 * fun MyDetailScreen() {
 *     BaseDetailScreen(
 *         title = "My Widget",
 *         subtitle = "Configure widget settings"
 *     ) {
 *         item {
 *             // Your settings groups here
 *         }
 *     }
 * }
 * ```
 */
@Composable
fun BaseDetailScreen(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    iconShape: IconShape = RepositoryProvider.preferences.getIconShape(),
    content: LazyListScope.(iconShape: IconShape) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Standard Header
        item {
            DetailScreenHeader(
                title = title,
                subtitle = subtitle
            )
        }
        
        // Custom content with iconShape passed in
        content(iconShape)
    }
}

/**
 * Standard header for detail screens
 */
@Composable
private fun DetailScreenHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Helper for creating settings sections with consistent spacing
 */
@Composable
fun SettingsSection(
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content
    )
}
