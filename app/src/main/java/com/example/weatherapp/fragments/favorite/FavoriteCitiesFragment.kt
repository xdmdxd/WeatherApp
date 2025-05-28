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

    private var _binding: FragmentFavoriteCitiesBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val sharedPreferencesManager: SharedPreferencesManager by inject()
    private lateinit var adapter: FavoriteCitiesAdapter

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

        val favoriteCities = sharedPreferencesManager.getFavoriteCities().toList()

        adapter = FavoriteCitiesAdapter(favoriteCities) { selectedCity ->
            Toast.makeText(requireContext(), "Vybráno: $selectedCity", Toast.LENGTH_SHORT).show()
            // Optionally, go back to HomeFragment with selectedCity
            findNavController().navigateUp()
        }

        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.favoritesRecyclerView.adapter = adapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
