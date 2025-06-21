package com.project.farmingapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.farmingapp.databinding.SingleNormalAttributesEcommBinding

class AttributesNormalAdapter(
    val context: Context,
    private val allData: List<Map<String, Any>>
) : RecyclerView.Adapter<AttributesNormalAdapter.AttributesNormalViewHolder>() {

    inner class AttributesNormalViewHolder(val binding: SingleNormalAttributesEcommBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttributesNormalViewHolder {
        val binding = SingleNormalAttributesEcommBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        return AttributesNormalViewHolder(binding)
    }

    override fun getItemCount(): Int = allData.size

    override fun onBindViewHolder(holder: AttributesNormalViewHolder, position: Int) {
        val currentData = allData[position]
        for ((key, value) in currentData) {
            holder.binding.normalAttributeTitle.text = "$key - "
            holder.binding.normalAttributeValue.text = value.toString()
        }
    }
}
