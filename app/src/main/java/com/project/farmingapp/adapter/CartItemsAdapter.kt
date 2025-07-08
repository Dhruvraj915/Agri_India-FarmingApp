package com.project.farmingapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.project.farmingapp.databinding.SingleCartItemBinding
import com.project.farmingapp.utilities.CartItemBuy
import com.project.farmingapp.view.ecommerce.CartFragment
import com.project.farmingapp.viewmodel.EcommViewModel

class CartItemsAdapter(
    val context: CartFragment,
    val allData: HashMap<String, Any>,
    val cartitembuy: CartItemBuy
) : RecyclerView.Adapter<CartItemsAdapter.CartItemsViewHolder>() {

    var itemCost = 0
    var deliveryCharge = 0
    var quantity = 0

    inner class CartItemsViewHolder(val binding: SingleCartItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemsViewHolder {
        val binding = SingleCartItemBinding.inflate(
            LayoutInflater.from(context.context),
            parent,
            false
        )
        return CartItemsViewHolder(binding)
    }

    override fun getItemCount(): Int = allData.size

    override fun onBindViewHolder(holder: CartItemsViewHolder, position: Int) {
        val currentData = allData.entries.toTypedArray()[position]
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val firebaseAuth = FirebaseAuth.getInstance()

        val itemQtyRef = firebaseDatabase.getReference(firebaseAuth.currentUser!!.uid)
            .child("cart")
            .child(currentData.key)
            .child("quantity")

        val itemRef = firebaseDatabase.getReference(firebaseAuth.currentUser!!.uid)
            .child("cart")
            .child(currentData.key)

        val binding = holder.binding

        binding.cartItemBuyBtn.setOnClickListener {
            val qty = binding.quantityCountEcomm.text.toString().toInt()
            val itemPrice = binding.itemPriceCart.text.toString().split("₹")
            val deliveryCharge = binding.deliveryChargeCart.text.toString().toInt()
            Log.d("totalPrice", quantity.toString())
            Log.d("totalPrice", itemCost.toString())
            Log.d("totalPrice", deliveryCharge.toString())
            cartitembuy.addToOrders(currentData.key, qty, itemPrice[1].toInt(), deliveryCharge)
        }

        binding.removeCartBtn.setOnClickListener {
            itemRef.removeValue()
        }

        binding.increaseQtyBtn.setOnClickListener {
            val newQty = binding.quantityCountEcomm.text.toString().toInt() + 1
            binding.quantityCountEcomm.text = newQty.toString()
            itemQtyRef.setValue(newQty)
        }

        binding.decreaseQtyBtn.setOnClickListener {
            val currentQty = binding.quantityCountEcomm.text.toString().toInt()
            if (currentQty > 1) {
                val newQty = currentQty - 1
                binding.quantityCountEcomm.text = newQty.toString()
                itemQtyRef.setValue(newQty)
            }
        }

        val curr = currentData.value as Map<String, Object>
        val ecommViewModel = EcommViewModel()

        ecommViewModel.getSpecificItem(currentData.key).observe(context, Observer {
            itemCost = it.get("price").toString().toInt()
            deliveryCharge = it.get("delCharge").toString().toInt()
            quantity = curr["quantity"].toString().toInt()

            binding.itemNameCart.text = it.getString("title")
            binding.itemPriceCart.text = "₹$itemCost"
            binding.quantityCountEcomm.text = quantity.toString()
            binding.deliveryChargeCart.text = deliveryCharge.toString()
            binding.cartItemFirm.text = it.get("retailer").toString()
            binding.cartItemAvailability.text = it.get("availability").toString()

            val allImages = it.get("imageUrl") as ArrayList<String>
            Glide.with(context).load(allImages[0]).into(binding.cartItemImage)

            val total = itemCost * quantity + deliveryCharge
            binding.cartItemBuyBtn.text = "Buy Now: ₹$total"
        })
    }
}
