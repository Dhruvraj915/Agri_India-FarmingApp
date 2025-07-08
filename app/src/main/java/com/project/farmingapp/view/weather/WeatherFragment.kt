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
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.farmingapp.adapter.CurrentWeatherAdapter
import com.project.farmingapp.adapter.WeatherAdapter
import com.project.farmingapp.databinding.FragmentWeatherBinding
import com.project.farmingapp.model.data.WeatherList
import com.project.farmingapp.viewmodel.WeatherListener
import com.project.farmingapp.viewmodel.WeatherViewModel

class WeatherFragment : Fragment(), WeatherListener {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var viewModel: WeatherViewModel
    private lateinit var adapter: WeatherAdapter
    private lateinit var adapter2: CurrentWeatherAdapter
    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        viewModel = ViewModelProvider(requireActivity())[WeatherViewModel::class.java]

        val bundle = this.arguments
        bundle?.getString("key")?.let {
            Log.d("WeatherFrag Bundle", it)
        }
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

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.title = "Weather Forecast"

        val city = viewModel.getCoordinates().value
        binding.weatherCity.text = city?.getOrNull(2).toString()

        val newWeatherData = viewModel.newDataTrial.value
        Log.d("New Data Weather Trial", newWeatherData.toString())

        if (newWeatherData != null && newWeatherData.list.isNotEmpty()) {
            val firstDate = newWeatherData.list[0].dt_txt.substring(8, 10)
            var otherDate = firstDate
            var i = 1
            val data2 = mutableListOf<WeatherList>()

            while (i < newWeatherData.list.size && otherDate == firstDate) {
                data2.add(newWeatherData.list[i - 1])
                otherDate = newWeatherData.list.getOrNull(i)?.dt_txt?.substring(8, 10) ?: break
                i++
            }

            val data3 = mutableListOf<WeatherList>()
            for (a in i - 1 until newWeatherData.list.size) {
                if (newWeatherData.list[a].dt_txt.substring(11, 13) == "12") {
                    Log.d("Filtered date", newWeatherData.list[a].dt_txt)
                    data3.add(newWeatherData.list[a])
                }
            }

            adapter = WeatherAdapter(requireContext(), data3)
            adapter2 = CurrentWeatherAdapter(requireContext(), data2)

            binding.rcylrWeather.adapter = adapter
            binding.rcylrWeather.layoutManager = LinearLayoutManager(requireContext())

            binding.currentWeatherRcycl.adapter = adapter2
            binding.currentWeatherRcycl.layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSuccess(authRepo: LiveData<String>) {
        authRepo.observe(viewLifecycleOwner) {
            Log.d("WeatherFragment", it.toString())
        }
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WeatherFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
