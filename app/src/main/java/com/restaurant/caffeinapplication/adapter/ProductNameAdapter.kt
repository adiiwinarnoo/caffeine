package com.restaurant.caffeinapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.restaurant.caffeinapplication.R
import com.restaurant.caffeinapplication.data.model.ResultItemNewOrder

class ProductNameAdapter(private val models : ResultItemNewOrder) :
    RecyclerView.Adapter<ProductNameAdapter.ViewHolder>() {

    var dataOption = ArrayList<String>()
    var dataOptionT = ArrayList<String>()

    class ViewHolder (itemView : View) : RecyclerView.ViewHolder (itemView) {
        var productName = itemView.findViewById<TextView>(R.id.tv_order_product_name_1)
        var optionName = itemView.findViewById<TextView>(R.id.value_option_product)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product_name, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        dataOption.clear()
        dataOptionT.clear()

        val order = models.chartOrders?.getOrNull(position)
        if (order != null) {
            val productNameBuilder = StringBuilder()
            val optionNameBuilder = StringBuilder()

            val optionListCartNames = ArrayList<String>()

//            for (cartOptionalProduct in order.cart?.cartOptionalProducts ?: emptyList()) {
//                dataOption.add(cartOptionalProduct?.productOptionList?.name ?: "")
//                val option2 = cartOptionalProduct?.productOptionList?.name ?: ""
//                optionListCartNames.add(option2)
//            }
//
//            for (optionListCart in order.cart?.optionlistCarts ?: emptyList()) {
//                dataOptionT.add(optionListCart?.productOptionList?.name ?: "")
//                val option1 = optionListCart?.productOptionList?.name ?: ""
//                optionListCartNames.add(option1)
//            }
//            for (optionList in order.cart!!.cartOptionalProducts!!.indices) {
//                for (optionName in order.cart!!.cartOptionalProducts!![optionList]!!.optionlistCarts ?: emptyList()) {
//                    dataOption.add(optionName!!.productOptionList!!.name!!)
//                    val optionName = optionName.productOptionList!!.name
//                    optionListCartNames.add(optionName!!)
//                }
//            }

            for (cartOptionalProduct in order?.cart?.cartOptionalProducts ?: emptyList()) {
                for (optionListCart in cartOptionalProduct?.optionlistCarts ?: emptyList()) {
                    dataOption.add(optionListCart?.productOptionList?.name ?: "")
                    val optionName = optionListCart?.productOptionList!!.name
                    optionListCartNames.add(optionName!!)
                }
            }

            val combinedOptions = optionListCartNames.joinToString("/")
            optionNameBuilder.append(combinedOptions)

            val productName = order.cart?.product?.name ?: ""
            productNameBuilder.append(productName)

            val maxOptionNameLength = 10 // Tentukan panjang maksimal optionName

            if (optionNameBuilder.length > maxOptionNameLength) {
                val truncatedOptionName = optionNameBuilder.substring(0, maxOptionNameLength) + "......." // Tambahkan elipsis
                holder.optionName.text = truncatedOptionName
            } else {
                holder.optionName.text = optionNameBuilder.toString()
            }


            holder.productName.text = productNameBuilder.toString()
//            holder.optionName.text = optionNameBuilder
        }
    }

    override fun getItemCount(): Int {
        return models!!.chartOrders!!.size
    }
}