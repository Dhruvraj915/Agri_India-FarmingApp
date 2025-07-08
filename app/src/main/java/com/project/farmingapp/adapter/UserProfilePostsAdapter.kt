package com.project.farmingapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.project.farmingapp.databinding.SingleUserPostItemBinding

class UserProfilePostsAdapter(
    private val context: Context,
    private var posts: List<DocumentSnapshot>
) : RecyclerView.Adapter<UserProfilePostsAdapter.UserPostViewHolder>() {

    inner class UserPostViewHolder(val binding: SingleUserPostItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPostViewHolder {
        val binding = SingleUserPostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserPostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserPostViewHolder, position: Int) {
        val post = posts[position]
        val imageUrl = post.getString("imageUrl")

        // Handle null safety and optional image loading
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(context)
                .load(imageUrl)
                .into(holder.binding.imageUserPost)
        } else {
            // Optionally load a placeholder or hide the image
            holder.binding.imageUserPost.setImageResource(android.R.color.darker_gray)
        }
    }

    override fun getItemCount(): Int = posts.size

    fun updatePosts(newPosts: List<DocumentSnapshot>) {
        posts = newPosts
        notifyDataSetChanged()
    }
}
