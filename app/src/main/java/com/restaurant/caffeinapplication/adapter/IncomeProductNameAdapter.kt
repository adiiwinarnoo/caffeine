package com.restaurant.caffeinapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.restaurant.caffeinapplication.R
import com.restaurant.caffeinapplication.data.model.DataOrderItem

class IncomeProductNameAdapter(val models : DataOrderItem?) :
    RecyclerView.Adapter<IncomeProductNameAdapter.ViewHolder>() {

    var dataOption = ArrayList<String>()
    var dataOptionT = ArrayList<String>()

    class ViewHolder (itemView : View) : RecyclerView.ViewHolder (itemView) {
        var productName = itemView.findViewById<TextView>(R.id.tv_menu_order)
        var productPrice = itemView.findViewById<TextView>(R.id.tv_price)
        var recyclerPrice = itemView.findViewById<RecyclerView>(R.id.recycler_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_name_option_product, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        dataOption.clear()
        dataOptionT.clear()

        val chartOrdersItem = models?.chartOrders?.get(position) // Get the specific ChartOrdersItem
        holder.productName.text = chartOrdersItem?.cart?.product?.name
        holder.productPrice.text = chartOrdersItem?.cart?.productAndOptionTotalPrice.toString()

        val layoutManager = LinearLayoutManager(holder.recyclerPrice.context, LinearLayoutManager.VERTICAL, false)
        holder.recyclerPrice.layoutManager = layoutManager

        val adapterPrice = IncomePricePopupAdapter(chartOrdersItem!!) // Pass the specific ChartOrdersItem to the adapter
        holder.recyclerPrice.adapter = adapterPrice


    }

    override fun getItemCount(): Int {
        return models?.chartOrders?.size ?: 0
    }
}