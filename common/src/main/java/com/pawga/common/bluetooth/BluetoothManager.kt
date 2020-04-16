package com.pawga.common.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Handler
import android.os.ParcelUuid
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import no.nordicsemi.android.support.v18.scanner.*
import no.nordicsemi.android.thingylib.BaseThingyService
import no.nordicsemi.android.thingylib.ThingyListener
import no.nordicsemi.android.thingylib.ThingyListenerHelper
import no.nordicsemi.android.thingylib.ThingySdkManager
import no.nordicsemi.android.thingylib.utils.ThingyUtils
import timber.log.Timber
import java.util.*

/**
 * Created by sivannikov
 */
class BluetoothManager {

    private lateinit var thingySdkManager: ThingySdkManager
    private var device: BluetoothDevice? = null
    private var binder: ThingyService.ThingyBinder? = null
    private lateinit var activityContext: AppCompatActivity
    private var isConnected = false

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

    private val _temperature = MutableLiveData<Double?>()
    val temperature: LiveData<Double?> = _temperature // Температура

    private val _pressure = MutableLiveData<Double?>()
    val pressure: LiveData<Double?> = _pressure // Давление

    private val _humidity = MutableLiveData<Int?>()
    val humidity: LiveData<Int?> = _humidity // Влажность

    private val _carbon = MutableLiveData<Double?>()
    val carbon: LiveData<Double?> = _carbon // Концентрации углекислого газа ( сО2 )

    private val _tvoc = MutableLiveData<Double?>()
    val tvoc: LiveData<Double?> = _tvoc // Концентрации совокупных летучих органических соединений

    init {
        thingySdkManager = ThingySdkManager.getInstance()
    }

    fun bindService(context: AppCompatActivity, service: Class<out BaseThingyService?>?) {
        if (isConnected) return

        activityContext = context
        thingySdkManager.bindService(activityContext, service)
        ThingyListenerHelper.registerThingyListener(activityContext, thingyListener)

        bluetoothDevice.observe(activityContext, androidx.lifecycle.Observer {
            if (it != null) {
                device = it
                thingySdkManager.connectToThingy(activityContext, device, ThingyService::class.java)
            } else if (device != null) {
                thingySdkManager.disconnectFromAllThingies()
            }
        })
        isConnected = true
    }

    fun unbindService(context: Context) {
        if (device != null) {
            thingySdkManager.disconnectFromAllThingies()
        }
        thingySdkManager.unbindService(activityContext)
        isConnected = false
    }

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

    fun onServiceConnected() {
        binder = thingySdkManager.getThingyBinder() as ThingyService.ThingyBinder
        if (device != null && thingySdkManager.hasInitialServiceDiscoverCompleted(device)) {
            onServiceDiscoveryCompletion(device!!)
        }
    }

    private fun onServiceDiscoveryCompletion(device: BluetoothDevice) {
        thingySdkManager.enableEnvironmentNotifications(device, true)
    }

    private val thingyListener: ThingyListener = object : ThingyListener {
        override fun onDeviceConnected(device: BluetoothDevice, connectionState: Int) {
            Timber.d("onDeviceConnected")
        }

        override fun onDeviceDisconnected(device: BluetoothDevice, connectionState: Int) {
            Timber.d("onDeviceDisconnected")
        }

        override fun onServiceDiscoveryCompleted(device: BluetoothDevice) {
            onServiceDiscoveryCompletion(device)
        }

        override fun onBatteryLevelChanged(
            bluetoothDevice: BluetoothDevice,
            batteryLevel: Int
        ) {
        }

        override fun onTemperatureValueChangedEvent(
            bluetoothDevice: BluetoothDevice,
            temperature: String
        ) {
            this@BluetoothManager._temperature.value = temperature.toDoubleOrNull()
        }

        override fun onPressureValueChangedEvent(
            bluetoothDevice: BluetoothDevice,
            pressure: String
        ) {
            val _pressure = pressure.toDoubleOrNull()
            if (_pressure != null) {
                this@BluetoothManager._pressure.value = _pressure / 1.333333 // перевод в мм рт столба
            } else {
                this@BluetoothManager._pressure.value = null
            }
        }

        override fun onHumidityValueChangedEvent(
            bluetoothDevice: BluetoothDevice,
            humidity: String
        ) {
            this@BluetoothManager._humidity.value = humidity.toIntOrNull()
        }

        override fun onAirQualityValueChangedEvent(
            bluetoothDevice: BluetoothDevice,
            eco2: Int,
            tvoc: Int
        ) {
            this@BluetoothManager._carbon.value = eco2.toDouble()
            this@BluetoothManager._tvoc.value = tvoc.toDouble()
        }

        override fun onColorIntensityValueChangedEvent(
            bluetoothDevice: BluetoothDevice,
            red: Float,
            green: Float,
            blue: Float,
            alpha: Float
        ) {
            Timber.d("ColorIntensity: $alpha:$red:$green:$blue")
        }

        override fun onButtonStateChangedEvent(
            bluetoothDevice: BluetoothDevice,
            buttonState: Int
        ) {
        }

        override fun onTapValueChangedEvent(
            bluetoothDevice: BluetoothDevice,
            direction: Int,
            count: Int
        ) {
        }

        override fun onOrientationValueChangedEvent(
            bluetoothDevice: BluetoothDevice,
            orientation: Int
        ) {
        }

        override fun onQuaternionValueChangedEvent(
            bluetoothDevice: BluetoothDevice,
            w: Float,
            x: Float,
            y: Float,
            z: Float
        ) {
        }

        override fun onPedometerValueChangedEvent(
            bluetoothDevice: BluetoothDevice,
            steps: Int,
            duration: Long
        ) {
        }

        override fun onAccelerometerValueChangedEvent(
            bluetoothDevice: BluetoothDevice,
            x: Float,
            y: Float,
            z: Float
        ) {
        }

        override fun onGyroscopeValueChangedEvent(
            bluetoothDevice: BluetoothDevice,
            x: Float,
            y: Float,
            z: Float
        ) {
        }

        override fun onCompassValueChangedEvent(
            bluetoothDevice: BluetoothDevice,
            x: Float,
            y: Float,
            z: Float
        ) {
        }

        override fun onEulerAngleChangedEvent(
            bluetoothDevice: BluetoothDevice,
            roll: Float,
            pitch: Float,
            yaw: Float
        ) {
        }

        override fun onRotationMatrixValueChangedEvent(
            bluetoothDevice: BluetoothDevice,
            matrix: ByteArray
        ) {
        }

        override fun onHeadingValueChangedEvent(
            bluetoothDevice: BluetoothDevice,
            heading: Float
        ) {
        }

        override fun onGravityVectorChangedEvent(
            bluetoothDevice: BluetoothDevice,
            x: Float,
            y: Float,
            z: Float
        ) {
        }

        override fun onSpeakerStatusValueChangedEvent(
            bluetoothDevice: BluetoothDevice,
            status: Int
        ) {
        }

        override fun onMicrophoneValueChangedEvent(
            bluetoothDevice: BluetoothDevice,
            data: ByteArray
        ) {
        }
    }
}