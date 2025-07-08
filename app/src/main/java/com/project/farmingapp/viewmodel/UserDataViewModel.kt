package com.project.farmingapp.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class UserDataViewModel : ViewModel() {

    val userLiveData = MutableLiveData<DocumentSnapshot>()
    private val firebaseFirestore = FirebaseFirestore.getInstance()

    fun getUserData(userId: String) {
        firebaseFirestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                userLiveData.value = document
                Log.d("UserDataViewModel", "Fetched user data for ID: $userId")
            }
            .addOnFailureListener {
                Log.e("UserDataViewModel", "Failed to fetch user data: ${it.message}", it)
            }
    }

    fun updateUserField(context: Context, userID: String, about: String?, city: String?) {
        val updates = mutableMapOf<String, Any>()

        if (!about.isNullOrEmpty()) updates["about"] = about
        if (!city.isNullOrEmpty()) updates["city"] = city

        if (updates.isNotEmpty()) {
            firebaseFirestore.collection("users")
                .document(userID)
                .update(updates)
                .addOnSuccessListener {
                    Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
                    Log.d("UserDataViewModel", "Updated fields: $updates")
                    getUserData(userID)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to update profile. Try again!", Toast.LENGTH_SHORT).show()
                    Log.e("UserDataViewModel", "Update failed: ${e.message}", e)
                }
        } else {
            Toast.makeText(context, "Nothing to update.", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteUserPost(userId: String, postId: String) {
        firebaseFirestore.collection("posts")
            .document(postId)
            .delete()
            .addOnSuccessListener {
                Log.d("UserDataViewModel", "Post $postId deleted successfully.")

                // Remove post from user's post list
                firebaseFirestore.collection("users")
                    .document(userId)
                    .update("posts", FieldValue.arrayRemove(postId))
                    .addOnSuccessListener {
                        Log.d("UserDataViewModel", "Removed $postId from user's post array.")
                        getUserData(userId)
                    }
                    .addOnFailureListener { e ->
                        Log.e("UserDataViewModel", "Failed to update user doc: ${e.message}", e)
                    }

            }
            .addOnFailureListener { e ->
                Log.e("UserDataViewModel", "Failed to delete post $postId: ${e.message}", e)
            }
    }
}
