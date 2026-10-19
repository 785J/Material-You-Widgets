package com.material.you.widgets.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.material.you.widgets.data.models.AppFontFamily
import com.material.you.widgets.data.models.IconShape
import com.material.you.widgets.data.models.ThemeMode

/**
 * Universal Preferences Repository
 * Single source of truth for all SharedPreferences access
 * 
 * Benefits:
 * - Centralized preference management
 * - Type-safe access
 * - Easy to test and mock
 * - Clear API for all preference operations
 */
interface PreferencesRepository {
    
    // ============ Theme Preferences ============
    
    fun getThemeMode(): ThemeMode
    fun setThemeMode(mode: ThemeMode)
    
    fun getIconShape(): IconShape
    fun setIconShape(shape: IconShape)
    
    fun getFontFamily(): AppFontFamily
    fun setFontFamily(family: AppFontFamily)
    
    fun useDynamicColor(): Boolean
    fun setUseDynamicColor(enabled: Boolean)

    fun getSeedColor(): Int
    fun setSeedColor(color: Int)

    fun useMonochrome(): Boolean
    fun setMonochrome(enabled: Boolean)

    fun usePureBlack(): Boolean
    fun setPureBlack(enabled: Boolean)
    
    fun useExpressive(): Boolean
    fun setExpressive(enabled: Boolean)
    
    // ============ Widget Appearance ============
    
    fun getWidgetCornerRadius(): Int
    fun setWidgetCornerRadius(radius: Int)
    
    fun getWidgetOpacity(): Float
    fun setWidgetOpacity(opacity: Float)
    
    fun useWidgetDynamicColors(): Boolean
    fun setWidgetDynamicColors(enabled: Boolean)
    
    fun useWidgetPureBlack(): Boolean
    fun setWidgetPureBlack(enabled: Boolean)
    
    // ============ Widget States ============
    
    fun getTorchState(): Boolean
    fun setTorchState(isOn: Boolean)
    
    fun getDndState(): Boolean
    fun setDndState(isOn: Boolean)
    
    fun getRingerMode(): Int
    fun setRingerMode(mode: Int)
    
    // ============ Calendar Widget ============
    
    fun isCalendarSyncEnabled(): Boolean
    fun setCalendarSyncEnabled(enabled: Boolean)
    
    fun getCalendarRefreshInterval(): String
    fun setCalendarRefreshInterval(interval: String)
    
    fun showAllDayEvents(): Boolean
    fun setShowAllDayEvents(show: Boolean)
    
    fun getMaxEvents(): String
    fun setMaxEvents(max: String)
    
    // ============ Generic Access (for custom preferences) ============
    
    fun getString(key: String, defaultValue: String): String
    fun setString(key: String, value: String)
    
    fun getInt(key: String, defaultValue: Int): Int
    fun setInt(key: String, value: Int)
    
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun setBoolean(key: String, value: Boolean)
    
    fun getFloat(key: String, defaultValue: Float): Float
    fun setFloat(key: String, value: Float)
    
    fun getLong(key: String, defaultValue: Long): Long
    fun setLong(key: String, value: Long)
    
    // ============ Utility ============
    
    fun clear()
    fun contains(key: String): Boolean
}

/**
 * Default implementation using SharedPreferences
 */
class PreferencesRepositoryImpl(context: Context) : PreferencesRepository {
    
    private val appPrefs: SharedPreferences = context.getSharedPreferences(
        "Otter_prefs",
        Context.MODE_PRIVATE
    )
    
    private val widgetPrefs: SharedPreferences = context.getSharedPreferences(
        "widget_settings",
        Context.MODE_PRIVATE
    )
    
