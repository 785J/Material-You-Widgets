package com.material.you.widgets.data.repository

import android.content.Context

/**
 * Universal Repository Provider
 * Singleton provider for accessing repositories throughout the app
 * 
 * Usage:
 * ```kotlin
 * // Initialize once in Application onCreate
 * RepositoryProvider.init(context)
 * 
 * // Access anywhere
 * val themeMode = RepositoryProvider.preferences.getThemeMode()
 * RepositoryProvider.preferences.setThemeMode(ThemeMode.DARK)
 * ```
 */
object RepositoryProvider {
    
    private var _preferences: PreferencesRepository? = null
    
    /**
     * Get PreferencesRepository instance
     * Throws IllegalStateException if not initialized
     */
    val preferences: PreferencesRepository
        get() = _preferences ?: throw IllegalStateException(
            "RepositoryProvider not initialized. Call RepositoryProvider.init(context) first."
        )
    
    /**
     * Initialize the repository provider
     * Should be called once in Application.onCreate()
     */
    @Synchronized
    fun init(context: Context) {
        if (_preferences == null) {
            _preferences = PreferencesRepositoryImpl(context.applicationContext)
        }
    }
    
    /**
     * Check if repositories are initialized
     */
    fun isInitialized(): Boolean = _preferences != null
    
    /**
     * For testing: set a custom repository implementation
     */
    fun setPreferencesRepository(repository: PreferencesRepository) {
        _preferences = repository
    }
    
    /**
     * For testing: reset to uninitialized state
     */
    fun reset() {
        _preferences = null
    }
}
