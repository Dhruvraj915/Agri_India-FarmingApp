package com.project.farmingapp.view.ecommerce

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.project.farmingapp.adapter.MyOrdersAdapter
import com.project.farmingapp.databinding.FragmentMyOrdersBinding
import com.project.farmingapp.utilities.CartItemBuy
import com.project.farmingapp.utilities.CellClickListener
import com.project.farmingapp.viewmodel.OrderViewModel

class MyOrdersFragment : Fragment() {

    private var _binding: FragmentMyOrdersBinding? = null
    private val binding get() = _binding!!

    private lateinit var orderViewModel: OrderViewModel
    private lateinit var myOrdersAdapter: MyOrdersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.title = "My Orders"

        orderViewModel = ViewModelProvider(this)[OrderViewModel::class.java]

        setupRecyclerView()

        FirebaseAuth.getInstance().currentUser?.uid?.let { uid ->
            orderViewModel.getOrders(uid).observe(viewLifecycleOwner) { orders: List<DocumentSnapshot> ->
                val mappedData = convertToHashMap(orders)
                myOrdersAdapter.updateOrders(mappedData)
                binding.progressBar.visibility = if (orders.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun setupRecyclerView() {
        myOrdersAdapter = MyOrdersAdapter(
            context = this,
            allData = HashMap(),
            cellClickListener = object : CellClickListener {
                override fun onCellClickListener(data: String) {
                    // Handle click
                }
            },
            cartItemBuy = object : CartItemBuy {
                override fun addToOrders(productId: String, quantity: Int, itemCost: Int, deliveryCost: Int) {
                    // Handle repurchase
                }
            }
        )

        binding.myOrderRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = myOrdersAdapter
        }
    }

    private fun convertToHashMap(orders: List<DocumentSnapshot>): HashMap<String, Object> {
        val result = HashMap<String, Object>()
        for (doc in orders) {
            result[doc.id] = doc.data as Object
        }
        return result
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
