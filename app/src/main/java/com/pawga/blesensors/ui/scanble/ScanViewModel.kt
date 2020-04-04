package com.pawga.blesensors.ui.scanble

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pawga.blesensors.ui.home.HomeViewModel
import timber.log.Timber

class ScanViewModel : ViewModel() {

    fun start() {
        Timber.d("start")
    }

    fun stop() {
        Timber.d("stop")
    }

    /**
     * Factory for [LiveDataViewModel].
     */
    object ViewModelFactory : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ScanViewModel() as T
        }
    }
}
