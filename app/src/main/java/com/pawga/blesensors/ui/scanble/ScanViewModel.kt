package com.pawga.blesensors.ui.scanble

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pawga.common.bluetooth.BluetoothManager
import org.koin.android.ext.android.inject
import org.koin.experimental.property.inject
import timber.log.Timber

class ScanViewModel(val bluetoothManager: BluetoothManager) : ViewModel() {

    fun start() {
        Timber.d("start")
    }

    fun stop() {
        Timber.d("stop")
    }

    /**
     * Factory for [LiveDataViewModel].
     */
    class ViewModelFactory(val bluetoothManager: BluetoothManager) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ScanViewModel(bluetoothManager) as T
        }
    }
}
