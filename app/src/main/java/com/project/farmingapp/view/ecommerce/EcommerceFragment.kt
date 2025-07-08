package com.project.farmingapp.view.ecommerce

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels // Import for the 'by viewModels' delegate
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentSnapshot
import com.project.farmingapp.R
import com.project.farmingapp.adapter.EcommerceAdapter
import com.project.farmingapp.databinding.FragmentEcommerceBinding // Import the generated binding class
import com.project.farmingapp.utilities.CellClickListener
import com.project.farmingapp.viewmodel.EcommViewModel

class EcommerceFragment : Fragment(), CellClickListener {

    // 1. Use View Binding instead of Kotlin Synthetics
    private var _binding: FragmentEcommerceBinding? = null
    private val binding get() = _binding!!

    // 2. Use the modern 'by viewModels()' delegate for safe and concise ViewModel initialization
    private val viewmodel: EcommViewModel by viewModels()

    // 3. The adapter should be non-nullable and initialized in onViewCreated
    private lateinit var ecommerceAdapter: EcommerceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using View Binding
        _binding = FragmentEcommerceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "E-Commerce"
        setHasOptionsMenu(true)

        setupRecyclerView()
        setupChipGroupListener()
        observeViewModel()

        // 4. Load initial data once the view is created
        binding.chipgrp.check(R.id.chip1) // This will trigger the listener to load initial data
    }

    private fun setupRecyclerView() {
        // 5. Create the adapter only ONCE
        ecommerceAdapter = EcommerceAdapter(requireContext(), emptyList(), this)
        binding.ecommrcyclr.apply {
            adapter = ecommerceAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        // 6. Set up a SINGLE observer to listen for data changes
        viewmodel.ecommLiveData.observe(viewLifecycleOwner, Observer { items ->
            // When data changes, just update the list in the existing adapter
            ecommerceAdapter.updateData(items) // You need to add this method to your adapter
        })
    }

    private fun setupChipGroupListener() {
        // 7. The listener now ONLY tells the ViewModel to fetch new data.
        // The observer above will handle the UI update automatically.
        binding.chipgrp.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.chip1 -> viewmodel.loadAllEcommItems()
                R.id.chip2 -> viewmodel.getSpecificCategoryItems("fertilizer")
                R.id.chip3 -> viewmodel.getSpecificCategoryItems("pestiside") // Note: Typo in original code "pestiside"
                R.id.chip4 -> viewmodel.getSpecificCategoryItems("machine")
            }
        }
    }

    override fun onCellClickListener(name: String) {
        val ecommerceItemFragment = EcommerceItemFragment()
        val bundle = Bundle()
        bundle.putString("name", name)
        ecommerceItemFragment.arguments = bundle // Use .arguments, not .setArguments

        // 8. Use requireActivity() for null safety
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, ecommerceItemFragment, name)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .setReorderingAllowed(true)
            .addToBackStack("ecommItem")
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.cart_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.cart_item) {
            val cartFragment = CartFragment()
            requireActivity().supportFragmentManager // Use requireActivity()
                .beginTransaction()
                .replace(R.id.frame_layout, cartFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setReorderingAllowed(true)
                .addToBackStack("cart")
                .commit()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 9. Clean up the binding reference to avoid memory leaks
        _binding = null
    }

    // Unused boilerplate from template, can be removed if not needed.
    companion object {
        @JvmStatic
        fun newInstance() = EcommerceFragment()
    }
}

private fun EcommerceAdapter.updateData(snapshots: kotlin.collections.List<com.google.firebase.firestore.DocumentSnapshot>) {}
