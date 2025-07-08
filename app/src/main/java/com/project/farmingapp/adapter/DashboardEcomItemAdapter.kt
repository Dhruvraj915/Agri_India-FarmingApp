package com.project.farmingapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.farmingapp.databinding.SingleDashboardEcommItemBinding
import com.project.farmingapp.utilities.CellClickListener
import com.project.farmingapp.view.Home.DashboardItem

class DashboardEcomItemAdapter(
    private val context: Context,
    private val items: List<DashboardItem>,
    private val cellClickListener: CellClickListener
) : RecyclerView.Adapter<DashboardEcomItemAdapter.DashboardEcomItemViewHolder>() {

    inner class DashboardEcomItemViewHolder(val binding: SingleDashboardEcommItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardEcomItemViewHolder {
        val binding = SingleDashboardEcommItemBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return DashboardEcomItemViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: DashboardEcomItemViewHolder, position: Int) {
        val item = items[position]
        val binding = holder.binding

        binding.itemTitle.text = item.title
        binding.itemPrice.text = "₹${String.format("%.2f", item.price)}"
        if (item.imageUrl.isNotEmpty()) {
            Glide.with(context).load(item.imageUrl).into(binding.itemImage)
        } else {
            binding.itemImage.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        binding.root.setOnClickListener {
            cellClickListener.onCellClickListener(item.title) // Pass title instead of position
        }
    }
}