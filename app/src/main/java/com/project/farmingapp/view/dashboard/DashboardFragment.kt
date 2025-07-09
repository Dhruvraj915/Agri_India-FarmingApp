package com.project.farmingapp.view.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore // Import Firestore
import com.project.farmingapp.R
import com.project.farmingapp.view.ecommerce.EcommerceItemFragment
import com.project.farmingapp.viewmodel.WeatherViewModel

// Data class for items shown in the dashboard's product list
data class DashboardItem(
    val id: String,       // Firestore Document ID
    val title: String,
    val imageUrl: String
)

// The Adapter for the dashboard's product list
class DashboardEcommItemAdapter(
    private val items: List<DashboardItem>,
    private val onCellClickListener: (DashboardItem) -> Unit
) : RecyclerView.Adapter<DashboardEcommItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dashboard, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.titleText.text = item.title
        if (item.imageUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(item.imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imageView)
        }
        holder.itemView.setOnClickListener { onCellClickListener(item) }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.itemTitle)
        val imageView: ImageView = itemView.findViewById(R.id.itemImage)
    }
}


// The Main Dashboard Fragment Class
class DashboardFragment : Fragment() {

    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView
    private lateinit var adapter: DashboardEcommItemAdapter
    private val items = mutableListOf<DashboardItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel from the parent Activity
        weatherViewModel = ViewModelProvider(requireActivity())[WeatherViewModel::class.java]

        // Setup the different UI sections of the screen
        setupWeatherCard(view)
        setupProductList(view)

        // Fetch product data from Firestore
        fetchDashboardDataFromFirestore()
    }

    private fun setupWeatherCard(view: View) {
        val cityTitle = view.findViewById<TextView>(R.id.weatherCityTitle)
        val tempText = view.findViewById<TextView>(R.id.weathTempTextWeathFrag)
        val windText = view.findViewById<TextView>(R.id.windTextWeathFrag)
        val humidityText = view.findViewById<TextView>(R.id.humidityTextWeathFrag)
        val weatherIcon = view.findViewById<ImageView>(R.id.weathIconImageWeathFrag)

        weatherViewModel.weatherData.observe(viewLifecycleOwner) { weatherRootList ->
            if (weatherRootList != null && weatherRootList.list.isNotEmpty()) {
                val currentForecast = weatherRootList.list[0]
                cityTitle.text = weatherRootList.city.name
                tempText.text = "${currentForecast.main.temp.toInt()}°C"
                windText.text = "Wind: ${currentForecast.wind.speed} km/h"
                humidityText.text = "Humidity: ${currentForecast.main.humidity}%"

                val iconCode = currentForecast.weather[0].icon
                val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
                Glide.with(this).load(iconUrl).into(weatherIcon)
            }
        }
    }

    private fun setupProductList(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewDashboard)
        progressBar = view.findViewById(R.id.progressBar)
        errorText = view.findViewById(R.id.errorText)

        // The product list on the dashboard is horizontal
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.isNestedScrollingEnabled = false

        adapter = DashboardEcommItemAdapter(items) { clickedItem ->
            Log.d("DashboardFragment", "Navigating to product with ID: ${clickedItem.id}")

            // Use your existing EcommerceItemFragment
            val ecommerceItemFragment = EcommerceItemFragment.newInstance(clickedItem.id)

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_content_frame, ecommerceItemFragment)
                .addToBackStack(null) // Allows user to press back to return here
                .commit()
        }
        recyclerView.adapter = adapter
    }

    private fun fetchDashboardDataFromFirestore() {
        progressBar.visibility = View.VISIBLE
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("E-commerce").get()
            .addOnSuccessListener { querySnapshot ->
                progressBar.visibility = View.GONE
                items.clear()

                if (querySnapshot.isEmpty) {
                    errorText.text = "No products found."
                    errorText.visibility = View.VISIBLE
                    return@addOnSuccessListener
                }

                for (document in querySnapshot.documents) {
                    val id = document.id
                    val name = document.getString("title") ?: "No Name"
                    val imageUrls = document.get("imageUrl") as? List<String>
                    val firstImageUrl = imageUrls?.firstOrNull() ?: ""

                    items.add(DashboardItem(id, name, firstImageUrl))
                }
                adapter.notifyDataSetChanged()
                errorText.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                errorText.text = "Failed to load products: ${e.message}"
                errorText.visibility = View.VISIBLE
                Log.e("DashboardFragment", "Error fetching from Firestore", e)
            }
    }
}