package com.project.farmingapp.view.weather

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.farmingapp.adapter.CurrentWeatherAdapter
import com.project.farmingapp.adapter.WeatherAdapter
import com.project.farmingapp.databinding.FragmentWeatherBinding
import com.project.farmingapp.model.data.WeatherList
import com.project.farmingapp.viewmodel.WeatherViewModel

// Note: I've removed WeatherListener as it's no longer needed with the new ViewModel
class WeatherFragment : Fragment() {

    private lateinit var viewModel: WeatherViewModel
    private lateinit var adapter: WeatherAdapter
    private lateinit var adapter2: CurrentWeatherAdapter
    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the ViewModel here
        viewModel = ViewModelProvider(requireActivity())[WeatherViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup the UI and observe the ViewModel
        setupToolbar()
        setupRecyclerViews()
        observeWeatherViewModel()
    }

    private fun setupToolbar() {
        setHasOptionsMenu(true)
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Weather Forecast"
    }

    private fun setupRecyclerViews() {
        // Prepare the adapters with empty lists initially
        adapter = WeatherAdapter(requireContext(), mutableListOf())
        adapter2 = CurrentWeatherAdapter(requireContext(), mutableListOf())

        binding.rcylrWeather.adapter = adapter
        binding.rcylrWeather.layoutManager = LinearLayoutManager(requireContext())

        binding.currentWeatherRcycl.adapter = adapter2
        binding.currentWeatherRcycl.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.HORIZONTAL, false
        )
    }

    private fun observeWeatherViewModel() {
        // THIS IS THE FIX: We observe the single, correct LiveData object
        viewModel.weatherData.observe(viewLifecycleOwner) { weatherRootList ->
            if (weatherRootList == null || weatherRootList.list.isEmpty()) {
                Log.d("WeatherFragment", "Weather data is null or empty.")
                return@observe
            }

            Log.d("WeatherFragment", "Received weather data for ${weatherRootList.city.name}")
            binding.weatherCity.text = weatherRootList.city.name

            // Your original filtering logic, now working with the new LiveData
            val allForecasts = weatherRootList.list
            val firstDate = allForecasts[0].dt_txt.substring(8, 10)
            var otherDate = firstDate
            var i = 1
            val todayForecasts = mutableListOf<WeatherList>()

            while (i <= allForecasts.size && otherDate == firstDate) {
                todayForecasts.add(allForecasts[i - 1])
                otherDate = allForecasts.getOrNull(i)?.dt_txt?.substring(8, 10) ?: break
                i++
            }

            val futureForecasts = mutableListOf<WeatherList>()
            for (a in i - 1 until allForecasts.size) {
                if (allForecasts[a].dt_txt.substring(11, 13) == "12") {
                    futureForecasts.add(allForecasts[a])
                }
            }

            // Update the adapters with the new, filtered data
            adapter.updateData(futureForecasts)
            adapter2.updateData(todayForecasts)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // You will need to add an 'updateData' method to your adapters
    // Example for WeatherAdapter:
    // fun updateData(newItems: List<WeatherList>) {
    //     items.clear()
    //     items.addAll(newItems)
    //     notifyDataSetChanged()
    // }

    companion object {
        @JvmStatic
        fun newInstance() = WeatherFragment()
    }
}