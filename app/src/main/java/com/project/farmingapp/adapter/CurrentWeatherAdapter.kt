package com.project.farmingapp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.farmingapp.R
import com.project.farmingapp.databinding.SingleCurrentweatherBinding
import com.project.farmingapp.model.data.WeatherList
import java.text.SimpleDateFormat
import java.util.*

// The constructor now takes a MutableList, so we can change it later.
class CurrentWeatherAdapter(
    private val context: Context,
    private val weatherItems: MutableList<WeatherList>
) : RecyclerView.Adapter<CurrentWeatherAdapter.CurrentWeatherViewHolder>() {

    inner class CurrentWeatherViewHolder(val binding: SingleCurrentweatherBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrentWeatherViewHolder {
        val binding = SingleCurrentweatherBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return CurrentWeatherViewHolder(binding)
    }

    override fun getItemCount(): Int = weatherItems.size

    override fun onBindViewHolder(holder: CurrentWeatherViewHolder, position: Int) {
        val currentItem = weatherItems[position]
        val binding = holder.binding

        // Set the text for temperature, description, etc.
        binding.temp.text = "${currentItem.main.temp.toInt()}°C"
        binding.desc.text = currentItem.weather[0].description.replaceFirstChar { it.uppercase() }

        // Safely format the time
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault()) // Format for "15:00"
            val date: Date? = inputFormat.parse(currentItem.dt_txt)
            binding.todayTitle.text = if (date != null) outputFormat.format(date) else "N/A"
        } catch (e: Exception) {
            binding.todayTitle.text = "Invalid Time"
            Log.e("CurrentWeatherAdapter", "Time parsing failed", e)
        }

        // Set min/max temp and humidity
        binding.minTemp.text = "${currentItem.main.temp_min.toInt()}°C"
        binding.maxTemp.text = "${currentItem.main.temp_max.toInt()}°C"
        binding.humidity.text = "${currentItem.main.humidity}%"

        // Load weather icon with Glide
        val iconUrl = "https://openweathermap.org/img/wn/${currentItem.weather[0].icon}@2x.png" // Using bigger icon
        Glide.with(context)
            .load(iconUrl)
            .into(binding.icon)

        // Apply animation
        binding.currentWeatherContainer.animation = AnimationUtils.loadAnimation(context, R.anim.fade_scale)
    }

    // THIS IS THE NEW, IMPORTANT FUNCTION
    // This allows the WeatherFragment to update the list of today's forecasts.
    fun updateData(newItems: List<WeatherList>) {
        weatherItems.clear()
        weatherItems.addAll(newItems)
        notifyDataSetChanged() // This tells the RecyclerView to redraw itself
    }
}