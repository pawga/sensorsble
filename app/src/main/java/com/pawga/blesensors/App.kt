package com.pawga.blesensors

import android.app.Application
import com.pawga.blesensors.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.startKoin
import timber.log.Timber

/**
 * Created by sivannikov
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeTimber()
        initializeKoin()
        instance = this
    }

    private fun initializeKoin() {
        startKoin {
            androidLogger(org.koin.core.logger.Level.DEBUG)
            androidContext(this@App)
            androidFileProperties()
            fragmentFactory()
            modules(appModule)
        }
    }

    private fun initializeTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    companion object {
        lateinit var instance: App
            private set
    }
}