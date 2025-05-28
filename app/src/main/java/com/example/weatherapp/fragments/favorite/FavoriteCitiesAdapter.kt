package com.example.weatherapp.fragments.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ItemFavoriteCityBinding

class FavoriteCitiesAdapter(
    private val cities: MutableList<String>,
    private val onDeleteClicked: (String) -> Unit
) : RecyclerView.Adapter<FavoriteCitiesAdapter.CityViewHolder>() {

    inner class CityViewHolder(private val binding: ItemFavoriteCityBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(cityName: String) {
            binding.textCityName.text = cityName
            binding.imageDelete.setOnClickListener {
                onDeleteClicked(cityName)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val binding = ItemFavoriteCityBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CityViewHolder(binding)
    }

    override fun getItemCount() = cities.size

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(cities[position])
    }

    fun removeCity(cityName: String) {
        val index = cities.indexOf(cityName)
        if (index != -1) {
            cities.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}
