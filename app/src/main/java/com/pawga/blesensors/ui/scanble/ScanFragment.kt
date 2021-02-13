package com.pawga.blesensors.ui.scanble

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pawga.blesensors.R
import com.pawga.blesensors.databinding.ScanFragmentBinding
import com.pawga.blesensors.ui.PermissionRationaleDialogFragment
import com.pawga.common.bluetooth.BluetoothManager
import com.pawga.common.bluetooth.showToast
import org.koin.android.ext.android.inject

class ScanFragment : Fragment(), PermissionRationaleDialogFragment.PermissionDialogListener {

    private val REQUEST_PERMISSION_REQ_CODE = 76 // any 8-bit number
    private val bluetoothManager: BluetoothManager by inject()

    private val viewModel: ScanViewModel by viewModels { ScanViewModel.ViewModelFactory(bluetoothManager) }
    private lateinit var adapter: DeviceListAdapter
    private lateinit var binding: ScanFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        adapter = DeviceListAdapter(viewModel)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.scan_fragment,
            container,
            false
        )
        binding.devicesList.adapter = adapter
        binding.devicesList.layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.callBackScanning = {
            if (bluetoothManager.isScanning) {
                stopScan()
            } else {
                startScan()
            }
        }

        bluetoothManager.scanResults.observe(
            viewLifecycleOwner,
            Observer {
                adapter.update(it)
            }
        )

        bluetoothManager.bluetoothDevice.observe(
            viewLifecycleOwner,
            Observer {
                if (it != null) {
                    findNavController().popBackStack()
                }
            }
        )

        return binding.root
    }

    override fun onRequestPermission(permission: String?, requestCode: Int) {
        if (activity == null || permission == null) return
        ActivityCompat.requestPermissions(activity as Activity, arrayOf(permission), requestCode)
    }

    override fun onCancellingPermissionRationale() {
        showToast(activity, getString(R.string.requested_permission_not_granted_rationale))
    }

    override fun onResume() {
        super.onResume()
        startScan()
    }

    override fun onStop() {
        // Stop scan moved from onDestroyView to onStop
        stopScan()
        super.onStop()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION_REQ_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We have been granted the Manifest.permission.ACCESS_FINE_LOCATION permission. Now we may proceed with scanning.
                startScan()
            } else {
                Toast.makeText(
                    activity,
                    R.string.rationale_permission_denied,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun startScan() {
        // Since Android 6.0 we need to obtain either Manifest.permission.ACCESS_FINE_LOCATION or Manifest.permission.ACCESS_FINE_LOCATION to be able to scan for
        // Bluetooth LE devices. This is related to beacons as proximity devices.
        // On API older than Marshmallow the following code does nothing.
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // When user pressed Deny and still wants to use this functionality, show the rationale
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                return
            }
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSION_REQ_CODE
            )
            return
        }

        adapter.clearDevices()

        binding.satusTextView.setText(R.string.scanning)
        binding.statusProgressBar.visibility = View.VISIBLE

        bluetoothManager.startScan()
    }

    private fun stopScan() {
        if (bluetoothManager.isScanning) {
            bluetoothManager.stopScan()
            binding.statusProgressBar.visibility = View.INVISIBLE
            binding.satusTextView.setText(R.string.scan_stopped)
        }
    }
}
