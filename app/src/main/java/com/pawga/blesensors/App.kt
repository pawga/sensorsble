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
            androidLogger()
            androidContext(this@App)
            androidFileProperties()
            fragmentFactory()
            // TODO Await fix for Koin and replace the explicit invocations
            //  of loadModules() and createRootScope() with a single call to modules()
            //  (https://github.com/InsertKoinIO/koin/issues/847)
            koin.loadModules(listOf(appModule))
            koin.createRootScope()
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
