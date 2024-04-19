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
import com.restaurant.caffeinapplication.data.model.ResultItemNewOrder
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ProductNewOrderAdapter(val models: List<ResultItemNewOrder?>?) :
    RecyclerView.Adapter<ProductNewOrderAdapter.ViewHolder>() {

    private lateinit var itemClickListener: ItemClickListener
    private lateinit var itemClickListener2: ItemClickListener2

    interface ItemClickListener {
        fun onClick(view: View, position: Int, idProduct: Int)
    }

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    interface ItemClickListener2 {
        fun onClick(
            view: View, position: Int, customerName: String, addressUser: String,
            productName: String, priceProduct: Int, orderNumber: Int, dateTime: String,
            deliveryCost: Int, totalPayment: Int, idProduct: Int, models: List<ResultItemNewOrder?>?,
            type: String,paymentMethod: String,totalGross: Int
        )

//        fun clickPickUp(
//            view: View, position: Int, customerName: String, addressUser: String,
//            productName: String, priceProduct: Int, orderNumber: Int, dateTime: String,
//            deliveryCost: Int, totalPayment: Int, idProduct: Int, paymentMethod: String,models: List<ResultItem?>?
//        )
    }

    fun setItemClickListener(itemClickListener2: ItemClickListener2) {
        this.itemClickListener2 = itemClickListener2
    }

    var dataOption = ArrayList<String>()
    var dataOptionT = ArrayList<String>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageThumbnail = itemView.findViewById<ImageView>(R.id.img_product)
        var typeOrder = itemView.findViewById<TextView>(R.id.tv_delivery_order)
        var productName = itemView.findViewById<TextView>(R.id.tv_order_product_name_1)
        var optionListName = itemView.findViewById<TextView>(R.id.value_option_product)
        var buttonCancel = itemView.findViewById<TextView>(R.id.tv_cancel)
        var buttonConfirm = itemView.findViewById<TextView>(R.id.tv_confirm)
        var textDate = itemView.findViewById<TextView>(R.id.tv_order_date)
        var timeAgo = itemView.findViewById<TextView>(R.id.tv_time_ago)
        var recyclerProductName = itemView.findViewById<RecyclerView>(R.id.recycler_view_product_name)
        var invoiceId = itemView.findViewById<TextView>(R.id.value_invoice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_new_order_home, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models?.get(position)
        Log.d("DATA-NEW-ORDER-ADA", "onBindViewHolder: ${models!!.size}")
        holder.itemView.setOnClickListener {
            Log.d("DATA-NEW-ORDER-AC", "onBindViewHolder-click: ${models[position]!!.id}")
        }

        val layoutManager =
            LinearLayoutManager(holder.recyclerProductName.context, LinearLayoutManager.VERTICAL, false)
        holder.recyclerProductName.layoutManager = layoutManager
        holder.invoiceId.text = model?.id?.toString()

        if (model?.status.equals("ordered", ignoreCase = true)) {
            val orders = model?.chartOrders
            if (orders != null) {
                if (orders.size == 1) {
                    val singleOrderList = ArrayList<ResultItemNewOrder?>()
                    val singleOrderItem = ResultItemNewOrder(id = model.id, chartOrders = listOf(orders[0]))
                    singleOrderList.add(singleOrderItem)
                    val adapter = ChartOneAdapter(singleOrderList)
                    holder.recyclerProductName.adapter = adapter
                    adapter.notifyDataSetChanged()
                } else if (orders.size == 2) {
                    dataOption.clear()
                    dataOptionT.clear()
                    val adapter = ProductNameAdapter(models[position]!!)
                    holder.recyclerProductName.adapter = adapter
                    adapter.notifyDataSetChanged()
                }else{
                    dataOption.clear()
                    dataOptionT.clear()
                    val adapter = ProductNameAdapter(models[position]!!)
                    holder.recyclerProductName.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
                for (order in orders){
                    for (cartOptionalProduct in order?.cart?.cartOptionalProducts ?: emptyList()) {
                        for (optionListCart in cartOptionalProduct?.optionlistCarts ?: emptyList()) {
                            dataOption.add(optionListCart?.productOptionList?.name ?: "")
                        }
                    }
                }


                for (j in model.chartOrders.indices ?: emptyList()) {
                    Glide.with(holder.itemView.context)
                        .load(model.chartOrders.get(0)?.cart?.product?.thumbnail)
                        .into(holder.imageThumbnail)
                    Log.d("DATA-IMAGE", "onBindViewHolder: ${model.chartOrders.get(0)?.cart?.product?.thumbnail}")
                    if (model.type.equals("dinein")){
                        holder.typeOrder.text = "홀 주문"
                    }else if (model.type.equals("delivery")){
                        holder.typeOrder.text = "배달 주문"
                    }else if (model.type.equals("pickup")){
                        holder.typeOrder.text = "포장 주문"
                    }

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

                    // Calculate the difference between the API dateTime and the current time
                    val apiDateTime = localDateTime.atZone(ZoneId.systemDefault()).toInstant()
                    val currentDateTime = Instant.now()
                    val duration = Duration.between(apiDateTime, currentDateTime)

                    // Calculate days, hours, and minutes
                    val days = duration.toDays()
                    val hours = duration.toHours() % 24
                    val minutes = duration.toMinutes() % 60

                    val readableDuration = when {
                        days > 0 -> "${days} 일전${if (days > 1) "" else ""}"
                        hours > 0 -> "${hours} 시간전${if (hours > 1) "" else ""}"
                        else -> "${minutes} 분전${if (minutes > 1) "" else ""}"
                    }

                    holder.timeAgo.text = readableDuration
                }

                // Button Cancel Click Listener
                holder.buttonCancel.setOnClickListener {
                    itemClickListener.onClick(it, position, model.id ?: -1)
                }

                // Button Confirm Click Listener
                val chartOrders = model.chartOrders ?: emptyList()
                for (i in chartOrders.indices) {
                    holder.buttonConfirm.setOnClickListener {
                        if (chartOrders.isNotEmpty()) {
                            // Menggunakan order yang telah disalin
                            itemClickListener2.onClick(it, position, model.user?.name ?: "",
                                model.addressUser?.address ?: "",
                                chartOrders?.get(i)!!.cart?.product?.name ?: "",
                                chartOrders?.get(i)!!.cart?.productAndOptionTotalPrice ?: 0,
                                model.id ?: -1, model.createdAt ?: "",
                                model.deliveryPrice ?: 0, model.totalPrice ?: 0,
                                model.id ?: -1, models, models!![position]!!.type!!,
                                models!![position]!!.payment!!.paymentType!!, models[position]!!.totalGross!!
                                )
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return models?.size!!
    }

}
