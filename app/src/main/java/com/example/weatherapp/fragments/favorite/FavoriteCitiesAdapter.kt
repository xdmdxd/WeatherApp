package com.example.weatherapp.fragments.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ItemFavoriteCityBinding

class FavoriteCitiesAdapter(
    private val cities: List<String>,
    private val onCityClick: (String) -> Unit
) : RecyclerView.Adapter<FavoriteCitiesAdapter.CityViewHolder>() {

    inner class CityViewHolder(private val binding: ItemFavoriteCityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(city: String) {
            binding.textCityName.text = city
            binding.root.setOnClickListener {
                onCityClick(city)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val binding = ItemFavoriteCityBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(cities[position])
    }

    override fun getItemCount(): Int = cities.size
}
