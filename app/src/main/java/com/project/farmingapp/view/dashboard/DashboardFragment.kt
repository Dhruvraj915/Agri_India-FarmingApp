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
import com.google.firebase.database.FirebaseDatabase
import com.project.farmingapp.R
import com.project.farmingapp.viewmodel.WeatherViewModel

// STEP 1: Update your data class to hold the unique ID of each product.
// You should move this to its own file (e.g., DashboardItem.kt) eventually.
data class DashboardItem(
    val id: String,
    val title: String,
    val imageUrl: String
)

// Your Adapter - This code is already correct
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

// Your Fragment - This is the final, updated version
class DashboardFragment : Fragment() {

    // ViewModels
    private lateinit var weatherViewModel: WeatherViewModel

    // UI Components
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView

    // RecyclerView components
    private lateinit var adapter: DashboardEcommItemAdapter
    private val items = mutableListOf<DashboardItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the ViewModels. We get them from the Activity to share data.
        weatherViewModel = ViewModelProvider(requireActivity())[WeatherViewModel::class.java]

        // Setup all the different parts of the UI
        setupWeatherCard(view)
        setupProductList(view)

        // Fetch the product data from Firebase
        fetchDashboardData()
    }

    // In DashboardFragment.kt

    // In DashboardFragment.kt

    private fun setupWeatherCard(view: View) {
        // Find all the UI elements from the layout
        val cityTitle = view.findViewById<TextView>(R.id.weatherCityTitle)
        val tempText = view.findViewById<TextView>(R.id.weathTempTextWeathFrag)
        val windText = view.findViewById<TextView>(R.id.windTextWeathFrag)
        val humidityText = view.findViewById<TextView>(R.id.humidityTextWeathFrag)
        val weatherIcon = view.findViewById<ImageView>(R.id.weathIconImageWeathFrag)

        // THE FIX: We now observe the 'weatherData' LiveData from our new ViewModel.
        weatherViewModel.weatherData.observe(viewLifecycleOwner) { weatherRootList ->

            // Check if the received data is valid
            if (weatherRootList != null && weatherRootList.list.isNotEmpty()) {

                // Get the first forecast from the list (which is the most current one)
                val currentForecast = weatherRootList.list[0]

                Toast.makeText(requireContext(), "Weather Updated: ${weatherRootList.city.name}", Toast.LENGTH_SHORT).show()

                // Update all the TextViews with the new data
                cityTitle.text = weatherRootList.city.name
                tempText.text = "${currentForecast.main.temp.toInt()}°C"
                windText.text = "Wind: ${currentForecast.wind.speed} km/h"
                humidityText.text = "Humidity: ${currentForecast.main.humidity}%"

                // Get the weather icon code (e.g., "01d") and build the full URL
                val iconCode = currentForecast.weather[0].icon
                val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"

                // Use Glide to load the weather icon from the URL into your ImageView
                Glide.with(this)
                    .load(iconUrl)
                    .into(weatherIcon)

            } else {
                // This will be called if the data is null or the list is empty
                Log.d("DashboardFragment", "Weather data received but it was null or empty.")
            }
        }
    }

    private fun setupProductList(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewDashboard)
        progressBar = view.findViewById(R.id.progressBar)
        errorText = view.findViewById(R.id.errorText)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.isNestedScrollingEnabled = false // Important for smooth scrolling

        // STEP 2: Update the click listener to navigate to a new fragment
        adapter = DashboardEcommItemAdapter(items) { clickedItem ->
            Log.d("DashboardFragment", "Clicked on product with ID: ${clickedItem.id}")

            // TODO: Create a new Fragment called "ProductDetailFragment"
            // val productDetailFragment = ProductDetailFragment.newInstance(clickedItem.id)

            // requireActivity().supportFragmentManager.beginTransaction()
            //     .replace(R.id.main_content_frame, productDetailFragment)
            //     .addToBackStack(null) // Allows user to press back to return here
            //     .commit()

            Toast.makeText(context, "Navigating to details for ${clickedItem.title}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter
    }

    private fun fetchDashboardData() {
        progressBar.visibility = View.VISIBLE
        val database = FirebaseDatabase.getInstance().reference

        database.child("Products").get()
            .addOnSuccessListener { dataSnapshot ->
                progressBar.visibility = View.GONE
                items.clear()

                if (!dataSnapshot.exists() || !dataSnapshot.hasChildren()) {
                    errorText.text = "No products found in the database."
                    errorText.visibility = View.VISIBLE
                    return@addOnSuccessListener
                }

                Log.d("FirebaseRTDB", "✅ Fetched ${dataSnapshot.childrenCount} products")
                for (snapshot in dataSnapshot.children) {
                    // STEP 3: Get the product's unique key (e.g., "prod1", "prod2")
                    val id = snapshot.key ?: ""
                    val name = snapshot.child("name").getValue(String::class.java) ?: "No Name"
                    val imageUrl = snapshot.child("imageUrl").getValue(String::class.java) ?: ""

                    // Add the item with its ID to the list
                    if (id.isNotEmpty()) {
                        items.add(DashboardItem(id, name, imageUrl))
                    }
                }

                adapter.notifyDataSetChanged()
                errorText.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                errorText.text = "Failed to load data: ${e.message}"
                errorText.visibility = View.VISIBLE
                Log.e("FirebaseRTDB", "❌ Error fetching data", e)
            }
    }
}