package com.project.farmingapp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.farmingapp.R
import com.project.farmingapp.model.data.WeatherList
import java.text.SimpleDateFormat
import java.util.*

// The constructor now takes a MutableList, so we can change it later.
class WeatherAdapter(private val context: Context, private val weatherItems: MutableList<WeatherList>) :
    RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val weatherDate: TextView = itemView.findViewById(R.id.weatherDate)
        val weatherDescription: TextView = itemView.findViewById(R.id.weatherDescription)
        val weatherTemperature: TextView = itemView.findViewById(R.id.weatherTemperature)
        val weatherIcon: ImageView = itemView.findViewById(R.id.weatherIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.single_weather, parent, false)
        return WeatherViewHolder(view)
    }

    override fun getItemCount(): Int {
        return weatherItems.size
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val currentItem = weatherItems[position]

        // Date formatting logic (your original code was correct)
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date: Date? = inputFormat.parse(currentItem.dt_txt)
            holder.weatherDate.text = if (date != null) outputFormat.format(date) else "N/A"
        } catch (e: Exception) {
            holder.weatherDate.text = "Invalid Date"
            Log.e("WeatherAdapter", "Date parsing failed", e)
        }

        val weatherInfo = currentItem.weather[0]
        val mainInfo = currentItem.main

        // Update UI elements
        holder.weatherDescription.text = weatherInfo.description.replaceFirstChar { it.uppercase() }
        holder.weatherTemperature.text = "${mainInfo.temp.toInt()}°C"

        // Load weather icon with Glide
        val iconCode = weatherInfo.icon
        val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
        Glide.with(holder.itemView.context)
            .load(iconUrl)
            .into(holder.weatherIcon)
    }

    // THIS IS THE NEW, IMPORTANT FUNCTION
    // This allows the WeatherFragment to update the list of weather forecasts.
    fun updateData(newItems: List<WeatherList>) {
        weatherItems.clear()
        weatherItems.addAll(newItems)
        notifyDataSetChanged() // This tells the RecyclerView to redraw itself with the new data
    }
}