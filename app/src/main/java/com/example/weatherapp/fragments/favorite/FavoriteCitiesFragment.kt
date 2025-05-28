package com.example.weatherapp.fragments.favorite

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.data.CurrentLocation
import com.example.weatherapp.databinding.FragmentFavoriteCitiesBinding
import com.example.weatherapp.fragments.home.HomeFragment
import com.example.weatherapp.storage.SharedPreferencesManager
import org.koin.android.ext.android.inject
import java.util.Locale

class FavoriteCitiesFragment : Fragment() {

    private var _binding: FragmentFavoriteCitiesBinding? = null
    private val binding get() = _binding!!

    private val sharedPreferencesManager: SharedPreferencesManager by inject()
    private lateinit var adapter: FavoriteCitiesAdapter

    private val geocoder by lazy { Geocoder(requireContext(), Locale.getDefault()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteCitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val favoriteCities = sharedPreferencesManager.getFavoriteCities().toMutableList()

        adapter = FavoriteCitiesAdapter(
            cities = favoriteCities,
            onDeleteClicked = { cityToDelete ->
                sharedPreferencesManager.removeFavoriteCity(cityToDelete)
                adapter.removeCity(cityToDelete)
                Toast.makeText(requireContext(), "$cityToDelete odstraněno", Toast.LENGTH_SHORT).show()
            },
            onCityClicked = { selectedCity ->
                val addressList = geocoder.getFromLocationName(selectedCity, 1)
                if (!addressList.isNullOrEmpty()) {
                    val address = addressList[0]
                    val currentLocation = CurrentLocation(
                        location = selectedCity,
                        latitude = address.latitude,
                        longitude = address.longitude
                    )
                    sharedPreferencesManager.saveCurrentLocation(currentLocation)

                    // ✅ Set result for HomeFragment
                    setFragmentResult(
                        HomeFragment.REQUEST_KEY_MANUAL_LOCATION_SEARCH,
                        bundleOf(
                            HomeFragment.KEY_LOCATION_TEXT to currentLocation.location,
                            HomeFragment.KEY_LATITUDE to currentLocation.latitude,
                            HomeFragment.KEY_LONGITUDE to currentLocation.longitude
                        )
                    )

                    findNavController().popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Nepodařilo se najít lokaci", Toast.LENGTH_SHORT).show()
                }
            }
        )

        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.favoritesRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
