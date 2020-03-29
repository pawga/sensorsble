package com.pawga.blesensors.ui.scanble

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels

import com.pawga.blesensors.R
import com.pawga.blesensors.ui.home.HomeViewModel

class ScanFragment : Fragment() {

    // Obtain ViewModel
    private val viewModel: ScanViewModel by viewModels { ScanViewModel.ViewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.scan_fragment, container, false)
    }
}
