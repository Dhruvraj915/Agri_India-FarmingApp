package com.project.farmingapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class EcommViewModel : ViewModel() {

    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firebaseFireStore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val firebaseStorage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    var ecommLiveData = MutableLiveData<List<DocumentSnapshot>>()
    var specificCategoryItems = MutableLiveData<List<DocumentSnapshot>>()
    var specificItem = MutableLiveData<DocumentSnapshot>()

    // Loads all products
    fun loadAllEcommItems(): MutableLiveData<List<DocumentSnapshot>> {
        firebaseFireStore.collection("products")
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Log.d("EcommViewModel", "No products found")
                } else {
                    Log.d("EcommViewModel", "First item title: ${result.documents[0].getString("title")}")
                }
                ecommLiveData.value = result.documents
            }
            .addOnFailureListener { exception ->
                Log.e("EcommViewModel", "Error loading all products: ${exception.message}", exception)
            }

        return ecommLiveData
    }

    // Loads specific type of products
    fun loadSpecificTypeEcomItem(itemType: String) {
        firebaseFireStore.collection("products")
            .whereEqualTo("type", itemType)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Log.d("EcommViewModel", "No items of type '$itemType' found")
                } else {
                    Log.d("EcommViewModel", "First item title: ${result.documents[0].getString("title")}")
                }
                ecommLiveData.value = result.documents
            }
            .addOnFailureListener { exception ->
                Log.e("EcommViewModel", "Error loading items of type '$itemType': ${exception.message}", exception)
            }
    }

    // Returns LiveData of products of specific category
    fun getSpecificCategoryItems(itemType: String): MutableLiveData<List<DocumentSnapshot>> {
        firebaseFireStore.collection("products")
            .whereEqualTo("type", itemType)
            .get()
            .addOnSuccessListener { result ->
                specificCategoryItems.value = result.documents
                Log.d("EcommViewModel", "Loaded ${result.documents.size} items of type $itemType")
            }
            .addOnFailureListener { exception ->
                Log.e("EcommViewModel", "Error loading category items: ${exception.message}", exception)
            }

        return specificCategoryItems
    }

    // Returns LiveData of a specific item by ID
    fun getSpecificItem(itemID: String): MutableLiveData<DocumentSnapshot> {
        firebaseFireStore.collection("products")
            .document(itemID)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    specificItem.value = document
                    Log.d("EcommViewModel", "Item loaded: ${document.data}")
                } else {
                    Log.w("EcommViewModel", "No item found with ID: $itemID")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("EcommViewModel", "Error loading item: ${exception.message}", exception)
            }

        return specificItem
    }
}
