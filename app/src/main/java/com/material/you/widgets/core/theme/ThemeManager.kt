package com.material.you.widgets.core.theme

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.material.you.widgets.data.models.AppFontFamily
import com.material.you.widgets.data.models.IconShape
import com.material.you.widgets.data.models.ThemeMode
import com.material.you.widgets.data.repository.PreferencesRepository
import com.material.you.widgets.data.repository.RepositoryProvider
import com.material.you.widgets.glance.common.WidgetUpdateCoordinator
import com.material.you.widgets.glance.common.WidgetRefreshManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Central manager for theme configuration and dynamic color palettes.
 *
 * Reactive layer over [PreferencesRepository]: every setting is exposed as a
 * [StateFlow] that screens collect, and every mutation is persisted through
 * the repository so there is exactly one preference store in the app.
 * Color-affecting changes automatically queue a widget refresh via the
 * coordinator (rapid successive writes are coalesced into one update pass).
 */
object ThemeManager {

    // Legacy file used before persistence moved to PreferencesRepository.
    private const val LEGACY_PREFS_NAME = "theme_preferences"

    // State flows for reactive theme updates
    private val _useDynamicColor = MutableStateFlow(true)
    val useDynamicColor: StateFlow<Boolean> = _useDynamicColor.asStateFlow()

    private val _themeSeedColor = MutableStateFlow(ThemeColors.Presets.defaultSeed)
    val themeSeedColor: StateFlow<Color> = _themeSeedColor.asStateFlow()

    private val _useMonochrome = MutableStateFlow(false)
    val useMonochrome: StateFlow<Boolean> = _useMonochrome.asStateFlow()

    private val _pureBlack = MutableStateFlow(false)
    val pureBlack: StateFlow<Boolean> = _pureBlack.asStateFlow()

    private val _expressiveShapes = MutableStateFlow(false)
    val expressiveShapes: StateFlow<Boolean> = _expressiveShapes.asStateFlow()

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _fontFamily = MutableStateFlow(AppFontFamily.BITCOUNT)
    val fontFamily: StateFlow<AppFontFamily> = _fontFamily.asStateFlow()

    private val _iconShape = MutableStateFlow(IconShape.GHOSTISH)
    val iconShape: StateFlow<IconShape> = _iconShape.asStateFlow()

    private var repository: PreferencesRepository? = null
    private var appContext: Context? = null

    /**
     * Initialize ThemeManager with app context
     * Should be called in Application.onCreate()
     */
    fun initialize(context: Context) {
        appContext = context.applicationContext
        if (!RepositoryProvider.isInitialized()) {
            RepositoryProvider.init(context)
        }
        repository = RepositoryProvider.preferences
        migrateLegacyPrefs(context)
        loadPreferences()
    }

    /**
     * One-time migration: import values from the old dedicated
     * "theme_preferences" file into the shared store, then remove the file.
     * Skipped entirely when the legacy file holds no values.
     */
    private fun migrateLegacyPrefs(context: Context) {
        val legacy = context.getSharedPreferences(LEGACY_PREFS_NAME, Context.MODE_PRIVATE)
        val hasContent = legacy.all.isNotEmpty()
        if (!hasContent) return

        val repo = repository ?: return
        val legacyKeys = mapOf(
            "use_dynamic_color" to { v: Any -> repo.setUseDynamicColor(v as Boolean) },
            "theme_seed_color" to { v: Any -> repo.setSeedColor(v as Int) },
            "use_monochrome" to { v: Any -> repo.setMonochrome(v as Boolean) },
            "pure_black" to { v: Any -> repo.setPureBlack(v as Boolean) },
            "expressive_shapes" to { v: Any -> repo.setExpressive(v as Boolean) }
        )
        for ((key, apply) in legacyKeys) {
            val value = legacy.all[key] ?: continue
            // Only import when the canonical store has no value yet.
            if (!repo.contains(key)) apply(value)
        }
        legacy.edit().clear().apply()
    }

