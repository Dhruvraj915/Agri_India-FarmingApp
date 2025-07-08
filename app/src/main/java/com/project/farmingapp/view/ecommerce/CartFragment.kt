package com.project.farmingapp.view.ecommerce

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
// CORRECTED IMPORT: Adjust the package '...view' to the actual location of your RazorPayActivity
import com.project.farmingapp.adapter.CartItemsAdapter
import com.project.farmingapp.databinding.FragmentCartBinding
import com.project.farmingapp.utilities.CartItemBuy
import com.project.farmingapp.viewmodel.EcommViewModel

class CartFragment : Fragment(), CartItemBuy {

    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private var isOpened: Boolean = false
    private var totalCount = 0
    private var totalPrice = 0
    private var items = HashMap<String, Any>() // Changed to HashMap<String, Any> for better type safety
    private lateinit var ecommViewModel: EcommViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        ecommViewModel = EcommViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firebaseDatabase = FirebaseDatabase.getInstance()
        val firebaseAuth = FirebaseAuth.getInstance()
        val cartRef = firebaseDatabase.getReference("${firebaseAuth.currentUser!!.uid}").child("cart")

        (activity as AppCompatActivity).supportActionBar?.title = "Cart"
        isOpened = true

        val postListener = object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error loading cart", Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    items = dataSnapshot.value as HashMap<String, Any>
                    var totalCartPrice = 0

                    for ((key, value) in items) {
                        val currVal = value as Map<String, Any>
                        ecommViewModel.getSpecificItem(key).observe(viewLifecycleOwner, Observer {
                            totalCartPrice += currVal["quantity"].toString().toInt() *
                                    it["price"].toString().toInt() +
                                    it["delCharge"].toString().toInt()

                            binding.totalItemsValue.text = items.size.toString()
                            binding.totalCostValue.text = "\u20B9$totalCartPrice"
                        })
                    }

                    if (isOpened) {
                        binding.totalItemsValue.text = items.size.toString()
                        binding.totalCostValue.text = "\u20B9$totalCartPrice"
                    }

                    val adapter = CartItemsAdapter(this@CartFragment, items, this@CartFragment)
                    binding.recyclerCart.adapter = adapter
                    binding.recyclerCart.layoutManager = LinearLayoutManager(requireContext())
                    binding.progressCart.visibility = View.GONE
                    binding.loadingTitleText.visibility = View.GONE
                } else {
                    Toast.makeText(requireContext(), "Cart is empty", Toast.LENGTH_SHORT).show()
                    binding.progressCart.visibility = View.GONE
                    binding.loadingTitleText.visibility = View.GONE
                }
            }
        }

        cartRef.addValueEventListener(postListener)

        binding.buyAllBtn.setOnClickListener {
            // Implement RazorPay or PrePayment navigation if needed
            // Example:
            // val intent = Intent(requireContext(), RazorPayActivity::class.java)
            // intent.putExtra("tp", totalPrice)
            // startActivity(intent)
        }
    }

    override fun addToOrders(productId: String, quantity: Int, itemCost: Int, deliveryCost: Int) {
        val intent = Intent(requireContext(), RazorPayActivity::class.java)
        intent.putExtra("productId", productId)
        intent.putExtra("itemCost", itemCost.toString())
        intent.putExtra("quantity", quantity.toString())
        intent.putExtra("deliveryCost", deliveryCost.toString())
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        // You should define ARG_PARAM1 and ARG_PARAM2 or remove them if not used
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CartFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}