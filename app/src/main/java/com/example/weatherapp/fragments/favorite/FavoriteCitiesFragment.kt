package com.example.weatherapp.fragments.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.databinding.FragmentFavoriteCitiesBinding
import com.example.weatherapp.storage.SharedPreferencesManager
import org.koin.android.ext.android.inject

class FavoriteCitiesFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteCitiesBinding
    private lateinit var adapter: FavoriteCitiesAdapter
    private val sharedPreferencesManager: SharedPreferencesManager by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteCitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val favoriteCities = sharedPreferencesManager.getFavoriteCities().toMutableList()
        adapter = FavoriteCitiesAdapter(favoriteCities) { cityToDelete ->
            sharedPreferencesManager.removeFavoriteCity(cityToDelete)
            adapter.removeCity(cityToDelete)
            Toast.makeText(requireContext(), "$cityToDelete odstraněno", Toast.LENGTH_SHORT).show()
        }

        binding.favoritesRecyclerView.adapter = adapter
    }
}

