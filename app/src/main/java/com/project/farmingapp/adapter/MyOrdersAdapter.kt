package com.project.farmingapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.farmingapp.databinding.SingleMyorderItemBinding
import com.project.farmingapp.utilities.CartItemBuy
import com.project.farmingapp.utilities.CellClickListener
import com.project.farmingapp.view.ecommerce.MyOrdersFragment
import com.project.farmingapp.viewmodel.EcommViewModel

class MyOrdersAdapter(
    val context: MyOrdersFragment,
    var allData: HashMap<String, Object>,
    private val cellClickListener: CellClickListener,
    private val cartItemBuy: CartItemBuy
) : RecyclerView.Adapter<MyOrdersAdapter.MyOrdersViewHolder>() {

    inner class MyOrdersViewHolder(val binding: SingleMyorderItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrdersViewHolder {
        val binding = SingleMyorderItemBinding.inflate(
            LayoutInflater.from(context.requireContext()),
            parent,
            false
        )
        return MyOrdersViewHolder(binding)
    }

    override fun getItemCount(): Int = allData.size

    override fun onBindViewHolder(holder: MyOrdersViewHolder, position: Int) {
        val myOrder = allData.entries.toTypedArray()[position].value as HashMap<String, Object>
        val viewModel = EcommViewModel()

        viewModel.getSpecificItem(myOrder["productId"].toString())
            .observe(context.viewLifecycleOwner) {
                val binding = holder.binding

                binding.myOrderPinCode.text = "Pin Code: ${myOrder["pincode"]}"
                binding.myOrderItemName.text = it?.getString("title") ?: ""
                binding.myOrderItemPrice.text = "\u20B9" + (
                        myOrder["quantity"].toString().toInt() *
                                myOrder["itemCost"].toString().toInt() +
                                myOrder["deliveryCost"].toString().toInt()
                        ).toString()
                binding.myOderDeliveryCharge.text = myOrder["deliveryCost"].toString()
                binding.myOrderDeliveryStatus.text = myOrder["deliveryStatus"].toString()

                val allImages = it?.get("imageUrl") as? List<*>
                val imageUrl = allImages?.getOrNull(0)?.toString()
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(context).load(imageUrl).into(binding.myOderItemImage)
                }

                val date = myOrder["time"].toString().split(" ")
                binding.myOrderTimeStamp.text = date[0]
            }

        holder.binding.myOrderPurchaseAgain.setOnClickListener {
            cartItemBuy.addToOrders(
                myOrder["productId"].toString(),
                myOrder["quantity"].toString().toInt(),
                myOrder["itemCost"].toString().toInt(),
                myOrder["deliveryCost"].toString().toInt()
            )
        }

        holder.binding.root.setOnClickListener {
            cellClickListener.onCellClickListener(myOrder["productId"].toString())
        }
    }

    fun updateOrders(newData: HashMap<String, Object>) {
        allData.clear()
        allData.putAll(newData)
        notifyDataSetChanged()
    }
}
