/*
package com.project.farmingapp.view.ecommerce

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.asura.library.posters.Poster
import com.asura.library.posters.RawVideo
import com.asura.library.posters.RemoteImage
import com.asura.library.posters.RemoteVideo
import com.asura.library.views.PosterSlider
import com.google.common.base.MoreObjects
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.project.farmingapp.R
import com.project.farmingapp.adapter.AttributesNormalAdapter
import com.project.farmingapp.adapter.AttributesSelectionAdapter
import com.project.farmingapp.model.data.CartItem
import com.project.farmingapp.utilities.CellClickListener
import com.project.farmingapp.viewmodel.EcommViewModel
import kotlinx.android.synthetic.main.fragment_ecommerce_item.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

*/
/**
 * A simple [Fragment] subclass.
 * Use the [EcommItemFragment.newInstance] factory method to
 * create an instance of this fragment.
 *//*

class EcommerceItemFragment : Fragment(), CellClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private lateinit var viewmodel: EcommViewModel
    private var param2: String? = null
    private var selectionAttribute = mutableMapOf<String, Any>()
    private var currentItemId: Any?= null
    lateinit var realtimeDatabase: FirebaseDatabase
    lateinit var firebaseAuth: FirebaseAuth
    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        viewmodel = ViewModelProviders.of(requireActivity())
            .get<EcommViewModel>(EcommViewModel::class.java)
        Toast.makeText(requireActivity().applicationContext, "Something" + tag, Toast.LENGTH_SHORT).show()

        realtimeDatabase = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ecommerce_item, container, false)
    }

    companion object {
        */
