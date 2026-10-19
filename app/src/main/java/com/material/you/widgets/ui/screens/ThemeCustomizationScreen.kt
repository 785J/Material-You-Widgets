package com.material.you.widgets.ui.screens

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.material.you.widgets.core.shapes.toShape
import com.material.you.widgets.core.theme.ColorPresets
import com.material.you.widgets.core.theme.ThemeManager
import com.material.you.widgets.core.theme.collectUseDynamicColorAsState
import com.material.you.widgets.core.theme.collectThemeSeedColorAsState
import com.material.you.widgets.core.theme.collectUseMonochromeAsState
import com.material.you.widgets.core.theme.collectThemeModeAsState
import com.material.you.widgets.core.theme.collectFontFamilyAsState
import com.material.you.widgets.core.theme.collectIconShapeAsState
import com.material.you.widgets.data.models.AppFontFamily
import com.material.you.widgets.data.models.ThemeMode
import com.material.you.widgets.ui.components.ButtonOption
import com.material.you.widgets.ui.components.ConnectedButtonGroup
import com.material.you.widgets.ui.components.Material3ExpressiveSettingsGroup
import com.material.you.widgets.ui.components.ModernInfoItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeCustomizationScreen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Collect theme state — changes apply instantly via recomposition
    val useDynamicColor by ThemeManager.collectUseDynamicColorAsState()
    val currentSeedColor by ThemeManager.collectThemeSeedColorAsState()
    val useMonochrome by ThemeManager.collectUseMonochromeAsState()
    val themeMode by ThemeManager.collectThemeModeAsState()
    val iconShape by ThemeManager.collectIconShapeAsState()
    val fontFamily by ThemeManager.collectFontFamilyAsState()

    var showCustomColorPicker by remember { mutableStateOf(false) }
    
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
            text = "Theme",
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 28.sp),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 4.dp)
        )
        
        Text(
            text = "Choose your color palette and theme style",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Current Theme Preview
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Current Theme",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ColorSwatch(
                        color = MaterialTheme.colorScheme.primary,
                        label = "Primary",
                        modifier = Modifier.weight(1f)
                    )
                    ColorSwatch(
                        color = MaterialTheme.colorScheme.secondary,
                        label = "Secondary",
                        modifier = Modifier.weight(1f)
                    )
                    ColorSwatch(
                        color = MaterialTheme.colorScheme.tertiary,
                        label = "Tertiary",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Theme Mode Section
        Text(
            text = "THEME MODE",
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
                // Dynamic Colors (Material You)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    ThemeModeItem(
                        icon = Icons.Default.AutoAwesome,
                        title = "Dynamic Colors",
                        description = "Colors from your wallpaper (Material You)",
                        isSelected = useDynamicColor,
                        onClick = {
                            ThemeManager.enableDynamicColors()
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }

                // Custom Color
                ThemeModeItem(
                    icon = Icons.Default.Palette,
                    title = "Custom Color",
                    description = "Choose from preset colors",
                    isSelected = !useDynamicColor && !useMonochrome,
                    onClick = {
                        ThemeManager.setUseDynamicColor(false)
                        ThemeManager.setUseMonochrome(false)
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                // Monochrome
                ThemeModeItem(
                    icon = Icons.Default.Contrast,
                    title = "Monochrome",
                    description = "Black and white theme",
                    isSelected = useMonochrome,
                    onClick = {
                        ThemeManager.enableMonochrome()
                    }
                )
            }
        }
        
        // Color Presets Section (only show if custom color mode is active)
        if (!useDynamicColor && !useMonochrome) {
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "COLOR PRESETS",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(ColorPresets.presets) { preset ->
                    ColorPresetItem(
                        preset = preset,
                        isSelected = currentSeedColor == preset.color,
                        onClick = {
                            ThemeManager.applyPresetColor(preset.color, preset.shape)
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // THEME Section (Dark/Light/System)
        Text(
            text = "APPEARANCE",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        
        Material3ExpressiveSettingsGroup(
            modifier = Modifier.fillMaxWidth(),
            items = listOf {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            ModernInfoItem(
                                icon = { Icon(Icons.Rounded.DarkMode, null, modifier = Modifier.size(22.dp)) },
                                title = "Dark Theme",
                                subtitle = "Select theme preference",
                                iconShape = iconShape,
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val themeOptions = listOf(
                        ButtonOption(ThemeMode.SYSTEM, "System"),
                        ButtonOption(ThemeMode.LIGHT, "Light"),
                        ButtonOption(ThemeMode.DARK, "Dark"),
                    )
                    ConnectedButtonGroup(
                        options = themeOptions,
                        selectedValue = themeMode,
                        onSelectionChange = {
                            ThemeManager.setThemeMode(it)
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 8.dp),
                    )
                }
            }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // TYPOGRAPHY Section
        Text(
            text = "TYPOGRAPHY",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        
        Material3ExpressiveSettingsGroup(
            modifier = Modifier.fillMaxWidth(),
            items = listOf {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            ModernInfoItem(
                                icon = { Icon(Icons.Rounded.TextFields, null, modifier = Modifier.size(22.dp)) },
                                title = "Font Family",
                                subtitle = fontFamily.displayName(),
                                iconShape = iconShape,
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val fontOptions = listOf(
                        ButtonOption(AppFontFamily.DEFAULT, "Default"),
                        ButtonOption(AppFontFamily.BITCOUNT, "Bitcount"),
                        ButtonOption(AppFontFamily.QUICKSAND, "Quicksand"),
                        ButtonOption(AppFontFamily.BALOO_BHAI, "Baloo"),
                    )
                    ConnectedButtonGroup(
                        options = fontOptions,
                        selectedValue = fontFamily,
                        onSelectionChange = {
                            ThemeManager.setFontFamily(it)
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 8.dp),
                    )
                }
            }
        )
        
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
                Column {
                    Text(
                        text = "Material 3 Dynamic Color",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Theme colors are generated using Material 3's HCT color system, creating harmonious palettes with proper contrast and accessibility.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun ColorSwatch(
    color: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ThemeModeItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            Color.Transparent
        },
        label = "backgroundColor"
    )
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
        
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ColorPresetItem(
    preset: ColorPresets.ColorPreset,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Get the actual shape for this preset
    val presetShape = remember(preset.shape) { preset.shape.toShape() }
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(presetShape) // Use the preset's actual shape!
                .clickable(onClick = onClick)
                .background(preset.color)
                .then(
                    if (isSelected) {
                        Modifier.border(
                            BorderStroke(3.dp, MaterialTheme.colorScheme.primary),
                            presetShape
                        )
                    } else {
                        Modifier.border(
                            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            presetShape
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        
        Text(
            text = preset.name,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}
