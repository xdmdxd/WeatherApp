package com.example.weatherapp.fragments.map

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapPickerFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var geocoder: Geocoder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("MapPickerFragment", "onCreateView called")
        return inflater.inflate(R.layout.fragment_map_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MapPickerFragment", "onViewCreated started")

        geocoder = Geocoder(requireContext(), Locale.getDefault())

        val mapFragment = SupportMapFragment.newInstance()
        Log.d("MapPickerFragment", "Map fragment instance created")

        childFragmentManager
            .beginTransaction()
            .replace(R.id.map_container, mapFragment)
            .commit()
        Log.d("MapPickerFragment", "Map fragment transaction committed")

        mapFragment.getMapAsync(this)
        Log.d("MapPickerFragment", "getMapAsync called")
    }

    override fun onMapReady(map: GoogleMap) {
        Log.d("MapPickerFragment", "onMapReady called")
        googleMap = map

        val defaultLocation = LatLng(50.0755, 14.4378) // Prague
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 6f))
        Log.d("MapPickerFragment", "Camera moved to default location")

        googleMap.setOnMapClickListener { latLng ->
            Log.d("MapPickerFragment", "Map clicked at: ${latLng.latitude}, ${latLng.longitude}")
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(latLng))

            try {
                val addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                val address = addressList?.firstOrNull()

                val city = address?.locality ?: address?.subAdminArea
                val country = address?.countryName

                val locationName = listOfNotNull(city, country)
                    .joinToString(", ")
                    .ifBlank { "Vybraná poloha" }

                Log.d("MapPickerFragment", "Location name resolved: $locationName")

                val result = Bundle().apply {
                    putString("locationText", locationName)
                    putDouble("latitude", latLng.latitude)
                    putDouble("longitude", latLng.longitude)
                }

                parentFragmentManager.setFragmentResult("manualLocationSearch", result)
                findNavController().popBackStack()
                Log.d("MapPickerFragment", "Result set and popped back")
            } catch (e: Exception) {
                Log.e("MapPickerFragment", "Error resolving address: ${e.message}", e)
            }
        }
    }
}
