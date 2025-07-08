// File: com/project/farmingapp/viewmodel/OrderViewModel.kt

package com.project.farmingapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class OrderViewModel : ViewModel() {

    private val ordersLiveData = MutableLiveData<List<QueryDocumentSnapshot>>()
    private val db = FirebaseFirestore.getInstance()

    fun getOrders(uid: String): LiveData<List<QueryDocumentSnapshot>> {
        db.collection("orders")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { result ->
                val orderList = result.documents.mapNotNull { it as? QueryDocumentSnapshot }
                ordersLiveData.value = orderList
            }
            .addOnFailureListener {
                ordersLiveData.value = emptyList()
            }
        return ordersLiveData
    }
}