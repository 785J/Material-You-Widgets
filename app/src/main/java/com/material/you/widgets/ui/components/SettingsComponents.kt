package com.material.you.widgets.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.material.you.widgets.data.models.IconShape
import com.material.you.widgets.core.shapes.toShape

/**
 * Universal Settings Components
 * All reusable components for building consistent settings screens
 */

// ============ Base Components ============

/**
 * ModernInfoItem - A modern list item with icon, title, and subtitle
 * Base component used by all setting items
 */
@Composable
fun ModernInfoItem(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    showArrow: Boolean = false,
    iconBackgroundColor: Color? = null,
    iconContentColor: Color? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    iconShape: IconShape = IconShape.CIRCLE,
    iconSize: Dp = 44.dp,
    modifier: Modifier = Modifier,
) {
    val actualShape = remember(iconShape) { iconShape.toShape() }
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Icon Container
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

        Spacer(modifier = Modifier.width(16.dp))

        // Text Content
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // Trailing Content
        if (trailingContent != null) {
            trailingContent()
        } else {
            if (showArrow) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                )
            }
        }
    }
}

// ============ Specialized Setting Items ============

/**
 * Toggle Setting Item - Switch with icon, title, and subtitle
 */
@Composable
fun ToggleSettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    iconShape: IconShape,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    iconBackgroundColor: Color? = null,
    iconContentColor: Color? = null
) {
    ModernInfoItem(
        icon = { 
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            ) 
        },
        title = title,
        subtitle = subtitle,
        onClick = if (enabled) {{ onCheckedChange(!checked) }} else null,
        iconShape = iconShape,
        showArrow = false,
        iconBackgroundColor = iconBackgroundColor,
        iconContentColor = iconContentColor,
        trailingContent = if (enabled) {{
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }} else null,
        modifier = modifier
    )
}

/**
 * Action Setting Item - Clickable item that triggers an action
 */
@Composable
fun ActionSettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)?,
    iconShape: IconShape,
    modifier: Modifier = Modifier,
    showArrow: Boolean = true,
    iconBackgroundColor: Color? = null,
    iconContentColor: Color? = null
) {
    ModernInfoItem(
        icon = { 
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            ) 
        },
        title = title,
        subtitle = subtitle,
        onClick = onClick,
        iconShape = iconShape,
        showArrow = showArrow,
        iconBackgroundColor = iconBackgroundColor,
        iconContentColor = iconContentColor,
        modifier = modifier
    )
}

/**
 * Selection Setting Item - Opens a dialog or navigation for selection
 */
@Composable
fun SelectionSettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    iconShape: IconShape,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    iconBackgroundColor: Color? = null,
    iconContentColor: Color? = null
) {
    ModernInfoItem(
        icon = { 
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            ) 
        },
        title = title,
        subtitle = subtitle,
        onClick = if (enabled) {{ onClick() }} else null,
        iconShape = iconShape,
        showArrow = enabled,
        iconBackgroundColor = iconBackgroundColor,
        iconContentColor = iconContentColor,
        modifier = modifier
    )
}

// ============ Layout Components ============

/**
 * Groups items together with adaptive corner shapes for top, middle, and bottom items.
 */
@Composable
fun Material3ExpressiveSettingsGroup(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    items: List<@Composable () -> Unit>,
) {
    val cornerRadius = 24.dp
    val connectionRadius = 4.dp

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        items.forEachIndexed { index, item ->
            val shape = when {
                items.size == 1 -> RoundedCornerShape(cornerRadius)
                index == 0 -> RoundedCornerShape(
                    topStart = cornerRadius,
                    topEnd = cornerRadius,
                    bottomStart = connectionRadius,
                    bottomEnd = connectionRadius,
                )
                index == items.size - 1 -> RoundedCornerShape(
                    topStart = connectionRadius,
                    topEnd = connectionRadius,
                    bottomStart = cornerRadius,
                    bottomEnd = cornerRadius,
                )
                else -> RoundedCornerShape(connectionRadius)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(containerColor, shape)
                    .clip(shape),
            ) {
                item()
            }
        }
    }
}

/**
 * Data class for a button option in ConnectedButtonGroup.
 */
data class ButtonOption<T>(
    val value: T,
    val label: String,
)

/**
 * A Material 3 style ButtonGroup using ToggleButton for multi-option selections.
 * Uses official ButtonGroupDefaults for proper connected button shapes.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T> ConnectedButtonGroup(
    options: List<ButtonOption<T>>,
    selectedValue: T,
    onSelectionChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    buttonHeight: Dp = 40.dp,
    maxWidth: Dp = 280.dp,
) {
    val selectedIndex = options.indexOfFirst { it.value == selectedValue }.coerceAtLeast(0)

    Row(
        modifier =
            modifier
                .widthIn(max = maxWidth)
                .height(buttonHeight),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
    ) {
        options.forEachIndexed { index, option ->
            val isSelected = index == selectedIndex

            ToggleButton(
                checked = isSelected,
                onCheckedChange = { onSelectionChange(option.value) },
                modifier =
                    Modifier
                        .weight(1f)
                        .semantics { role = Role.RadioButton },
                shapes =
                    when (index) {
                        0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                        options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                        else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                    },
                colors =
                    ToggleButtonDefaults.toggleButtonColors(
                        checkedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        checkedContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
            ) {
                Text(
                    text = option.label,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}
