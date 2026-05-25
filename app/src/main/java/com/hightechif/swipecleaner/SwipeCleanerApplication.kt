package com.hightechif.swipecleaner

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class SwipeCleanerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Log Koin events with Android Logger
            androidLogger(Level.ERROR)
            // Reference Android context
            androidContext(this@SwipeCleanerApplication)
            // Load modules
            modules(appModule)
        }
    }
}
