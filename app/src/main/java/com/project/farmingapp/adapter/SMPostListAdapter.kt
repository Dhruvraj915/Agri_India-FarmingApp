package com.project.farmingapp.adapter

import android.content.Context
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.project.farmingapp.databinding.PostWithImageSmBinding

class SMPostListAdapter(
    val context: Context,
    val postListData: List<DocumentSnapshot>
) : RecyclerView.Adapter<SMPostListAdapter.SMPostListViewHolder>() {

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseFirestore: FirebaseFirestore

    inner class SMPostListViewHolder(val binding: PostWithImageSmBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SMPostListViewHolder {
        val binding = PostWithImageSmBinding.inflate(LayoutInflater.from(context), parent, false)
        return SMPostListViewHolder(binding)
    }

    override fun getItemCount(): Int = postListData.size

    override fun onBindViewHolder(holder: SMPostListViewHolder, position: Int) {
        val currentPost = postListData[position]
        val binding = holder.binding

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        binding.userNamePostSM.text = currentPost["name"].toString()
        binding.userPostTitleValue.text = currentPost["title"].toString()
        binding.userPostDescValue.text = currentPost["description"].toString()

        val timeStamp = currentPost["timeStamp"] as? Long
        if (timeStamp != null) {
            binding.userPostUploadTime.text = DateUtils.getRelativeTimeSpanString(timeStamp)
        }

        val uploadType = currentPost["uploadType"].toString()
        val imageUrl = currentPost["imageUrl"].toString()

        when (uploadType) {
            "video" -> {
                val webSettings: WebSettings = binding.postVideoSM.settings
                webSettings.javaScriptEnabled = true
                webSettings.loadWithOverviewMode = true
                webSettings.useWideViewPort = true

                binding.postVideoSM.webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
                        // Optional: autoplay via JS
                    }
                }
                binding.postVideoSM.loadUrl(imageUrl)

                binding.postImageSM.visibility = android.view.View.GONE
                binding.postVideoSM.visibility = android.view.View.VISIBLE
            }

            "image" -> {
                Glide.with(context).load(imageUrl).into(binding.postImageSM)
                binding.postVideoSM.visibility = android.view.View.GONE
                binding.postImageSM.visibility = android.view.View.VISIBLE
            }

            else -> {
                binding.postImageSM.visibility = android.view.View.GONE
                binding.postVideoSM.visibility = android.view.View.GONE
            }
        }

        binding.userPostDescValue.setOnClickListener {
            binding.userPostDescValue.maxLines = Int.MAX_VALUE
        }

        // Animations
        val fadeAnim = AnimationUtils.loadAnimation(context, com.project.farmingapp.R.anim.fade_transition)
        binding.userProfileImageCard.startAnimation(fadeAnim)
        binding.postContainer.startAnimation(fadeAnim)

        // Load profile image
        firebaseFirestore.collection("users")
            .document(currentPost["userID"].toString())
            .get()
            .addOnSuccessListener {
                val profileImgUrl = it.getString("profileImage")
                if (!profileImgUrl.isNullOrEmpty()) {
                    Glide.with(context).load(profileImgUrl).into(binding.userProfileImagePost)
                }
            }
    }
}
