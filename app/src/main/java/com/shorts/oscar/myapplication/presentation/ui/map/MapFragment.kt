package com.shorts.oscar.myapplication.presentation.ui.map

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.shorts.oscar.myapplication.databinding.FragmentMapBinding
import com.shorts.oscar.myapplication.presentation.utils.PermissionHelper
import com.shorts.oscar.myapplication.service.LocationService


// Класс экрана карты
class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var googleMap: GoogleMap
    private val mapViewModel: MapViewModel by viewModels()
    private var currentMarker: Marker? = null
    private lateinit var permissionHelper: PermissionHelper

    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val latitude = it.getDoubleExtra(LocationService.EXTRA_LATITUDE, 0.0)
                val longitude = it.getDoubleExtra(LocationService.EXTRA_LONGITUDE, 0.0)
                updateMarker(LatLng(latitude, longitude))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionHelper = PermissionHelper(requireContext())
        fetchLocation()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnMyLocation.setOnClickListener {
            fetchLocation()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        // Убираем стандартные кнопки
        googleMap.uiSettings.isZoomControlsEnabled = false
        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.uiSettings.isMyLocationButtonEnabled = false
    }

    private fun fetchLocation() {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        googleMap.isMyLocationEnabled = true
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (location != null) {
            val latitude = location.latitude
            val longitude = location.longitude
            // Обновить маркер на карте
            val latLng = LatLng(location.latitude, location.longitude)
            updateMarker(latLng)
            Toast.makeText(requireContext(), "Latitude: $latitude, Longitude: $longitude", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Не удалось получить текущее местоположение", Toast.LENGTH_SHORT).show()
        }
    }

    //Обновить маркер на карте
    private fun updateMarker(latLng: LatLng) {
        googleMap.apply {
            currentMarker?.remove()
            currentMarker = addMarker(MarkerOptions().position(latLng).title("My Location"))
            animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            locationReceiver, IntentFilter(LocationService.LOCATION_UPDATE)
        )
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(locationReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
