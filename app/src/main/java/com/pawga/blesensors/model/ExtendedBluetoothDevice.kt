package com.pawga.blesensors.model

import android.bluetooth.BluetoothDevice
import no.nordicsemi.android.support.v18.scanner.ScanResult

/**
 * Created by sivannikov
 */

class ExtendedBluetoothDevice(scanResult: ScanResult) {
    var device: BluetoothDevice
    var rssi: Int
    var name: String?

    init {
        device = scanResult.device
        name = scanResult.scanRecord?.deviceName
        rssi = scanResult.rssi
    }

    fun matches(scanResult: ScanResult): Boolean {
        return device.address == scanResult.device.address
    }

    override fun equals(other: Any?): Boolean {
        if (other is ExtendedBluetoothDevice) {
            return device.address == other.device.address
        }
        return super.equals(other)
    }
}
