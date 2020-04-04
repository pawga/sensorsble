package com.pawga.common.bluetooth

import android.bluetooth.BluetoothDevice
import android.os.Handler
import android.os.ParcelUuid
import no.nordicsemi.android.support.v18.scanner.*
import no.nordicsemi.android.thingylib.utils.ThingyUtils
import timber.log.Timber
import java.util.*

/**
 * Created by sivannikov
 */
class BluetoothManager {

    var isScanning = false

    fun test() {
        Timber.d("test")
    }

    fun startScan() {
        if (isScanning) {
            return
        }
        val scanner = BluetoothLeScannerCompat.getScanner()
        val settings =
            ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(0).setUseHardwareBatchingIfSupported(false)
                .setUseHardwareFilteringIfSupported(false)
                .build()
        val filters: MutableList<ScanFilter> = ArrayList()
        filters.add(
            ScanFilter.Builder().setServiceUuid(
                ParcelUuid(ThingyUtils.THINGY_BASE_UUID)
            ).build()
        )
        scanner.startScan(filters, settings, scanCallback)
        isScanning = true
    }

    private fun stopScan() {
        if (isScanning) {
            Timber.v("Stopping scan")
            val scanner = BluetoothLeScannerCompat.getScanner()
            scanner.stopScan(scanCallback)
            isScanning = false
        }
    }

    private fun connect() {
//        mThingySdkManager.connectToThingy(this, mDevice, ThingyService::class.java)
//        val thingy = Thingy(mDevice)
//        mThingySdkManager.setSelectedDevice(mDevice)
//        updateSelectionInDb(thingy, true)
    }

    private fun connect(device: BluetoothDevice) {
//        mThingySdkManager.connectToThingy(this, device, ThingyService::class.java)
//        val thingy = Thingy(device)
//        mThingySdkManager.setSelectedDevice(device)
//        updateSelectionInDb(thingy, true)
//        updateUiOnBind()
    }

    private val device: BluetoothDevice? = null
    private val oldDevice: BluetoothDevice? = null

    private var address: String? = null
    private val scanCallback: ScanCallback =
        object : ScanCallback() {
            override fun onScanResult(
                callbackType: Int,
                result: ScanResult
            ) {
                // do nothing
                val device = result.device
                if (address != null && address == device.address) {
                    //mProgressHandler.removeCallbacks(mProgressDialogRunnable)
                    stopScan()
                    connect(device)
                    address = null
                    return
                }
                if (device == this@BluetoothManager.device) {
                    Handler().post {
                        stopScan()
                        connect()
                    }
                }
            }

            override fun onBatchScanResults(results: List<ScanResult>) {}
            override fun onScanFailed(errorCode: Int) {
                // should never be called
                Timber.i("onScanFailed")
            }
        }
}