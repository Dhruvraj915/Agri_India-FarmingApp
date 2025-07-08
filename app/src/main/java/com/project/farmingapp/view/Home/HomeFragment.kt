package com.project.farmingapp.view.Home

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.project.farmingapp.R
import com.project.farmingapp.adapter.DashboardEcomItemAdapter
import com.project.farmingapp.utilities.CellClickListener

class HomeFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView
    private lateinit var adapter: DashboardEcomItemAdapter
    private val items = mutableListOf<DashboardItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewDashboard)
        progressBar = view.findViewById(R.id.progressBar)
        errorText = view.findViewById(R.id.errorText)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = DashboardEcomItemAdapter(requireContext(), items, object : CellClickListener {
            override fun onCellClickListener(name: String) {
                Toast.makeText(requireContext(), "Item $name clicked", Toast.LENGTH_SHORT).show()
            }
        })
        recyclerView.adapter = adapter
        fetchDashboardData()
        return view
    }

    private fun fetchDashboardData() {
        if (!FirestoreUtils.isOnline(requireContext())) {
            Toast.makeText(context, "Offline: Showing cached data", Toast.LENGTH_LONG).show()
        }
        progressBar.visibility = View.VISIBLE
        FirestoreUtils.queryFirestore(
            query = FirebaseFirestore.getInstance().collection("products").limit(3),
            context = requireContext(),
            dataList = items,
            mapper = { result ->
                result.map { doc ->
                    DashboardItem("Product", doc.getString("name") ?: "", doc.getString("imageUrl") ?: "")
                }
            },
            onComplete = {
                progressBar.visibility = View.GONE
                adapter.notifyDataSetChanged()
            })
        if (!isOnline(requireContext())) {
            errorText.visibility = View.VISIBLE
            errorText.text = "No internet connection. Using cached data."
            Toast.makeText(requireContext(), "Offline mode", Toast.LENGTH_SHORT).show()
        } else {
            errorText.visibility = View.GONE
            Toast.makeText(requireContext(), "Fetching data online...", Toast.LENGTH_SHORT).show()
        }

        progressBar.visibility = View.VISIBLE
        FirebaseFirestore.getInstance().collection("products")
            .limit(3)
            .get()
            .addOnSuccessListener { result ->
                progressBar.visibility = View.GONE
                errorText.visibility = View.GONE
                items.clear()
                Log.d("Firestore", "Fetched ${result.size()} products")
                for (doc in result) {
                    val name = doc.getString("name") ?: "No Name"
                    val imageUrl = doc.getString("imageUrl") ?: ""
                    val price = doc.getDouble("price") ?: 0.0
                    Log.d("Firestore", "Product: $name, Image: $imageUrl, Price: $price")
                    items.add(DashboardItem("Product", name, imageUrl, price))
                }
                adapter.notifyDataSetChanged()
                if (items.isEmpty()) {
                    errorText.visibility = View.VISIBLE
                    errorText.text = "No products available"
                    Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                errorText.visibility = View.VISIBLE
                errorText.text = "Error: ${e.message}"
                Toast.makeText(requireContext(), "Error fetching data: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("Firestore", "Error: ${e.message}")
            }
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        return network != null
    }
}