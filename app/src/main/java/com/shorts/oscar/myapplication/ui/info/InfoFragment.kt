package com.shorts.oscar.myapplication.ui.info

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.shorts.oscar.myapplication.databinding.FragmentInfoBinding
import com.shorts.oscar.myapplication.service.LocationService


// Класс экрана Информации
class InfoFragment : Fragment() {

    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!

    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val latitude = it.getDoubleExtra(LocationService.EXTRA_LATITUDE, 0.0)
                val longitude = it.getDoubleExtra(LocationService.EXTRA_LONGITUDE, 0.0)
                val speed = it.getFloatExtra(LocationService.EXTRA_SPEED, 0.0f)
                val distance = it.getFloatExtra(LocationService.EXTRA_DISTANCE, 0.0f)
                val satellites = it.getIntExtra(LocationService.EXTRA_SATELLITES, 0)
                updateUI(latitude, longitude, speed, distance, satellites)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val infoViewModel = ViewModelProvider(this)[InfoViewModel::class.java]
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            locationReceiver, IntentFilter(LocationService.LOCATION_UPDATE)
        )
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(latitude: Double, longitude: Double, speed: Float, distance: Float, satellites: Int) {
        binding.pbLoading.visibility = View.GONE
        binding.tvLatitude.text = "Широта: $latitude"
        binding.tvLongitude.text = "Долгота: $longitude"
        binding.tvSpeed.text = "Скорость: $speed м/с"
        binding.tvDistance.text = "Расстояние: $distance м"
        binding.tvSatellites.text = "Спутники: $satellites"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(locationReceiver)
    }
}