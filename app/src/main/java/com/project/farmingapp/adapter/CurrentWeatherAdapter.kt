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

class CurrentWeatherAdapter(
    private val context: Context,
    private val weatherrootdatas: List<WeatherList>
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

    override fun getItemCount(): Int = weatherrootdatas.size

    override fun onBindViewHolder(holder: CurrentWeatherViewHolder, position: Int) {
        val weather = weatherrootdatas[position]
        val binding = holder.binding

        binding.temp.text = "${(weather.main.temp - 273.15).toInt()}℃"
        binding.desc.text = weather.weather[0].description.capitalize()
        binding.todayTitle.text = "Today, " + weather.dt_txt.slice(10..15)

        try {
            Log.d("Something", weather.dt_txt.slice(10..weather.dt_txt.length - 1))
        } catch (e: Exception) {
            Log.e("SliceError", e.message ?: "Invalid datetime format")
        }

        binding.minTemp.text = "${(weather.main.temp_min.toDouble() - 273.1).toInt()}℃"
        binding.maxTemp.text = "${(weather.main.temp_max.toDouble() - 273.1).toInt()}℃"
        binding.humidity.text = "${weather.main.humidity}%"

        val iconUrl = "https://openweathermap.org/img/w/${weather.weather[0].icon}.png"
        Glide.with(context)
            .load(iconUrl)
            .into(binding.icon)

        binding.currentWeatherContainer.animation = AnimationUtils.loadAnimation(context, R.anim.fade_scale)
    }
}
