package com.project.farmingapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class UserProfilePostsViewModel : ViewModel() {

    val userPostsLiveData = MutableLiveData<List<DocumentSnapshot>>() // ✅ this fixes unresolved reference
    val userProfilePostsLiveData = MutableLiveData<ArrayList<HashMap<String, Any>>>()
    val userProfilePostsLiveData2 = MutableLiveData<List<String>?>()

    val liveData1 = MutableLiveData<List<String>>()
    val liveData2 = MutableLiveData<ArrayList<DocumentSnapshot>>()
    val liveData3 = MutableLiveData<ArrayList<DocumentSnapshot>>()

    fun getUserPosts(userID: String?) {
        val firebaseFirestore = FirebaseFirestore.getInstance()
        val firebaseFirestore2 = FirebaseFirestore.getInstance()

        firebaseFirestore.collection("users").document(userID ?: return)
            .get()
            .addOnSuccessListener {
                val allPostsIDs: List<String>? = it.get("posts") as? List<String>
                userProfilePostsLiveData2.value = allPostsIDs
                Log.d("User All Posts 3", userProfilePostsLiveData2.value.toString())

                val postList = ArrayList<DocumentSnapshot>()
                if (allPostsIDs != null) {
                    for (postId in allPostsIDs) {
                        firebaseFirestore2.collection("posts").document(postId)
                            .get()
                            .addOnSuccessListener { doc ->
                                postList.add(doc)
                                userPostsLiveData.value = postList
                            }
                            .addOnFailureListener {
                                Log.d("User All Posts", "Failed to fetch post $postId")
                            }
                    }
                }
            }
            .addOnFailureListener {
                Log.d("User All Posts", "Failed to fetch user document")
            }
    }

    fun getUserPostsIDs(userID: String?) {
        val firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseFirestore.collection("users").document(userID ?: return)
            .get()
            .addOnSuccessListener {
                liveData1.value = it.get("posts") as? List<String>
            }
    }

    fun getAllPostsOfUser(listOfIDs: List<String>) {
        val firebaseFirestore = FirebaseFirestore.getInstance()
        val someList = ArrayList<DocumentSnapshot>()
        var completed = 0
        for (id in listOfIDs) {
            firebaseFirestore.collection("posts").document(id)
                .get()
                .addOnSuccessListener {
                    someList.add(it)
                    completed++
                    if (completed == listOfIDs.size) {
                        liveData2.value = someList
                        Log.d("LiveData2", "Fetched all user posts.")
                    }
                }
        }
    }

    fun getAllPosts(userId: String?) {
        val firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseFirestore.collection("posts").whereEqualTo("userID", userId)
            .get()
            .addOnSuccessListener {
                liveData3.value = ArrayList(it.documents)
                Log.d("UserProfilePostsViewModel", "Fetched all posts of user.")
            }
            .addOnFailureListener {
                Log.d("UserProfilePostsViewModel", "Error fetching posts.")
            }
    }
}
