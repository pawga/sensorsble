package com.pawga.blesensors.ui.scanble

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.ParcelUuid
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.pawga.blesensors.R
import com.pawga.blesensors.databinding.ScanFragmentBinding
import com.pawga.blesensors.ui.PermissionRationaleDialogFragment
import com.pawga.common.bluetooth.BluetoothManager
import com.pawga.common.bluetooth.showToast
import no.nordicsemi.android.support.v18.scanner.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.*

class ScanFragment : Fragment(), PermissionRationaleDialogFragment.PermissionDialogListener {

    private val REQUEST_PERMISSION_REQ_CODE = 76 // any 8-bit number
    private val SCAN_DURATION: Long = 80000
    private val bluetoothManager: BluetoothManager by inject()

    private val viewModel: ScanViewModel by viewModels { ScanViewModel.ViewModelFactory(bluetoothManager) }
    private var isScanning = false
    private val handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: ScanFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.scan_fragment,
            container,
            false
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
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
            ) != PackageManager.PERMISSION_GRANTED) {
            // When user pressed Deny and still wants to use this functionality, show the rationale
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                return
            }
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSION_REQ_CODE)
            return
        }
//        mAdapter.clearDevices()
//        mScanButton.setText(R.string.scanner_action_cancel)
//        troubleshootView.setVisibility(View.VISIBLE)
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
                ParcelUuid(bluetoothManager.thingyBaseUuid)
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

    private fun stopScan() {
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
                Timber.d("onBatchScanResults results.size: ${results.size}")
//                if (results.size > 0 && troubleshootView.getVisibility() == View.VISIBLE) {
//                    troubleshootView.setVisibility(View.GONE)
//                }
//                mAdapter.update(results)
            }

            override fun onScanFailed(errorCode: Int) {
                // should never be called
            }
        }
}
