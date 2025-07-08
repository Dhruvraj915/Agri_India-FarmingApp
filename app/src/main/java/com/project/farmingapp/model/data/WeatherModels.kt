package com.project.farmingapp.model.data

// This is now the ONLY place these data classes are defined.

// This is the top-level response object from the API
data class WeatherRootList(
    val list: List<WeatherList>,
    val city: City
)

// This represents a single forecast in the 'list'
data class WeatherList(
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind,
    val dt_txt: String
)

// The main weather data for a forecast
// In model/data/WeatherModels.kt
data class Main(
    val temp: Double,
    val humidity: Int,
    val temp_min: Double, // <-- ADD THIS LINE
    val temp_max: Double  // <-- ADD THIS LINE
)

// The description and icon for the weather
data class Weather(
    val description: String,
    val icon: String
)

// Wind information
data class Wind(
    val speed: Double
)

// City information
data class City(
    val name: String
)