    companion object {
        // Theme keys — the single canonical key set. ThemeManager reads/writes
        // through this repository, so its legacy keys map onto these exactly.
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_ICON_SHAPE = "icon_shape"
        private const val KEY_FONT_FAMILY = "font_family"
        private const val KEY_USE_DYNAMIC_COLOR = "use_dynamic_color"
        private const val KEY_SEED_COLOR = "theme_seed_color"
        private const val KEY_USE_MONOCHROME = "use_monochrome"
        private const val KEY_PURE_BLACK = "pure_black"
        private const val KEY_EXPRESSIVE = "expressive_shapes"

        // Widget appearance keys (must match WidgetAppearanceHelper)
        private const val KEY_WIDGET_CORNER_RADIUS = "widget_corner_radius"
        private const val KEY_WIDGET_OPACITY = "widget_opacity"
        private const val KEY_WIDGET_DYNAMIC_COLORS = "widget_use_dynamic_colors"
        private const val KEY_WIDGET_PURE_BLACK = "widget_pure_black"

        // Widget state keys
        private const val KEY_TORCH_STATE = "torch_is_on"
        private const val KEY_DND_STATE = "dnd_is_on"
        private const val KEY_RINGER_MODE = "ringer_mode"

        // Calendar keys
        private const val KEY_CALENDAR_SYNC = "calendar_sync_enabled"
        private const val KEY_CALENDAR_REFRESH = "calendar_refresh_interval"
        private const val KEY_SHOW_ALL_DAY = "show_all_day_events"
        private const val KEY_MAX_EVENTS = "max_events"

        // Default values
        private const val DEFAULT_SEED_COLOR = 0xFF6750A4.toInt() // Material You purple
        private const val DEFAULT_CORNER_RADIUS = 28
        private const val DEFAULT_OPACITY = 1.0f
    }
    
    // ============ Theme Preferences ============
    
    override fun getThemeMode(): ThemeMode {
        val ordinal = appPrefs.getInt(KEY_THEME_MODE, ThemeMode.SYSTEM.ordinal)
        return ThemeMode.entries.getOrNull(ordinal) ?: ThemeMode.SYSTEM
    }
    
    override fun setThemeMode(mode: ThemeMode) {
        appPrefs.edit().putInt(KEY_THEME_MODE, mode.ordinal).apply()
    }
    
    override fun getIconShape(): IconShape {
        val ordinal = appPrefs.getInt(KEY_ICON_SHAPE, IconShape.GHOSTISH.ordinal)
        return IconShape.entries.getOrNull(ordinal) ?: IconShape.GHOSTISH
    }
    
    override fun setIconShape(shape: IconShape) {
        appPrefs.edit().putInt(KEY_ICON_SHAPE, shape.ordinal).apply()
    }
    
    override fun getFontFamily(): AppFontFamily {
        val ordinal = appPrefs.getInt(KEY_FONT_FAMILY, AppFontFamily.BITCOUNT.ordinal)
        return AppFontFamily.entries.getOrNull(ordinal) ?: AppFontFamily.BITCOUNT
    }
    
    override fun setFontFamily(family: AppFontFamily) {
        appPrefs.edit().putInt(KEY_FONT_FAMILY, family.ordinal).apply()
    }
    
    override fun useDynamicColor(): Boolean =
        appPrefs.getBoolean(KEY_USE_DYNAMIC_COLOR, true)
    
    override fun setUseDynamicColor(enabled: Boolean) {
        appPrefs.edit().putBoolean(KEY_USE_DYNAMIC_COLOR, enabled).apply()
    }
    
    override fun getSeedColor(): Int =
        appPrefs.getInt(KEY_SEED_COLOR, DEFAULT_SEED_COLOR)

    override fun setSeedColor(color: Int) {
        appPrefs.edit().putInt(KEY_SEED_COLOR, color).apply()
    }

    override fun useMonochrome(): Boolean =
        appPrefs.getBoolean(KEY_USE_MONOCHROME, false)

    override fun setMonochrome(enabled: Boolean) {
        appPrefs.edit().putBoolean(KEY_USE_MONOCHROME, enabled).apply()
    }

    override fun usePureBlack(): Boolean =
        appPrefs.getBoolean(KEY_PURE_BLACK, false)

    override fun setPureBlack(enabled: Boolean) {
        appPrefs.edit().putBoolean(KEY_PURE_BLACK, enabled).apply()
    }

    override fun useExpressive(): Boolean =
        appPrefs.getBoolean(KEY_EXPRESSIVE, false)

    override fun setExpressive(enabled: Boolean) {
        appPrefs.edit().putBoolean(KEY_EXPRESSIVE, enabled).apply()
    }

    // ============ Widget Appearance ============

    override fun getWidgetCornerRadius(): Int =
        widgetPrefs.getInt(KEY_WIDGET_CORNER_RADIUS, DEFAULT_CORNER_RADIUS)

    override fun setWidgetCornerRadius(radius: Int) {
        widgetPrefs.edit().putInt(KEY_WIDGET_CORNER_RADIUS, radius).apply()
    }

    override fun getWidgetOpacity(): Float =
        widgetPrefs.getFloat(KEY_WIDGET_OPACITY, DEFAULT_OPACITY)

