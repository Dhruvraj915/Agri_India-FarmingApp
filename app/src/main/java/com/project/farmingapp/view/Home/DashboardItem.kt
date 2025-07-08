package com.project.farmingapp.view.Home

// Data class for Dashboard items
data class DashboardItem(
    val type: String,
    val title: String,
    val imageUrl: String = "",
    val price: Double = 0.0 // Added to match adapter and Firestore
)