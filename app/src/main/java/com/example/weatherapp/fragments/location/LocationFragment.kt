package com.example.weatherapp.fragments.location

import android.content.Context
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethod
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.data.RemoteLocation
import com.example.weatherapp.databinding.FragmentLocationBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.http.Query

class LocationFragment : Fragment() {

    private var _binding: FragmentLocationBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val locationViewModel: LocationViewModel by viewModel()

    private val locationsAdapter= LocationsAdapter(
        onLocationClicked = {remoteLocation ->
            setLocation(remoteLocation)
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root

   }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setupLocationsRecyclerView()
        setObservers()
    }

    private fun setupLocationsRecyclerView() {
        with(binding.locationsRecyclerView) {
            addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
            adapter = locationsAdapter
        }
    }


    private fun setListeners() {
        binding.imageClose.setOnClickListener { findNavController().popBackStack() }

        binding.inputSearch.editText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideSoftKeyboard()
                val query = binding.inputSearch.editText?.text
                if (query.isNullOrBlank()) return@setOnEditorActionListener true
                searchLocation(query.toString())
            }
            return@setOnEditorActionListener true
        }
    }


    private fun setLocation(remoteLocation: RemoteLocation) {
        val result = Bundle().apply {
            putString("locationText", remoteLocation.name)
            putDouble("latitude", remoteLocation.lat)
            putDouble("longitude", remoteLocation.lon)
        }

        parentFragmentManager.setFragmentResult("manualLocationSearch", result)
        findNavController().popBackStack()
    }



    private fun setObservers() {
        locationViewModel.searchResult.observe(viewLifecycleOwner) {
            val searchResultDataState = it ?: return@observe

            if (searchResultDataState.isLoading) {
                binding.locationsRecyclerView.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }

            searchResultDataState.locations?.let { remoteLocations ->
                binding.locationsRecyclerView.visibility = View.VISIBLE
                locationsAdapter.setData(remoteLocations)
            }

            searchResultDataState.error?.let { error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }

    }




    private fun searchLocation(query: String){
        locationViewModel.searchLocation(query)
    }

    private fun hideSoftKeyboard() {
        val inputManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            binding.inputSearch.editText?.windowToken,0
        )
    }

}