    /**
     * Load all theme preferences from the repository into the state flows.
     */
    private fun loadPreferences() {
        val repo = repository ?: return
        _useDynamicColor.value = repo.useDynamicColor()
        _themeSeedColor.value = Color(repo.getSeedColor())
        _useMonochrome.value = repo.useMonochrome()
        _pureBlack.value = repo.usePureBlack()
        _expressiveShapes.value = repo.useExpressive()
        _themeMode.value = repo.getThemeMode()
        _fontFamily.value = repo.getFontFamily()
        _iconShape.value = repo.getIconShape()
    }

    /**
     * Update dynamic color preference
     */
    fun setUseDynamicColor(enabled: Boolean) {
        _useDynamicColor.value = enabled
        repository?.setUseDynamicColor(enabled)
        refreshWidgets()
    }

    /**
     * Update theme seed color
     * @param color Seed color for Material 3 palette generation
     */
    fun setThemeSeedColor(color: Color) {
        _themeSeedColor.value = color
        repository?.setSeedColor(color.toArgb())
        refreshWidgets()
    }

    /**
     * Update monochrome theme preference
     */
    fun setUseMonochrome(enabled: Boolean) {
        _useMonochrome.value = enabled
        repository?.setMonochrome(enabled)
        refreshWidgets()
    }

    /**
     * Update pure black mode for OLED displays
     */
    fun setPureBlack(enabled: Boolean) {
        _pureBlack.value = enabled
        repository?.setPureBlack(enabled)
        refreshWidgets()
    }

    /**
     * Update expressive shapes preference
     */
    fun setExpressiveShapes(enabled: Boolean) {
        _expressiveShapes.value = enabled
        repository?.setExpressive(enabled)
    }

    /**
     * Update theme mode (light/dark/system). App-only: widgets follow the
     * system setting themselves, so no widget refresh is needed.
     */
    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        repository?.setThemeMode(mode)
    }

    /**
     * Update the app font family preference.
     */
    fun setFontFamily(family: AppFontFamily) {
        _fontFamily.value = family
        repository?.setFontFamily(family)
    }

    /**
     * Update the icon shape used to decorate settings icons.
     */
    fun setIconShape(shape: IconShape) {
        _iconShape.value = shape
        repository?.setIconShape(shape)
    }

    /**
     * Refresh all widgets after a color-affecting theme change — via the
     * coordinator so that rapid successive writes are coalesced into one
     * update pass.
     */
    private fun refreshWidgets() {
        appContext?.let { context ->
            WidgetUpdateCoordinator.request(
                context,
                *WidgetRefreshManager.allWidgetInstances().toTypedArray()
            )
        }
    }

    /**
     * Reset theme to default settings
     */
    fun resetToDefaults() {
        setUseDynamicColor(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        setThemeSeedColor(ThemeColors.Presets.defaultSeed)
        setUseMonochrome(false)
        setPureBlack(false)
        setExpressiveShapes(false)
        setThemeMode(ThemeMode.SYSTEM)
        setFontFamily(AppFontFamily.BITCOUNT)
        setIconShape(IconShape.GHOSTISH)
    }

    /**
     * Get current theme configuration snapshot
     */
    fun getThemeConfig(): ThemeConfig {
        return ThemeConfig(
            useDynamicColor = _useDynamicColor.value,
            seedColor = _themeSeedColor.value,
            useMonochrome = _useMonochrome.value,
            pureBlack = _pureBlack.value,
            expressiveShapes = _expressiveShapes.value
        )
    }

    /**
     * Apply a preset color theme
     * Also applies the associated icon shape
     */
    fun applyPresetColor(presetColor: Color, shape: IconShape) {
        setUseDynamicColor(false)
        setUseMonochrome(false)
        setThemeSeedColor(presetColor)
        setIconShape(shape)
    }

    /**
     * Enable Material You dynamic colors (wallpaper-based)
     * Uses circle shape for dynamic theme
     */
    fun enableDynamicColors() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            setUseDynamicColor(true)
            setUseMonochrome(false)
            setIconShape(IconShape.CIRCLE)
        }
    }

    /**
     * Enable monochrome theme
     * Uses circle shape for monochrome
     */
    fun enableMonochrome() {
        setUseMonochrome(true)
        setUseDynamicColor(false)
        setIconShape(IconShape.CIRCLE)
    }
}

