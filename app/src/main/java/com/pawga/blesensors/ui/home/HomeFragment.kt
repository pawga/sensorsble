package com.pawga.blesensors.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.pawga.blesensors.R
import com.pawga.blesensors.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    // Obtain ViewModel
    private val homeViewModel: HomeViewModel by viewModels { HomeViewModel.ViewModelFactory }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentHomeBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_home,
            container,
            false)

        binding.lifecycleOwner = this
        binding.viewModel = homeViewModel

        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            binding.textHome.text = it
        })

        return binding.root
    }
}
