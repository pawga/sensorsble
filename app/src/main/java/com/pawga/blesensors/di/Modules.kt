package com.pawga.blesensors.di

import com.pawga.common.bluetooth.BluetoothManager
import org.koin.dsl.module

/**
 * Created by sivannikov
 */
val appModule = module {
    single { BluetoothManager() }
}
