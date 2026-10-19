package com.material.you.widgets

import android.app.Application
import com.material.you.widgets.core.theme.ThemeManager
import com.material.you.widgets.data.repository.RepositoryProvider
import com.material.you.widgets.worker.WorkManagerScheduler
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WidgetsApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Repository first: ThemeManager persists through it
        RepositoryProvider.init(this)
        ThemeManager.initialize(this)

        // Schedule periodic background updates for calendar widgets
        // (no-op when the user disabled auto updates)
        WorkManagerScheduler.schedulePeriodicUpdates(this)
    }
}