/**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EcommItemFragment.
         *//*

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EcommerceItemFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        (activity as AppCompatActivity).supportActionBar?.title = "E-Commerce"
        loadingText.text = "Loading..."


        val color1Params = color1.layoutParams
        val color2Params = color2.layoutParams
        val color3Params = color3.layoutParams
        val color4Params = color4.layoutParams

        val density = resources.displayMetrics.density
        color1Params.width = (density * 40).toInt()
        color1Params.height = (density * 35).toInt()
        color1.layoutParams = color1Params

        color1.setOnClickListener {
            color1Params.width = (density * 40).toInt()
            color1Params.height = (density * 35).toInt()
            color1.layoutParams = color1Params

            color3Params.width = (density * 30).toInt()
            color3Params.height = (density * 25).toInt()
            color3.layoutParams = color3Params

            color4Params.width = (density * 30).toInt()
            color4Params.height = (density * 25).toInt()
            color4.layoutParams = color4Params

            color2Params.width = (density * 30).toInt()
            color2Params.height = (density * 25).toInt()
            color2.layoutParams = color2Params
        }

        color2.setOnClickListener {
            color1Params.width = (density * 30).toInt()
            color1Params.height = (density * 25).toInt()
            color1.layoutParams = color1Params

            color3Params.width = (density * 30).toInt()
            color3Params.height = (density * 25).toInt()
            color3.layoutParams = color3Params

            color4Params.width = (density * 30).toInt()
            color4Params.height = (density * 25).toInt()
            color4.layoutParams = color4Params

            color2Params.width = (density * 40).toInt()
            color2Params.height = (density * 35).toInt()
            color2.layoutParams = color2Params

        }

        increaseQtyBtn.setOnClickListener {
            quantityCountEcomm.text = (quantityCountEcomm.text.toString().toInt() + 1).toString()
        }

        decreaseQtyBtn.setOnClickListener {
            if(quantityCountEcomm.text.toString().toInt() != 1){
                quantityCountEcomm.text = (quantityCountEcomm.text.toString().toInt() - 1).toString()
            }
        }

        var posters: ArrayList<Poster> = ArrayList()


        val allData = viewmodel.ecommLiveData.value
        val allDataLength = allData!!.size

        for (a in 0 until allDataLength){
            if(allData[a].id == this.tag){

                val specificData = allData[a]

                currentItemId = specificData.id!!

                productTitle.text = specificData.getString("title")
                productShortDescription.text = specificData.getString("shortDesc")
                productPrice.text =  "₹"  + specificData.getString("price")
                productLongDesc.text = specificData.getString("longDesc")
                howToUseText.text = specificData.getString("howtouse")
                deliverycost.text = specificData.getString("delCharge")
                Rating.rating = specificData.get("rating").toString().toFloat()
                var attributes = specificData.get("attributes") as Map<String, Any>


                if(attributes.contains("Color")){
                    colorLinear.visibility = View.VISIBLE
                    colorTitle.visibility = View.VISIBLE

                } else{
                    colorLinear.visibility = View.GONE
                    colorTitle.visibility = View.GONE
                }

                var allSelectionAttributes = mutableListOf<MutableMap<String, Any>>()
                var allNormalAttributes = mutableListOf<MutableMap<String, Any>>()
                for((key, value) in attributes){
                    var selectionMap = mutableMapOf<String, Any>()
                    var normalMap = mutableMapOf<String, Any>()

                    if(value is ArrayList<*> && key.toString()!="Color"){
                        selectionMap.put(key, value)
                        allSelectionAttributes.add(selectionMap)
                    }

                    if(value is String){
                        normalMap.put(key, value)
                        allNormalAttributes.add(normalMap)
                    }

                }

                val adapter = AttributesSelectionAdapter(requireActivity().applicationContext, allSelectionAttributes, this)
                recyclerSelectionAttributes.adapter = adapter
                recyclerSelectionAttributes.layoutManager = LinearLayoutManager(requireActivity().applicationContext)

                val adapter2 = AttributesNormalAdapter(requireActivity().applicationContext, allNormalAttributes)
                recyclerNormalAttributes.adapter = adapter2
                recyclerNormalAttributes.layoutManager = LinearLayoutManager(requireActivity().applicationContext)

                progress_ecommItem.visibility = View.GONE
                loadingText.visibility = View.GONE


                val allImages = specificData.get("imageUrl") as List<String>
                for (a in allImages){
                    posters.add(RemoteImage("${a}"))
                }
                poster_slider.setPosters(posters)
            }
            else{

            }
        }

        addToCart.setOnClickListener {
            addToCart.isClickable = false
            progress_ecommItem.visibility = View.VISIBLE
            loadingText.text = "Adding to Cart..."
            loadingText.visibility = View.GONE
            val realtimeRef = realtimeDatabase.getReference("${firebaseAuth.currentUser!!.uid}").child("cart").child("${currentItemId}")

//            selectionAttribute!!.put("quantity", quantityCountEcomm.text.toString().toInt())
//            selectionAttribute.put("basePrice", productPrice.text.toString().toInt())
//            selectionAttribute.put("delCharge", deliverycost.text.toString().toInt())

            val currentDateTime = sdf.format(Date())
            realtimeRef.setValue(CartItem(quantityCountEcomm.text.toString().toInt(), currentDateTime.toString()))
                .addOnCompleteListener {
                    Toast.makeText(requireActivity().applicationContext, "Item Added", Toast.LENGTH_SHORT).show()
                    progress_ecommItem.visibility = View.GONE
                    loadingText.visibility = View.GONE
                    addToCart.isClickable = true

                }.addOnFailureListener {
                    Toast.makeText(requireActivity().applicationContext, "Please Try Again!", Toast.LENGTH_SHORT).show()
                    progress_ecommItem.visibility = View.GONE
                    loadingText.visibility = View.GONE
                    addToCart.isClickable = true
                }

        }

        buynow.setOnClickListener {
//            var product_id = ArrayList<String>()
//            var item_cost=ArrayList<Int>()
//            var item_qty=ArrayList<Int>()
            val productPrice = productPrice.text.toString().split("₹") as ArrayList<String>


//            var totalPrice = quantityCountEcomm.text.toString().toInt()*productPrice[1].toString().toInt() + deliverycost.text.toString().toInt()

//            product_id.add(currentItemId as String)
//            item_cost.add(totalPrice)
//            item_qty.add(quantityCountEcomm.text.toString().toInt())

            Intent(requireActivity().applicationContext, RazorPayActivity::class.java).also {
                it.putExtra("productId",currentItemId.toString())
                it.putExtra("itemCost",productPrice[1].toString())
                it.putExtra("quantity", quantityCountEcomm.text.toString())
                it.putExtra("deliveryCost", deliverycost.text.toString())
                startActivity(it)
            }

        }
    }

    override fun onCellClickListener(name: String) {
        val selectionAttributeAllData = name.split(" ") as List<Any>

        Log.d("EcommItem", selectionAttributeAllData[0].toString())
        Log.d("EcommItem", selectionAttributeAllData[1].toString())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.cart_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId){
            R.id.cart_item -> {
                val cartFragment = CartFragment()
                val transaction = requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_layout, cartFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .setReorderingAllowed(true)
                    .addToBackStack("cart")
                    .commit()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}*/
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