    override fun setWidgetOpacity(opacity: Float) {
        widgetPrefs.edit().putFloat(KEY_WIDGET_OPACITY, opacity).apply()
    }

    override fun useWidgetDynamicColors(): Boolean =
        widgetPrefs.getBoolean(KEY_WIDGET_DYNAMIC_COLORS, true)

    override fun setWidgetDynamicColors(enabled: Boolean) {
        widgetPrefs.edit().putBoolean(KEY_WIDGET_DYNAMIC_COLORS, enabled).apply()
    }
    
    override fun useWidgetPureBlack(): Boolean =
        widgetPrefs.getBoolean(KEY_WIDGET_PURE_BLACK, false)
    
    override fun setWidgetPureBlack(enabled: Boolean) {
        widgetPrefs.edit().putBoolean(KEY_WIDGET_PURE_BLACK, enabled).apply()
    }

    // ============ Widget States ============

    override fun getTorchState(): Boolean =
        widgetPrefs.getBoolean(KEY_TORCH_STATE, false)

    override fun setTorchState(isOn: Boolean) {
        widgetPrefs.edit().putBoolean(KEY_TORCH_STATE, isOn).apply()
    }

    override fun getDndState(): Boolean =
        widgetPrefs.getBoolean(KEY_DND_STATE, false)

    override fun setDndState(isOn: Boolean) {
        widgetPrefs.edit().putBoolean(KEY_DND_STATE, isOn).apply()
    }

    override fun getRingerMode(): Int =
        widgetPrefs.getInt(KEY_RINGER_MODE, 2)

    override fun setRingerMode(mode: Int) {
        widgetPrefs.edit().putInt(KEY_RINGER_MODE, mode).apply()
    }

    // ============ Calendar Widget ============

    override fun isCalendarSyncEnabled(): Boolean =
        widgetPrefs.getBoolean(KEY_CALENDAR_SYNC, true)
    
    override fun setCalendarSyncEnabled(enabled: Boolean) {
        widgetPrefs.edit().putBoolean(KEY_CALENDAR_SYNC, enabled).apply()
    }
    
    override fun getCalendarRefreshInterval(): String =
        widgetPrefs.getString(KEY_CALENDAR_REFRESH, "30") ?: "30"
    
    override fun setCalendarRefreshInterval(interval: String) {
        widgetPrefs.edit().putString(KEY_CALENDAR_REFRESH, interval).apply()
    }
    
    override fun showAllDayEvents(): Boolean =
        widgetPrefs.getBoolean(KEY_SHOW_ALL_DAY, true)
    
    override fun setShowAllDayEvents(show: Boolean) {
        widgetPrefs.edit().putBoolean(KEY_SHOW_ALL_DAY, show).apply()
    }
    
    override fun getMaxEvents(): String =
        widgetPrefs.getString(KEY_MAX_EVENTS, "5") ?: "5"
    
    override fun setMaxEvents(max: String) {
        widgetPrefs.edit().putString(KEY_MAX_EVENTS, max).apply()
    }
    
    // ============ Generic Access ============
    
    override fun getString(key: String, defaultValue: String): String =
        appPrefs.getString(key, defaultValue) ?: defaultValue
    
    override fun setString(key: String, value: String) {
        appPrefs.edit().putString(key, value).apply()
    }
    
    override fun getInt(key: String, defaultValue: Int): Int =
        appPrefs.getInt(key, defaultValue)
    
    override fun setInt(key: String, value: Int) {
        appPrefs.edit().putInt(key, value).apply()
    }
    
    override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        appPrefs.getBoolean(key, defaultValue)
    
    override fun setBoolean(key: String, value: Boolean) {
        appPrefs.edit().putBoolean(key, value).apply()
    }
    
    override fun getFloat(key: String, defaultValue: Float): Float =
        appPrefs.getFloat(key, defaultValue)
    
    override fun setFloat(key: String, value: Float) {
        appPrefs.edit().putFloat(key, value).apply()
    }
    
    override fun getLong(key: String, defaultValue: Long): Long =
        appPrefs.getLong(key, defaultValue)
    
    override fun setLong(key: String, value: Long) {
        appPrefs.edit().putLong(key, value).apply()
    }
    
    // ============ Utility ============
    
    override fun clear() {
        appPrefs.edit().clear().apply()
        widgetPrefs.edit().clear().apply()
    }
    
    override fun contains(key: String): Boolean =
        appPrefs.contains(key) || widgetPrefs.contains(key)
}
