/*
package com.project.farmingapp.view.articles

import android.R.id.title
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.RotateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.project.farmingapp.databinding.FragmentFruitsBinding
import com.project.farmingapp.utilities.hide
import com.project.farmingapp.utilities.show
import com.project.farmingapp.viewmodel.ArticleListener
import com.project.farmingapp.viewmodel.ArticleViewModel

class FruitsFragment : Fragment(), ArticleListener {

    private var _binding: FragmentFruitsBinding? = null
    private val binding get() = _binding!!

    private var selectedName: String? = null
    private lateinit var viewModel: ArticleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedName = arguments?.getString("name")
        viewModel = ViewModelProvider(requireActivity())[ArticleViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFruitsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.title = "Articles"

        binding.descToggleBtnFruitFragArt.setOnClickListener {
            val isExpanded = binding.descTextValueFruitFragArt.maxLines == Int.MAX_VALUE
            binding.descTextValueFruitFragArt.maxLines = if (isExpanded) 3 else Int.MAX_VALUE

            val rotateAnim = RotateAnimation(
                if (isExpanded) 180f else 0f,
                if (isExpanded) 0f else 180f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f
            ).apply {
                duration = 200
                fillAfter = true
            }
            binding.descToggleBtnFruitFragArt.startAnimation(rotateAnim)
        }

        val articles = viewModel.message3.value ?: return

        for (data in articles) {
            val dataMap = data.data
            if (dataMap != null) {
                val attributes = dataMap["attributes"] as? Map<*, *>
                val diseases = dataMap["diseases"] as? List<*>
                val images = dataMap["images"] as? List<*>

                binding.titleTextFruitFragArt.text = title
                binding.descTextValueFruitFragArt.text = dataMap["description"]?.toString() ?: "-"
                binding.processTextValueFruitFragArt.text = dataMap["process"]?.toString() ?: "-"
                binding.soilTextValueFruitFragArt.text = dataMap["soil"]?.toString() ?: "-"
                binding.stateTextValueFruitFragArt.text = dataMap["state"]?.toString() ?: "-"

                binding.tempTextFruitFragArt.text = attributes?.get("Temperature")?.toString() ?: "-"
                binding.monthTextFruitFragArt.text = attributes?.get("Time")?.toString() ?: "-"
                binding.attr1ValueFruitFragArt.text = attributes?.get("Weight")?.toString() ?: "-"
                binding.attr2ValueFruitFragArt.text = attributes?.get("Vitamins")?.toString() ?: "-"
                binding.attr3ValueFruitFragArt.text = attributes?.get("Tree Height")?.toString() ?: "-"
                binding.attr4ValueFruitFragArt.text = attributes?.get("growthTime")?.toString() ?: "-"

                val diseaseText = diseases?.filterIsInstance<String>()?.withIndex()?.joinToString("\n") {
                    "${it.index + 1}. ${it.value}"
                } ?: "-"
                binding.diseaseTextValueFruitFragArt.text = diseaseText

                val imageUrl = images?.firstOrNull()?.toString()
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(this).load(imageUrl).into(binding.imageFruitFragArt)
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStarted() {}
    override fun onSuccess(authRepo: LiveData<String>) {
        authRepo.observe(viewLifecycleOwner) {
            Log.d("Fruit", "Success")
        }
    }

    override fun onFailure(message: String) {}

    companion object {
        @JvmStatic
        fun newInstance(name: String) = FruitsFragment().apply {
            arguments = Bundle().apply {
                putString("name", name)
            }
        }
    }
}
*/
// In .../view/articles/FruitsFragment.kt

package com.project.farmingapp.view.articles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.RotateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.project.farmingapp.databinding.FragmentFruitsBinding
import com.project.farmingapp.viewmodel.ArticleViewModel

class FruitsFragment : Fragment() {

    private var _binding: FragmentFruitsBinding? = null
    private val binding get() = _binding!!

    private var selectedName: String? = null
    private lateinit var viewModel: ArticleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get the name of the fruit to display from fragment arguments
        arguments?.let {
            selectedName = it.getString("name")
        }
        viewModel = ViewModelProvider(requireActivity())[ArticleViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFruitsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.title = selectedName ?: "Article"

        // Observe the list of articles from the ViewModel
        viewModel.message3.observe(viewLifecycleOwner) { articles ->
            // Find the specific article that matches the selectedName
            val article = articles.find { it.data?.get("title") == selectedName }

            if (article != null) {
                val dataMap = article.data
                if (dataMap != null) {
                    val attributes = dataMap["attributes"] as? Map<*, *>
                    val diseases = dataMap["diseases"] as? List<*>
                    val images = dataMap["images"] as? List<*>

                    // Use the data to populate the views via binding
                    binding.titleTextFruitFragArt.text = dataMap["title"]?.toString() ?: "N/A"
                    binding.descTextValueFruitFragArt.text = dataMap["description"]?.toString() ?: "N/A"
                    binding.processTextValueFruitFragArt.text = dataMap["process"]?.toString() ?: "N/A"
                    binding.soilTextValueFruitFragArt.text = dataMap["soil"]?.toString() ?: "N/A"
                    binding.stateTextValueFruitFragArt.text = dataMap["state"]?.toString() ?: "N/A"

                    binding.tempTextFruitFragArt.text = attributes?.get("Temperature")?.toString() ?: "N/A"
                    binding.monthTextFruitFragArt.text = attributes?.get("Time")?.toString() ?: "N/A"
                    binding.attr1ValueFruitFragArt.text = attributes?.get("Weight")?.toString() ?: "N/A"
                    binding.attr2ValueFruitFragArt.text = attributes?.get("Vitamins")?.toString() ?: "N/A"
                    binding.attr3ValueFruitFragArt.text = attributes?.get("Tree Height")?.toString() ?: "N/A"
                    binding.attr4ValueFruitFragArt.text = attributes?.get("growthTime")?.toString() ?: "N/A"

                    val diseaseText = diseases?.filterIsInstance<String>()?.withIndex()?.joinToString("\n") {
                        "${it.index + 1}. ${it.value}"
                    } ?: "N/A"
                    binding.diseaseTextValueFruitFragArt.text = diseaseText

                    val imageUrl = images?.firstOrNull()?.toString()
                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(this).load(imageUrl).into(binding.imageFruitFragArt)
                    }
                }
            }
        }

        binding.descToggleBtnFruitFragArt.setOnClickListener {
            val isExpanded = binding.descTextValueFruitFragArt.maxLines == Int.MAX_VALUE
            binding.descTextValueFruitFragArt.maxLines = if (isExpanded) 3 else Int.MAX_VALUE

            val rotateAnim = RotateAnimation(
                if (isExpanded) 180f else 0f,
                if (isExpanded) 0f else 180f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f
            ).apply {
                duration = 200
                fillAfter = true
            }
            binding.descToggleBtnFruitFragArt.startAnimation(rotateAnim)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(name: String) = FruitsFragment().apply {
            arguments = Bundle().apply {
                putString("name", name)
            }
        }
    }
}