package com.arturmaslov.skubaware

import android.app.Application
import com.arturmaslov.skubaware.di.appModule
import com.arturmaslov.tgnba.di.repoModule
import com.arturmaslov.tgnba.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@App)
            // Load modules
            modules(listOf(appModule, repoModule, viewModelModule))
        }

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}