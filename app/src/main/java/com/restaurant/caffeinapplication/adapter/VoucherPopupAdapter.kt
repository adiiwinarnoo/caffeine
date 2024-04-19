package com.restaurant.caffeinapplication.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.restaurant.caffeinapplication.R
import com.restaurant.caffeinapplication.data.model.ResultItemNewOrder

class VoucherPopupAdapter (val models : ResultItemNewOrder?) :
    RecyclerView.Adapter<VoucherPopupAdapter.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var voucherName = itemView.findViewById<TextView>(R.id.tv_general_payment)
        var voucherPrice = itemView.findViewById<TextView>(R.id.value_general_payment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_voucher_list, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return models?.orderVoucherHistories!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("DEBUG-IDA", "onBindViewHolder: ${models?.orderVoucherHistories?.size}")
        holder.voucherName.text = models?.orderVoucherHistories!![position].voucherName
        holder.voucherPrice.text = models.orderVoucherHistories[position].voucherAmount.toString()
    }
}