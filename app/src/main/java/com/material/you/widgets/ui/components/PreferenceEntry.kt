package com.material.you.widgets.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.material.you.widgets.data.models.IconShape
import com.material.you.widgets.core.shapes.toShape

@Composable
fun PreferenceEntry(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    description: String? = null,
    content: (@Composable () -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    isEnabled: Boolean = true,
    iconShape: IconShape = IconShape.GHOSTISH,
    iconSize: Dp = 44.dp,
    iconBackgroundColor: Color? = null,
    iconContentColor: Color? = null,
) {
    // Use centralized shape system
    val actualShape = remember(iconShape) { iconShape.toShape() }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(
                    enabled = isEnabled && onClick != null,
                    onClick = onClick ?: {},
                ).alpha(if (isEnabled) 1f else 0.5f)
                .padding(horizontal = 20.dp, vertical = 14.dp),
    ) {
        if (icon != null) {
            // Icon Container with background
            Box(
                modifier = Modifier
                    .size(iconSize)
                    .clip(actualShape)
                    .background(
                        color = iconBackgroundColor ?: MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                        shape = actualShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                CompositionLocalProvider(LocalContentColor provides (iconContentColor ?: MaterialTheme.colorScheme.primary)) {
                    icon()
                }
            }

            Spacer(Modifier.width(16.dp))
        }

        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f),
        ) {
            ProvideTextStyle(MaterialTheme.typography.bodyLarge) {
                title()
            }

            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            content?.invoke()
        }

        if (trailingContent != null) {
            Spacer(Modifier.width(12.dp))

            trailingContent()
        }
    }
}
