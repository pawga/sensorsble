package com.pawga.blesensors

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.pawga.common.bluetooth.BluetoothManager
import com.pawga.common.bluetooth.ThingyService
import com.pawga.common.bluetooth.ThingyService.ThingyBinder
import no.nordicsemi.android.thingylib.ThingyListener
import no.nordicsemi.android.thingylib.ThingyListenerHelper
import no.nordicsemi.android.thingylib.ThingySdkManager
import org.koin.android.ext.android.inject
import timber.log.Timber


class MainActivity : AppCompatActivity(), ThingySdkManager.ServiceConnectionListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var thingySdkManager: ThingySdkManager
    private var binder: ThingyBinder? = null
    private var device: BluetoothDevice? = null

    private val bluetoothManager: BluetoothManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        thingySdkManager = ThingySdkManager.getInstance()

        bluetoothManager.bluetoothDevice.observe(this, Observer {
            if (it != null) {
                device = it
                thingySdkManager.connectToThingy(this, device, ThingyService::class.java)
            } else if (device != null) {
                thingySdkManager.disconnectFromAllThingies()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        thingySdkManager.bindService(this, ThingyService::class.java)
        ThingyListenerHelper.registerThingyListener(this, thingyListener)
    }

    override fun onStop() {
        if (device != null) {
            thingySdkManager.disconnectFromAllThingies()
        }
        thingySdkManager.unbindService(this);
        super.onStop()
    }

    override fun onServiceConnected() {
        binder = thingySdkManager.getThingyBinder() as ThingyBinder
        if (device != null && thingySdkManager.hasInitialServiceDiscoverCompleted(device)) {
            onServiceDiscoveryCompletion(device!!)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.id_action_search -> {
                bluetoothManager.bluetoothDevice.value = null
                navController.navigate(R.id.nav_scam)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
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
            Timber.d("Temperature: $temperature")
        }

        override fun onPressureValueChangedEvent(
            bluetoothDevice: BluetoothDevice,
            pressure: String
        ) {
            Timber.d("Pressure: $pressure")
        }

        override fun onHumidityValueChangedEvent(
            bluetoothDevice: BluetoothDevice,
            humidity: String
        ) {
            Timber.d("Humidity: $humidity")
        }

        override fun onAirQualityValueChangedEvent(
            bluetoothDevice: BluetoothDevice,
            eco2: Int,
            tvoc: Int
        ) {
            Timber.d("AirQuality: $eco2 - $tvoc")
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
