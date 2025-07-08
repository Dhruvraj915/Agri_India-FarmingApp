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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.project.farmingapp.R
import com.project.farmingapp.view.Home.DashboardItem

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

// Your Fragment - This is the updated version
class DashboardFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView
    private lateinit var adapter: DashboardEcommItemAdapter
    private val items = mutableListOf<DashboardItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // Initialize all views from the layout
        recyclerView = view.findViewById(R.id.recyclerViewDashboard)
        progressBar = view.findViewById(R.id.progressBar)
        errorText = view.findViewById(R.id.errorText)

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.isNestedScrollingEnabled = false // Important for smooth scrolling in a NestedScrollView
        adapter = DashboardEcommItemAdapter(items) { item ->
            Toast.makeText(context, "Clicked: ${item.title}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter

        // Fetch the data from Firebase
        fetchDashboardData()

        return view
    }

    private fun fetchDashboardData() {
        progressBar.visibility = View.VISIBLE
        val database = FirebaseDatabase.getInstance().reference

        database.child("Products").get()
            .addOnSuccessListener { dataSnapshot ->
                progressBar.visibility = View.GONE
                items.clear() // Clear old data

                if (!dataSnapshot.exists() || !dataSnapshot.hasChildren()) {
                    errorText.text = "No products found in the database."
                    errorText.visibility = View.VISIBLE
                    return@addOnSuccessListener
                }

                Log.d("FirebaseRTDB", "✅ Fetched ${dataSnapshot.childrenCount} products")
                for (snapshot in dataSnapshot.children) {
                    val name = snapshot.child("name").getValue(String::class.java) ?: "No Name"
                    val imageUrl = snapshot.child("imageUrl").getValue(String::class.java) ?: ""
                    items.add(DashboardItem("Product", name, imageUrl))
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