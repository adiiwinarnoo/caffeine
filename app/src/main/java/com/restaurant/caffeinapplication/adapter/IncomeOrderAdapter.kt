package com.restaurant.caffeinapplication.adapter

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.restaurant.caffeinapplication.R
import com.restaurant.caffeinapplication.data.model.DataOrderItem
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class IncomeOrderAdapter(val models : List<DataOrderItem?>) :
    RecyclerView.Adapter<IncomeOrderAdapter.ViewHolder>() {

    private lateinit var itemClickListener : IncomeOrderItemClickListener

    interface IncomeOrderItemClickListener{
        fun onClickDeliverOrder(view: View, position: Int, customerName : String, addressUser : String,
                                productName : String,priceProduct : Int,orderNumber : Int, dateTime : String,
                                deliveryCost : Int, totalPayment : Int,models : List<DataOrderItem?>?,
                                totalGross: Int,paymentMethod : String)

        fun onClickPackagingOrder(view: View, position: Int, customerName : String,
                                  productName : String,priceProduct : Int,orderNumber : Int, dateTime : String,
                                  deliveryCost : Int, totalPayment : Int,paymentMethod : String
                                  ,models : List<DataOrderItem?>?,totalGross: Int)
    }

    fun setItemClickListener(itemClickListener: IncomeOrderItemClickListener){
        this.itemClickListener = itemClickListener
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var imageThumbnail = itemView.findViewById<ImageView>(R.id.iv_image_container)
        var productName = itemView.findViewById<TextView>(R.id.tv_product_name)
        var textDate = itemView.findViewById<TextView>(R.id.tv_order_date)
        var recyclerProductName = itemView.findViewById<RecyclerView>(R.id.recycler_view_product_name)
        var tvDetail = itemView.findViewById<TextView>(R.id.tv_read_more)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_income, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models?.get(position)
        Log.d("DATA-NEW-ORDER-ADA-INCOME", "onBindViewHolder: ${models!!.size}")
        holder.itemView.setOnClickListener {
            Log.d("DATA-NEW-ORDER-AC-INCOME", "onBindViewHolder-click: ${models[position]!!.id}")
        }

        val layoutManager =
            LinearLayoutManager(holder.recyclerProductName.context, LinearLayoutManager.VERTICAL, false)
        holder.recyclerProductName.layoutManager = layoutManager

            val orders = model?.chartOrders
            if (orders != null) {
                if (orders.size == 1) {
                    val singleOrderList = ArrayList<DataOrderItem?>()
                    val singleOrderItem = DataOrderItem(id = model.id, chartOrders = listOf(orders[0]))
                    singleOrderList.add(singleOrderItem)
                    val adapter = IncomeOrderOneAdapter(singleOrderList)
                    holder.recyclerProductName.adapter = adapter
                    adapter.notifyDataSetChanged()
                } else if (orders.size == 2) {
                    val adapter = IncomeOrderProductAdapter(models[position]!!)
                    holder.recyclerProductName.adapter = adapter
                    adapter.notifyDataSetChanged()
                }

                for (j in model.chartOrders.indices ?: emptyList()) {
                    Glide.with(holder.itemView.context)
                        .load(model.chartOrders.get(0)?.cart?.product?.thumbnail)
                        .into(holder.imageThumbnail)

                    holder.productName.text = model.chartOrders[j]?.cart?.product?.name

                    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
                    val instant = Instant.parse(model.chartOrders!![j]!!.createdAt)
                    val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                    val dateTime = formatter.format(localDateTime)
                    val yearOrder = dateTime.substring(6, 10)
                    val monthOrder = dateTime.substring(3, 5)
                    val dateOrder = dateTime.take(2)
                    val hourOrder = dateTime.substring(10, 13)
                    val minuteOrder = dateTime.substring(14, 16)
                    holder.textDate.text = "$yearOrder 년 $monthOrder 월 $dateOrder 일 $hourOrder 시 $minuteOrder 분"
                }
            }

        when(model?.type){
            "delivery" -> {
                for (i in models[position]!!.chartOrders!!.indices) {
                    holder.tvDetail.setOnClickListener {
                        Log.d("data-posisi", "onBindViewHolder: $position")
                        itemClickListener.onClickDeliverOrder(
                            it,
                            position,
                            models[position]!!.user!!.name!!,
                            models[position]!!.addressUser!!.address!!,
                            models[position]!!.chartOrders!![i]!!.cart!!.product!!.name!!,
                            models[position]!!.chartOrders!![i]!!.cart!!.productAndOptionTotalPrice!!,
                            models[position]!!.id!!,
                            models[position]!!.createdAt!!,
                            models[position]!!.deliveryPrice!!,
                            models[position]!!.totalPrice!!,
                            models,models[position]?.totalGross!!,
                            models[position]!!.payment!!.paymentType!!
                        )
                    }
                }
            }
            "pickup" -> {
                Log.d("ID-PRODUCT", "onBindViewHolder: ${models[position]?.id}")
                for (i in models[position]!!.chartOrders!!.indices){
                    holder.tvDetail.setOnClickListener {
                        Log.d("data-posisi", "onBindViewHolder: $position")
                        itemClickListener.onClickPackagingOrder(it, position,models[position]!!.user!!.name!!,
                            models[position]!!.chartOrders!![i]!!.cart!!.product!!.name!!,
                            models[position]!!.chartOrders!![i]!!.cart!!.productAndOptionTotalPrice!!,
                            models[position]!!.id!!,models[position]!!.createdAt!!,
                            models[position]!!.deliveryPrice!!,models[position]!!.totalPrice!!,
                            models[position]!!.payment!!.paymentType!!,models,models[position]?.totalGross!!)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return models.size
    }
}