/**
 * Theme configuration data class
 */
data class ThemeConfig(
    val useDynamicColor: Boolean,
    val seedColor: Color,
    val useMonochrome: Boolean,
    val pureBlack: Boolean,
    val expressiveShapes: Boolean
)

/**
 * Preset color palettes for quick theme selection
 * Each preset includes a color and associated icon shape
 */
object ColorPresets {

    data class ColorPreset(
        val name: String,
        val color: Color,
        val description: String,
        val shape: IconShape = IconShape.CIRCLE
    )

    val presets = listOf(
        ColorPreset(
            name = "Material Purple",
            color = ThemeColors.Presets.defaultSeed,
            description = "Classic Material You purple",
            shape = IconShape.ROUNDED_RECTANGLE
        ),
        ColorPreset(
            name = "Teal",
            color = ThemeColors.primary,
            description = "Material Widgets brand teal",
            shape = IconShape.GHOSTISH
        ),
        ColorPreset(
            name = "Red",
            color = ThemeColors.Presets.color0,
            description = "Bold and energetic",
            shape = IconShape.SQUARE
        ),
        ColorPreset(
            name = "Orange",
            color = ThemeColors.Presets.color2,
            description = "Warm and inviting",
            shape = IconShape.HEXAGON
        ),
        ColorPreset(
            name = "Yellow",
            color = ThemeColors.Presets.color3,
            description = "Bright and cheerful",
            shape = IconShape.PENTAGON
        ),
        ColorPreset(
            name = "Green",
            color = ThemeColors.Presets.color5,
            description = "Fresh and natural",
            shape = IconShape.OCTAGON
        ),
        ColorPreset(
            name = "Cyan",
            color = ThemeColors.Presets.color8,
            description = "Cool and modern",
            shape = IconShape.OVAL
        ),
        ColorPreset(
            name = "Blue",
            color = Color(0xFF1976D2),
            description = "Professional and trustworthy",
            shape = IconShape.PILL
        ),
        ColorPreset(
            name = "Indigo",
            color = Color(0xFF3F51B5),
            description = "Deep and sophisticated",
            shape = IconShape.COOKIE
        ),
        ColorPreset(
            name = "Pink",
            color = Color(0xFFE91E63),
            description = "Playful and vibrant",
            shape = IconShape.BUN
        )
    )

    val dynamicPreset = ColorPreset(
        name = "Dynamic (Wallpaper)",
        color = ThemeColors.Presets.dynamic,
        description = "Material You colors from your wallpaper",
        shape = IconShape.CIRCLE // Dynamic always uses circle
    )

    val monochromePreset = ColorPreset(
        name = "Monochrome",
        color = ThemeColors.Presets.monochrome,
        description = "Pure black and white theme",
        shape = IconShape.CIRCLE // Monochrome always uses circle
    )
}

/**
 * Composable extensions for easy theme state collection
 */
@Composable
fun ThemeManager.collectUseDynamicColorAsState(): State<Boolean> {
    return useDynamicColor.collectAsState()
}

@Composable
fun ThemeManager.collectThemeSeedColorAsState(): State<Color> {
    return themeSeedColor.collectAsState()
}

@Composable
fun ThemeManager.collectUseMonochromeAsState(): State<Boolean> {
    return useMonochrome.collectAsState()
}

@Composable
fun ThemeManager.collectPureBlackAsState(): State<Boolean> {
    return pureBlack.collectAsState()
}

@Composable
fun ThemeManager.collectExpressiveShapesAsState(): State<Boolean> {
    return expressiveShapes.collectAsState()
}

@Composable
fun ThemeManager.collectThemeModeAsState(): State<ThemeMode> {
    return themeMode.collectAsState()
}

@Composable
fun ThemeManager.collectFontFamilyAsState(): State<AppFontFamily> {
    return fontFamily.collectAsState()
}

@Composable
fun ThemeManager.collectIconShapeAsState(): State<IconShape> {
    return iconShape.collectAsState()
}
