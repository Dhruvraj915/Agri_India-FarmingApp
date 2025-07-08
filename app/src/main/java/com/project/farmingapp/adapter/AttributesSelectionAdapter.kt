package com.project.farmingapp.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.project.farmingapp.R
import com.project.farmingapp.databinding.SingleSelectionAttributesEcommBinding
import com.project.farmingapp.utilities.CellClickListener

class AttributesSelectionAdapter(
    private val context: Context,
    private val allData: List<Map<String, Any>>,
    private val cellClickListener: CellClickListener
) : RecyclerView.Adapter<AttributesSelectionAdapter.AttributesSelectionViewHolder>() {

    inner class AttributesSelectionViewHolder(val binding: SingleSelectionAttributesEcommBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttributesSelectionViewHolder {
        val binding = SingleSelectionAttributesEcommBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return AttributesSelectionViewHolder(binding)
    }

    override fun getItemCount(): Int = allData.size

    override fun onBindViewHolder(holder: AttributesSelectionViewHolder, position: Int) {
        val currentData = allData[position]

        for ((key, values) in currentData) {
            cellClickListener.onCellClickListener("1 $key")
            holder.binding.attributeTitle.text = key

            val allValues = values as ArrayList<String>

            var currentValue = allValues[0].split(" ")
            holder.binding.attribute1.text = currentValue[0]
            holder.binding.attribute1Price.text = currentValue[1]

            currentValue = allValues[1].split(" ")
            holder.binding.attribute2.text = currentValue[0]
            holder.binding.attribute2Price.text = currentValue[1]

            currentValue = allValues[2].split(" ")
            holder.binding.attribute3.text = currentValue[0]
            holder.binding.attribute3Price.text = currentValue[1]

            holder.binding.cardSize1.setOnClickListener {
                cellClickListener.onCellClickListener("1 $key")
                Toast.makeText(context, "You Clicked 1", Toast.LENGTH_SHORT).show()
                updateCardSelection(holder, 1)
            }

            holder.binding.cardSize2.setOnClickListener {
                cellClickListener.onCellClickListener("2 $key")
                Toast.makeText(context, "You Clicked 2", Toast.LENGTH_SHORT).show()
                updateCardSelection(holder, 2)
            }

            holder.binding.cardSize3.setOnClickListener {
                cellClickListener.onCellClickListener("3 $key")
                Toast.makeText(context, "You Clicked 3", Toast.LENGTH_SHORT).show()
                updateCardSelection(holder, 3)
            }
        }
    }

    private fun updateCardSelection(holder: AttributesSelectionViewHolder, selected: Int) {
        val white = Color.parseColor("#FFFFFF")
        val gray = Color.parseColor("#FF404A3A")

        @RequiresApi(Build.VERSION_CODES.M)
        fun styleCard(card: androidx.cardview.widget.CardView, attr: android.widget.TextView, price: android.widget.TextView, isSelected: Boolean) {
            card.backgroundTintList = context.getColorStateList(if (isSelected) R.color.colorPrimary else R.color.secondary)
            val color = if (isSelected) white else gray
            val typeface = if (isSelected) Typeface.BOLD else Typeface.NORMAL
            attr.setTextColor(color)
            price.setTextColor(color)
            attr.setTypeface(null, typeface)
            price.setTypeface(null, typeface)
        }

        styleCard(holder.binding.cardSize1, holder.binding.attribute1, holder.binding.attribute1Price, selected == 1)
        styleCard(holder.binding.cardSize2, holder.binding.attribute2, holder.binding.attribute2Price, selected == 2)
        styleCard(holder.binding.cardSize3, holder.binding.attribute3, holder.binding.attribute3Price, selected == 3)
    }
}
