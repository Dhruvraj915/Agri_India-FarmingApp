package com.project.farmingapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.farmingapp.databinding.SingleSliderScreenBinding
import com.project.farmingapp.model.data.IntroData

class IntroAdapter(private val introSlides: List<IntroData>) :
    RecyclerView.Adapter<IntroAdapter.IntroViewHolder>() {

    inner class IntroViewHolder(val binding: SingleSliderScreenBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(introSlider: IntroData) {
            binding.sliderTitle.text = introSlider.title
            binding.sliderDescription.text = introSlider.description
            binding.imageSlider.setImageResource(introSlider.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroViewHolder {
        val binding = SingleSliderScreenBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IntroViewHolder(binding)
    }

    override fun getItemCount(): Int = introSlides.size

    override fun onBindViewHolder(holder: IntroViewHolder, position: Int) {
        holder.bind(introSlides[position])
    }
}
