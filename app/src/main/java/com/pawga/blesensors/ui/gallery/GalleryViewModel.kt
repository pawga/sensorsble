package com.pawga.blesensors.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pawga.blesensors.ui.slideshow.SlideshowViewModel

class GalleryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }
    val text: LiveData<String> = _text


    /**
     * Factory for [LiveDataViewModel].
     */
    object ViewModelFactory : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return GalleryViewModel() as T
        }
    }
}