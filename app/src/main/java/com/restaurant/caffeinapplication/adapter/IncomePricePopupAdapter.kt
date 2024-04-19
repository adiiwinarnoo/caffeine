package com.restaurant.caffeinapplication.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.restaurant.caffeinapplication.R
import com.restaurant.caffeinapplication.data.model.ChartOrdersItemIncomeOrder

class IncomePricePopupAdapter(val models : ChartOrdersItemIncomeOrder) :
    RecyclerView.Adapter<IncomePricePopupAdapter.ViewHolder>() {

    class ViewHolder (itemView : View) : RecyclerView.ViewHolder (itemView) {
        var optionName = itemView.findViewById<TextView>(R.id.tv_option)
        var optionPrice = itemView.findViewById<TextView>(R.id.tv_value_price)
        var optionValue = itemView.findViewById<TextView>(R.id.tv_option_value)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_price_popup, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cartOrder = models.cart

        val optionNames = ArrayList<String>()
        val optionPrice = ArrayList<String>()
        val optionValue = ArrayList<String>()
        val priceOption = ArrayList<String>()

//        for (cartOptionalProduct in cartOrder?.cartOptionalProducts ?: emptyList()) {
//            optionNames.add(cartOptionalProduct?.productOptionList?.name ?: "")
//            val price = cartOptionalProduct?.value ?: 0 // Ganti 0 dengan nilai default jika tidak ada harga
//            optionPrice.add(price.toString())
//        }
//
//        for (optionListCart in cartOrder?.optionlistCarts ?: emptyList()) {
//            optionNames.add(optionListCart?.productOptionList?.name ?: "")
//            val price = optionListCart?.value ?: 0 // Ganti 0 dengan nilai default jika tidak ada harga
//            optionPrice.add(price.toString())
//        }

        if (!cartOrder?.cartOptionalProducts!!.isEmpty() || !cartOrder.cartOptionalProducts.isNullOrEmpty()){
        }
        for (optionListCart in cartOrder.cartOptionalProducts) {
            for (optionList2 in optionListCart?.optionlistCarts!!){
                Log.d("OPTION-DEBUG-2", "cartOptionalProduct: ${optionList2?.productOptionList?.name!!}")
                optionNames.add("> " + optionList2?.productOptionList?.name!!)
            }

            Log.d("DATA-OPTION-LIST-CART", "onBindViewHolder: ${optionListCart.optionlistCarts}")

            for (optionListValue in optionListCart.optionlistCarts) {
                Log.d("PRICE-INI", "onBindViewHolder-ASDA: ${optionListValue?.productOptionList?.addtionalPrice}")
                if (optionListValue?.productOptionList?.addtionalPrice != 0) {
                    if (optionListValue?.value != 0 ){
                        optionValue.add("x ${optionListValue?.value}")
                        val additionalPrice = optionListValue?.productOptionList?.addtionalPrice!! * optionListValue.value!!
                        priceOption.add(additionalPrice.toString())
                    }else{
                        optionValue.add("")
                        val additionalPrice = optionListValue?.productOptionList?.addtionalPrice!!
                        priceOption.add(additionalPrice.toString())
                    }
                } else {
                    optionValue.add("")
                    priceOption.add("")
                }
            }
        }


        holder.optionName.text = optionNames.joinToString("\n")
//        holder.optionPrice.text = optionPrice.joinToString("\n")

        if (!optionValue.isNullOrEmpty() && !priceOption.isNullOrEmpty()){
            holder.optionValue.visibility = View.VISIBLE
            Log.d("PRICE-INI", "onBindViewHolder: $priceOption")
            holder.optionValue.text = optionValue.joinToString("\n")
            holder.optionPrice.text = priceOption.joinToString("\n")
        }else if (priceOption.isNullOrEmpty()){
            holder.optionPrice.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return 1
    }
}