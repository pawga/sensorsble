package com.pawga.common.bluetooth

import android.bluetooth.BluetoothDevice
import android.os.Handler
import android.os.ParcelUuid
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import no.nordicsemi.android.support.v18.scanner.*
import no.nordicsemi.android.thingylib.utils.ThingyUtils
import timber.log.Timber
import java.util.*

/**
 * Created by sivannikov
 */
class BluetoothManager {

    var isScanning = false
        private set

    val thingyBaseUuid: UUID
        get() {
            return ThingyUtils.THINGY_BASE_UUID
        }

    private val SCAN_DURATION: Long = 80000 // настроить
    private val handler = Handler()

    private val _scanResults = MutableLiveData<List<ScanResult>>()
    val scanResults: LiveData<List<ScanResult>> = _scanResults

    val bluetoothDevice = MutableLiveData<BluetoothDevice?>()

    fun startScan() {
        if (isScanning) return

        val scanner = BluetoothLeScannerCompat.getScanner()
        val settings =
            ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(750).setUseHardwareBatchingIfSupported(false)
                .setUseHardwareFilteringIfSupported(false).build()
        val filters: MutableList<ScanFilter> =
            ArrayList()
        filters.add(
            ScanFilter.Builder().setServiceUuid(
                ParcelUuid(thingyBaseUuid)
            ).build()
        )
        scanner.startScan(filters, settings, scanCallback)
        isScanning = true
        handler.postDelayed(Runnable {
            if (isScanning) {
                stopScan()
            }
        }, SCAN_DURATION)
    }

    fun stopScan() {
        if (isScanning) {
            //mScanButton.setText(R.string.scanner_action_scan)
            val scanner = BluetoothLeScannerCompat.getScanner()
            scanner.stopScan(scanCallback)
            isScanning = false
        }
    }

    private val scanCallback: ScanCallback =
        object : ScanCallback() {
            override fun onScanResult(
                callbackType: Int,
                result: ScanResult
            ) {
                // do nothing
            }

            override fun onBatchScanResults(results: List<ScanResult>) {
                _scanResults.value = results
            }

            override fun onScanFailed(errorCode: Int) {
                // should never be called
            }
        }
}