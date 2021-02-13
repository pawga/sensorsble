package com.pawga.blesensors.ui.scanble

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pawga.common.bluetooth.BluetoothManager

class ScanViewModel(private val bluetoothManager: BluetoothManager) : ViewModel() {

    var callBackScanning: (() -> Unit)? = null

    fun scan() {
        callBackScanning?.invoke()
    }

    fun selectBluetoothDevice(device: BluetoothDevice?) {
        bluetoothManager.bluetoothDevice.value = device
    }

    /**
     * Factory for [LiveDataViewModel].
     */
    class ViewModelFactory(private val bluetoothManager: BluetoothManager) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ScanViewModel(bluetoothManager) as T
        }
    }
}
