package com.pawga.blesensors.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.pawga.blesensors.R
import com.pawga.blesensors.ui.SensorViews.SensorBigView
import com.pawga.blesensors.ui.SensorViews.SensorView
import com.pawga.common.bluetooth.BluetoothManager
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.ext.android.inject


class HomeFragment : Fragment() {

    // Obtain ViewModel
    private val homeViewModel: HomeViewModel by viewModels { HomeViewModel.ViewModelFactory }
    private val bluetoothManager: BluetoothManager by inject()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val bigView = view.findViewById(R.id.sensorBigView) as? SensorBigView
        if (bigView != null) {
            bluetoothManager.carbon.observe(viewLifecycleOwner, Observer {
                bigView.value = it
            })
        }

        val temperatureView = view.findViewById(R.id.sensorTemperatureView) as? SensorView
        if (temperatureView != null) {
            bluetoothManager.temperature.observe(viewLifecycleOwner, Observer {
                temperatureView.value = it
            })
        }

        val humidityView = view.findViewById(R.id.sensorHumidityView) as? SensorView
        if (humidityView != null) {
            bluetoothManager.humidity.observe(viewLifecycleOwner, Observer {
                humidityView.value = it?.toDouble() ?: 0.0
            })
        }

        val pressureView = view.findViewById(R.id.sensorPressureView) as? SensorView
        if (pressureView != null) {
            bluetoothManager.pressure.observe(viewLifecycleOwner, Observer {
                pressureView.value = it
            })
        }

        val leafView = view.findViewById(R.id.sensorLeafView) as? SensorView
        if (leafView != null) {
            bluetoothManager.tvoc.observe(viewLifecycleOwner, Observer {
                leafView.value = it
            })
        }

        return view
    }
}
