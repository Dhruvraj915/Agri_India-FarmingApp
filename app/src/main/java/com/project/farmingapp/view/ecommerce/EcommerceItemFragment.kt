// In .../view/ecommerce/EcommerceItemFragment.kt
package com.project.farmingapp.view.ecommerce

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.asura.library.posters.Poster
import com.asura.library.posters.RemoteImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.project.farmingapp.R
import com.project.farmingapp.adapter.AttributesNormalAdapter
import com.project.farmingapp.adapter.AttributesSelectionAdapter
import com.project.farmingapp.databinding.FragmentEcommerceItemBinding // Import ViewBinding
import com.project.farmingapp.model.data.CartItem
import com.project.farmingapp.utilities.CellClickListener
import com.project.farmingapp.viewmodel.EcommViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EcommerceItemFragment : Fragment(), CellClickListener {
    private var _binding: FragmentEcommerceItemBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewmodel: EcommViewModel
    private var selectionAttribute = mutableMapOf<String, Any>()
    private var currentItemId: String? = null
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss", Locale.getDefault())
    private var itemQuantity = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentItemId = arguments?.getString("ecomm_doc_id")
        viewmodel = ViewModelProvider(requireActivity())[EcommViewModel::class.java]
        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEcommerceItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.title = "E-Commerce"
        binding.loadingText.text = "Loading..."
        binding.quantityCountEcomm.text = itemQuantity.toString()

        viewmodel.ecommLiveData.observe(viewLifecycleOwner, Observer { allData ->
            val specificData = allData.find { it.id == currentItemId }
            if (specificData != null) {
                displayProductData(specificData)
            }
        })

        setupClickListeners()
    }

    private fun displayProductData(specificData: DocumentSnapshot) {
        binding.progressEcommItem.visibility = View.GONE
        binding.loadingText.visibility = View.GONE

        binding.productTitle.text = specificData.getString("title")
        binding.productShortDescription.text = specificData.getString("shortDesc")
        binding.productPrice.text = "₹" + specificData.getString("price")
        binding.productLongDesc.text = specificData.getString("longDesc")
        binding.howToUseText.text = specificData.getString("howtouse")
        binding.deliverycost.text = specificData.getString("delCharge")
        binding.Rating.rating = specificData.get("rating").toString().toFloat()

        // Image Slider
        val allImages = specificData.get("imageUrl") as? List<String>
        val posters: ArrayList<Poster> = ArrayList()
        allImages?.forEach { posters.add(RemoteImage(it)) }
        binding.posterSlider.setPosters(posters)

        // Attributes
        val attributes = specificData.get("attributes") as? Map<String, Any>
        if (attributes != null) {
            val allSelectionAttributes = mutableListOf<MutableMap<String, Any>>()
            val allNormalAttributes = mutableListOf<MutableMap<String, Any>>()

            for ((key, value) in attributes) {
                if (value is ArrayList<*> && key.toString() != "Color") {
                    allSelectionAttributes.add(mutableMapOf(key to value))
                }
                if (value is String) {
                    allNormalAttributes.add(mutableMapOf(key to value))
                }
            }

            binding.recyclerSelectionAttributes.adapter = AttributesSelectionAdapter(requireContext(), allSelectionAttributes, this)
            binding.recyclerSelectionAttributes.layoutManager = LinearLayoutManager(requireContext())

            binding.recyclerNormalAttributes.adapter = AttributesNormalAdapter(requireContext(), allNormalAttributes)
            binding.recyclerNormalAttributes.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupClickListeners() {
        binding.increaseQtyBtn.setOnClickListener {
            itemQuantity++
            binding.quantityCountEcomm.text = itemQuantity.toString()
        }

        binding.decreaseQtyBtn.setOnClickListener {
            if (itemQuantity > 1) {
                itemQuantity--
                binding.quantityCountEcomm.text = itemQuantity.toString()
            }
        }

        binding.addToCart.setOnClickListener {
            binding.addToCart.isClickable = false
            binding.progressEcommItem.visibility = View.VISIBLE
            binding.loadingText.text = "Adding to Cart..."
            binding.loadingText.visibility = View.VISIBLE

            val cartItem = CartItem(itemQuantity, sdf.format(Date()))
            firestore.collection("users").document(firebaseAuth.currentUser!!.uid)
                .collection("cart").document(currentItemId!!)
                .set(cartItem)
                .addOnCompleteListener {
                    Toast.makeText(requireContext(), "Item Added", Toast.LENGTH_SHORT).show()
                    binding.progressEcommItem.visibility = View.GONE
                    binding.loadingText.visibility = View.GONE
                    binding.addToCart.isClickable = true
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Please Try Again!", Toast.LENGTH_SHORT).show()
                    binding.progressEcommItem.visibility = View.GONE
                    binding.loadingText.visibility = View.GONE
                    binding.addToCart.isClickable = true
                }
        }

        binding.buynow.setOnClickListener {
            val priceString = binding.productPrice.text.toString().replace("₹", "").trim()
            val deliveryCostString = binding.deliverycost.text.toString().trim()

            Intent(requireActivity(), RazorPayActivity::class.java).also {
                it.putExtra("productId", currentItemId)
                it.putExtra("itemCost", priceString)
                it.putExtra("quantity", itemQuantity.toString())
                it.putExtra("deliveryCost", deliveryCostString)
                startActivity(it)
            }
        }
    }

    override fun onCellClickListener(name: String) {
        val selectionAttributeAllData = name.split(" ")
        Log.d("EcommItem", selectionAttributeAllData[0])
        Log.d("EcommItem", selectionAttributeAllData[1])
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.cart_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.cart_item) {
            val cartFragment = CartFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, cartFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack("cart")
                .commit()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(ecommDocId: String) =
            EcommerceItemFragment().apply {
                arguments = Bundle().apply {
                    putString("ecomm_doc_id", ecommDocId)
                }
            }
    }
}