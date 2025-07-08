package com.project.farmingapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.project.farmingapp.databinding.SingleEcommItemBinding
import com.project.farmingapp.utilities.CellClickListener

class EcommerceAdapter(
    private val context: Context,
    private val ecommtListData: List<DocumentSnapshot>,
    private val cellClickListener: CellClickListener
) : RecyclerView.Adapter<EcommerceAdapter.EcommerceViewHolder>() {

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseFirestore: FirebaseFirestore

    inner class EcommerceViewHolder(val binding: SingleEcommItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EcommerceViewHolder {
        val binding = SingleEcommItemBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return EcommerceViewHolder(binding)
    }

    override fun getItemCount(): Int = ecommtListData.size

    override fun onBindViewHolder(holder: EcommerceViewHolder, position: Int) {
        val currentList = ecommtListData[position]
        val binding = holder.binding

        binding.ecommtitle.text = currentList.getString("title")
        binding.ecommPrice.text = "\u20B9 " + currentList.get("price").toString()
        binding.ecommretailer.text = currentList.getString("retailer")
        binding.ecommItemAvailability.text = currentList.getString("availability")

        val allImages = currentList.get("imageUrl") as? List<*>
        val imageUrl = allImages?.firstOrNull()?.toString()
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(context).load(imageUrl).into(binding.ecommImage)
        }

        binding.ecommRating.rating = currentList.get("rating").toString().toFloatOrNull() ?: 0f

        binding.root.setOnClickListener {
            cellClickListener.onCellClickListener(currentList.id)
        }
    }
}
