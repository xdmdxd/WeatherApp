package com.example.weatherapp.fragments.home

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.weatherapp.data.CurrentLocation
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.depencency_injection.viewModelModule
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment(){

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val homeViewModel: HomeViewModel by viewModel()
    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private val geocoder by lazy { Geocoder(requireContext()) }


    private val weatherDataAdapter = WeatherDataAdapter(
        onLocationClicked = {
            showLocationOption()
        }
    )

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getCurrentLocation()
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            _binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWeatherDataAdapter()
        setWeatherData()
        setObservers()
    }

    private fun setObservers(){
        with (homeViewModel){
            currentLocation.observe (viewLifecycleOwner){
                val currentLocationDataState=it?:return@observe
                if (currentLocationDataState.isLoading){
                    showLoading()
                }
                currentLocationDataState.currentLocation?.let { currentLocation->
                    hideLoading()
                    setWeatherData(currentLocation)
                }
                currentLocationDataState.error?.let { error->
                    hideLoading()
                    Toast.makeText(requireContext(),error,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setWeatherDataAdapter(){

        binding.weatherDataRecyclerView.adapter=weatherDataAdapter
    }

    private fun setWeatherData(currentLocation: CurrentLocation?=null) {
        weatherDataAdapter.setData(
            data = listOf(currentLocation?: CurrentLocation())
        )
    }


    private fun getCurrentLocation(){
        homeViewModel.getCurrentLocation(fusedLocationProviderClient,geocoder)
    }

    private fun isLocationPermissionGranted():Boolean{
        return ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        )== PackageManager.PERMISSION_GRANTED
    }
    private fun requestLocationPermission(){
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun proceedWithCurrentLocation(){
        if (isLocationPermissionGranted()){
            getCurrentLocation()
        }else{
            requestLocationPermission()
        }
    }

    private fun showLocationOption(){
        val options= arrayOf("Aktuální poloha", "Vyhledat polohu")
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Vybrat způsob lokace")
            setItems(options) {_,which ->
                when(which){
                    0 -> proceedWithCurrentLocation()
                }
            }
            show()
        }
    }
    private fun showLoading(){
        with (binding){
            weatherDataRecyclerView.visibility=View.GONE
            swipeRefreshLayout.isRefreshing=true

        }
    }

    private fun hideLoading(){
        with (binding){
            weatherDataRecyclerView.visibility=View.VISIBLE
            swipeRefreshLayout.isRefreshing=false
        }
    }
}