package com.pawga.blesensors.ui.scanble

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.pawga.blesensors.R
import com.pawga.blesensors.databinding.ScanFragmentBinding
import com.pawga.blesensors.ui.MessageDialogFragment
import com.pawga.blesensors.ui.PermissionRationaleDialogFragment
import com.pawga.common.bluetooth.BluetoothManager
import com.pawga.common.bluetooth.REQUEST_ACCESS_COARSE_LOCATION
import com.pawga.common.bluetooth.REQUEST_ACCESS_FINE_LOCATION
import com.pawga.common.bluetooth.showToast
import org.koin.android.ext.android.inject

class ScanFragment : Fragment(), PermissionRationaleDialogFragment.PermissionDialogListener {

    private val bluetoothManager: BluetoothManager by inject()

    // Obtain ViewModel
    private val viewModel: ScanViewModel by viewModels { ScanViewModel.ViewModelFactory(bluetoothManager) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bluetoothManager.test()

        val binding: ScanFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.scan_fragment,
            container,
            false
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root

        //return inflater.inflate(R.layout.scan_fragment, container, false)
    }

    override fun onRequestPermission(permission: String?, requestCode: Int) {
        if (activity == null || permission == null) return
        ActivityCompat.requestPermissions(activity as Activity, arrayOf(permission), requestCode)
    }

    override fun onCancellingPermissionRationale() {
        showToast(activity, getString(R.string.requested_permission_not_granted_rationale))
    }

    fun checkIfVersionIsQ(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    }

    override fun onResume() {
        super.onResume()

        prepareForScanning()
    }

    fun checkIfVersionIsMarshmallowOrAbove(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    private fun checkIfRequiredPermissionsGranted(): Boolean {
        return if (checkIfVersionIsQ()) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                val dialog: PermissionRationaleDialogFragment =
                    PermissionRationaleDialogFragment.getInstance(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        REQUEST_ACCESS_FINE_LOCATION,
                        getString(R.string.rationale_message_location)
                    )
                dialog.show(childFragmentManager, null)
                false
            }
        } else if (checkIfVersionIsMarshmallowOrAbove()) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                val dialog: PermissionRationaleDialogFragment =
                    PermissionRationaleDialogFragment.getInstance(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        REQUEST_ACCESS_COARSE_LOCATION,
                        getString(R.string.rationale_message_location)
                    )
                dialog.show(childFragmentManager, null)
                false
            }
        } else {
            true
        }
    }

    fun isLocationEnabled(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var locationMode = Settings.Secure.LOCATION_MODE_OFF
            try {
                locationMode = Settings.Secure.getInt(
                    requireContext().contentResolver,
                    Settings.Secure.LOCATION_MODE
                )
            } catch (e: SettingNotFoundException) {
                // do nothing
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF
        }
        return true
    }

    private fun prepareForScanning() {
        if (checkIfRequiredPermissionsGranted()) {
            if (isLocationEnabled()) {
                // установить статус поиска
                bluetoothManager.startScan()
            } else {
                val messageDialogFragment: MessageDialogFragment =
                    MessageDialogFragment.newInstance(
                        getString(R.string.location_services_title),
                        getString(R.string.rationale_message_location)
                    )
                messageDialogFragment.show(childFragmentManager, null)
            }
        }
    }
}
