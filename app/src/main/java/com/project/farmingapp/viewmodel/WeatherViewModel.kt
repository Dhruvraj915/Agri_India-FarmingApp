package com.project.farmingapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.farmingapp.model.WeatherApi  // <-- Make sure this is the correct import
import com.project.farmingapp.model.data.WeatherRootList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherViewModel : ViewModel() {

    // This is the ONLY LiveData the fragment needs to observe.
    var weatherData = MutableLiveData<WeatherRootList>()

    // Your API Key from OpenWeatherMap.
    private val apiKey = "d200d2fc0b0af92c6e7786877c9f4f89" // <-- PASTE YOUR REAL API KEY HERE

    /**
     * This function is called by the Activity when it gets the user's location.
     * It uses the latitude and longitude to make a network call.
     */
    fun fetchWeatherData(latitude: Double, longitude: Double) {

        // Safety check to make sure you've added your key
        if (apiKey.startsWith("YOUR_")) {
            Log.e("WeatherViewModel", "API Key is not set. Please get a key from https://openweathermap.org/api")
            return
        }

        Log.d("WeatherViewModel", "Fetching weather for Lat: $latitude, Lon: $longitude")

        // Use the WeatherApi object to create the API call. This is the fix.
        val call = WeatherApi.weatherInstances.getWeather(
            latitude.toString(),
            longitude.toString(),
            apiKey
        )

        // Execute the call asynchronously
        call.enqueue(object : Callback<WeatherRootList> {
            override fun onResponse(call: Call<WeatherRootList>, response: Response<WeatherRootList>) {
                if (response.isSuccessful && response.body() != null) {
                    weatherData.postValue(response.body())
                    Log.d("WeatherViewModel", "Success! Weather for ${response.body()?.city?.name}")
                } else {
                    Log.e("WeatherViewModel", "API Response Error. Code: ${response.code()}, Message: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<WeatherRootList>, t: Throwable) {
                Log.e("WeatherViewModel", "API network call failed entirely.", t)
            }
        })
    }
}