package com.project.farmingapp.adapter

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.project.farmingapp.databinding.UserProfilePostsSingleBinding
import com.project.farmingapp.utilities.CellClickListener

class PostListUserProfileAdapter(
    val context: Context,
    var listData: ArrayList<DocumentSnapshot>,
    private val cellClickListener: CellClickListener
) : RecyclerView.Adapter<PostListUserProfileAdapter.PostListUserProfileViewHolder>() {

    inner class PostListUserProfileViewHolder(val binding: UserProfilePostsSingleBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostListUserProfileViewHolder {
        val binding = UserProfilePostsSingleBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return PostListUserProfileViewHolder(binding)
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: PostListUserProfileViewHolder, position: Int) {
        val currentData = listData[position]
        val binding = holder.binding

        binding.userPostTitleUserProfileFrag.text = currentData["title"].toString()

        val timestamp = currentData["timeStamp"] as? Long
        if (timestamp != null) {
            binding.userPostUploadTimeUserProfileFrag.text =
                DateUtils.getRelativeTimeSpanString(timestamp)
        }

        binding.userPostProfileCard.setOnClickListener {
            cellClickListener.onCellClickListener(currentData.id)
        }

        val imageUrl = currentData.getString("imageUrl")
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(context)
                .load(imageUrl)
                .into(binding.userPostImageUserProfileFrag)
        }
    }
}
