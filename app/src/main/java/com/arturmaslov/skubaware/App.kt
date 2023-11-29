package com.arturmaslov.skubaware

import android.app.Application
import android.content.Context
import com.arturmaslov.skubaware.di.appModule
import com.arturmaslov.skubaware.di.repoModule
import com.arturmaslov.skubaware.di.useCaseModule
import com.arturmaslov.skubaware.di.viewModelModule
import com.facebook.stetho.Stetho
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Stetho.initializeWithDefaults(this)
        }

        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@App)
            // Load modules
            modules(
                listOf(
                    appModule,
                    repoModule,
                    useCaseModule,
                    viewModelModule
                )
            )
        }
    }

    companion object {
        private lateinit var instance: App
        fun getAppContext(): Context {
            return instance.applicationContext
        }
    }
}