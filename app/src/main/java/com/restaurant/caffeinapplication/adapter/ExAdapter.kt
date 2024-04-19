package com.restaurant.caffeinapplication.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.restaurant.caffeinapplication.R
import com.restaurant.caffeinapplication.data.model.DataItemVoucher
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ExAdapter(val models : List<DataItemVoucher>?) : RecyclerView.Adapter<ExAdapter.ViewHolder>() {

    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {
        var title = itemView.findViewById<TextView>(R.id.childTitle)
        var textDate = itemView.findViewById<TextView>(R.id.tv_order_date)
        var voucherName = itemView.findViewById<TextView>(R.id.tv_voucher_name)
        var amountUsed = itemView.findViewById<TextView>(R.id.tv_used_amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.child_item_2, parent,false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        val instant = Instant.parse(models!![position]?.createdAt)
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val dateTime = formatter.format(localDateTime)
        val yearOrder = dateTime.substring(6, 10)
        val monthOrder = dateTime.substring(3, 5)
        val dateOrder = dateTime.take(2)
        val hourOrder = dateTime.substring(10, 13)
        val minuteOrder = dateTime.substring(14, 16)
        holder.textDate.text = "$yearOrder 년 $monthOrder 월 $dateOrder 일 $hourOrder 시 $minuteOrder 분"

        holder.voucherName.text = models[position]?.voucherCustomer?.voucher?.name
        holder.amountUsed.text = "${models[position]?.amount.toString()} 원"
    }

    override fun getItemCount(): Int {
        return models!!.size
    }
}