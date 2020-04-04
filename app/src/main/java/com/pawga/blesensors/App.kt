package com.pawga.blesensors

import android.app.Application
import timber.log.Timber

/**
 * Created by sivannikov
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeTimber()
        instance = this
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