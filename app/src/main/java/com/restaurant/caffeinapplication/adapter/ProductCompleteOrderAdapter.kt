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

class ProductCompleteOrderAdapter(val models : List<ResultItemNewOrder?>?) :
    RecyclerView.Adapter<ProductCompleteOrderAdapter.ViewHolder>() {

    private lateinit var itemClickListener : ItemClickListener
    private lateinit var itemClickDetail : ItemClickDetailProduct

    interface ItemClickListener{
        fun onClickDeliverOrder(view: View, position: Int, customerName : String, addressUser : String,
                                productName : String,priceProduct : Int,orderNumber : Int, dateTime : String,
                                deliveryCost : Int, totalPayment : Int,models : List<ResultItemNewOrder?>?,totalGross: Int)
    }
    interface ItemClickDetailProduct {
        fun onClick(
            view: View, position: Int, customerName: String, addressUser: String,
            productName: String, priceProduct: Int, orderNumber: Int, dateTime: String,
            deliveryCost: Int, totalPayment: Int, idProduct: Int, models: List<ResultItemNewOrder?>?,
            type: String,paymentMethod: String,totalGross: Int,typeOrder: String,userPhone: String
        )
    }

    fun setItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }
    fun setItemClickDetail(itemClickDetailProduct: ItemClickDetailProduct){
        this.itemClickDetail = itemClickDetailProduct
    }

    private lateinit var itemClickListener2 : ItemClickListener2

    interface ItemClickListener2{
        fun onClickPackagingOrder(view: View, position: Int, customerName : String, productName : String,
                                  priceProduct : Int,orderNumber : Int, dateTime : String,
                                  deliveryCost : Int, totalPayment : Int,paymentMethod : String,
                                  models : List<ResultItemNewOrder?>?,totalGross: Int)
    }

    fun setItemClickListener(itemClickListener2: ItemClickListener2){
        this.itemClickListener2 = itemClickListener2
    }

    var dataOption = ArrayList<String>()
    var dataOptionT = ArrayList<String>()



    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var imageThumbnail = itemView.findViewById<ImageView>(R.id.img_product)
        var typeOrder = itemView.findViewById<TextView>(R.id.tv_delivery_order)
        var productName = itemView.findViewById<TextView>(R.id.tv_order_product_name_1)
        var optionListName = itemView.findViewById<TextView>(R.id.value_option_product)
        var button = itemView.findViewById<TextView>(R.id.tv_status_button)
        var textDate = itemView.findViewById<TextView>(R.id.tv_order_date)
        var timeAgo = itemView.findViewById<TextView>(R.id.tv_time_ago)
        var recyclerProductName = itemView.findViewById<RecyclerView>(R.id.recycler_view_product_name)
        var invoiceId = itemView.findViewById<TextView>(R.id.value_invoice)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product_complete_order, parent,false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("DATA-MAKING", "onBindViewHolder: ${models!!.size}")
        dataOption.clear()
        dataOptionT.clear()
        var positionCart = 0
        val currentModel = models?.get(position)
        val model = models!!.get(position)

        val layoutManager =
            LinearLayoutManager(holder.recyclerProductName.context, LinearLayoutManager.VERTICAL, false)
        holder.recyclerProductName.layoutManager = layoutManager
        holder.invoiceId.text = currentModel?.id?.toString()
        if (currentModel?.status.equals("finished", ignoreCase = true)) {
            val orders = currentModel?.chartOrders
            if (orders != null) {
                if (orders.size == 1) {
                    val singleOrderList = ArrayList<ResultItemNewOrder?>()
                    val singleOrderItem = ResultItemNewOrder(id = currentModel.id, chartOrders = listOf(orders[0]))
                    singleOrderList.add(singleOrderItem)
                    val adapter = ChartOneAdapter(singleOrderList)
                    holder.recyclerProductName.adapter = adapter
                    adapter.notifyDataSetChanged()
                } else if (orders.size == 2) {
                    val adapter = ProductNameAdapter(models[position]!!) // Pass only the orders list
                    holder.recyclerProductName.adapter = adapter
                    adapter.notifyDataSetChanged()
                }else{
                    val adapter = ProductNameAdapter(models[position]!!) // Pass only the orders list
                    holder.recyclerProductName.adapter = adapter
                }
            }
            for (j in currentModel!!.chartOrders!!.indices) {
                positionCart = j
                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
                val instant = Instant.parse(models[position]!!.chartOrders!![j]!!.createdAt)
                val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                val dateTime = formatter.format(localDateTime)
                val yearOrder = dateTime.substring(6,10)
                val monthOrder = dateTime.substring(3,5)
                val dateOrder = dateTime.take(2)
                val hourOrder = dateTime.substring(10,13)
                val minuteOrder = dateTime.substring(14,16)
                holder.textDate.text = "$yearOrder 년 $monthOrder 월 $dateOrder 일 $hourOrder 시 $minuteOrder 분"

                // Calculate the difference between the API dateTime and the current time
                val apiDateTime = localDateTime.atZone(ZoneId.systemDefault()).toInstant()
                val currentDateTime = Instant.now()
                val duration = Duration.between(apiDateTime, currentDateTime)

                // Calculate days, hours, and minutes
                val days = duration.toDays()
                val hours = duration.toHours() % 24
                val minutes = duration.toMinutes() % 60

                val readableDuration =   when {
                    days > 0 -> "${days} 일전${if (days > 1) "s" else ""}"
                    hours > 0 -> "${hours} 시간전${if (hours > 1) "s" else ""}"
                    else -> "${minutes} 분전${if (minutes > 1) "s" else ""}"
                }

                holder.timeAgo.text = readableDuration
                Glide.with(holder.itemView.context)
                    .load(currentModel.chartOrders!![j]!!.cart!!.product!!.thumbnail)
                    .into(holder.imageThumbnail)
//                holder.productName.text = currentModel.chartOrders[j]!!.cart!!.product!!.name

//                for (k in currentModel.chartOrders!![j]!!.cart!!.optionlistCarts!!.indices) {
//                    dataOptionT.add(currentModel.chartOrders[j]!!.cart!!.optionlistCarts!![k]!!.productOptionList!!.name!!)
//                }
//                for (l in currentModel.chartOrders[j]!!.cart!!.cartOptionalProducts!!.indices) {
//                    dataOption.add(currentModel.chartOrders[j]!!.cart!!.cartOptionalProducts!![l]!!.productOptionList!!.name!!)
                }
            }

            when(currentModel?.type){
                "delivery" -> {
                    for (i in models[position]!!.chartOrders!!.indices) {
                        holder.itemView.setOnClickListener {
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
                                models,models[position]?.totalGross!!
                            )
                        }
                    }
                }
                "pickup" -> {
                    for (i in models[position]!!.chartOrders!!.indices){
                    holder.itemView.setOnClickListener {
                            itemClickListener2.onClickPackagingOrder(it, position,models[position]!!.user!!.name!!,
                                models[position]!!.chartOrders!![i]!!.cart!!.product!!.name!!,
                                models[position]!!.chartOrders!![i]!!.cart!!.productAndOptionTotalPrice!!,
                                models[position]!!.id!!,models[position]!!.createdAt!!,
                                models[position]!!.deliveryPrice!!,models[position]!!.totalPrice!!,
                                models[position]!!.payment!!.paymentType!!,models,models[position]?.totalGross!!)
                        }
                    }
                }
            }
        if (currentModel?.type.equals("dinein")){
            holder.typeOrder.text = "홀 주문"
            val chartOrders = model?.chartOrders ?: emptyList()
            for (i in chartOrders.indices) {
                holder.itemView.setOnClickListener {
                    if (chartOrders.isNotEmpty()) {
                        // Menggunakan order yang telah disalin
                        itemClickDetail.onClick(it, position, model?.user?.name ?: "",
                            model?.addressUser?.address ?: "",
                            chartOrders?.get(i)!!.cart?.product?.name ?: "",
                            chartOrders?.get(i)!!.cart?.productAndOptionTotalPrice ?: 0,
                            model?.id ?: -1, model?.createdAt ?: "",
                            model?.deliveryPrice ?: 0, model?.totalPrice ?: 0,
                            model?.id ?: -1, models, models!![position]!!.type!!,
                            models!![position]!!.payment!!.paymentType!!, models[position]!!.totalGross!!,models[position]!!.type!!,models[position]!!.user?.phone!!
                        )
                    }
                }
            }
        }else if (currentModel?.type.equals("delivery")){
            holder.typeOrder?.text = "배달 주문"
            val chartOrders = model?.chartOrders ?: emptyList()
            for (i in chartOrders.indices) {
                holder.itemView.setOnClickListener {
                    if (chartOrders.isNotEmpty()) {
                        // Menggunakan order yang telah disalin
                        itemClickDetail.onClick(it, position, model?.user?.name ?: "",
                            model?.addressUser?.address ?: "",
                            chartOrders?.get(i)!!.cart?.product?.name ?: "",
                            chartOrders?.get(i)!!.cart?.productAndOptionTotalPrice ?: 0,
                            model?.id ?: -1, model?.createdAt ?: "",
                            model?.deliveryPrice ?: 0, model?.totalPrice ?: 0,
                            model?.id ?: -1, models, models!![position]!!.type!!,
                            models!![position]!!.payment!!.paymentType!!, models[position]!!.totalGross!!,models[position]!!.type!!,models[position]!!.user?.phone!!
                        )
                    }
                }
            }
        }else if (currentModel?.type.equals("pickup")){
            holder.typeOrder.text = "포장 주문"
            val chartOrders = model?.chartOrders ?: emptyList()
            for (i in chartOrders.indices) {
                holder.itemView.setOnClickListener {
                    if (chartOrders.isNotEmpty()) {
                        // Menggunakan order yang telah disalin
                        itemClickDetail.onClick(it, position, model?.user?.name ?: "",
                            model?.addressUser?.address ?: "",
                            chartOrders?.get(i)!!.cart?.product?.name ?: "",
                            chartOrders?.get(i)!!.cart?.productAndOptionTotalPrice ?: 0,
                            model?.id ?: -1, model?.createdAt ?: "",
                            model?.deliveryPrice ?: 0, model?.totalPrice ?: 0,
                            model?.id ?: -1, models, models!![position]!!.type!!,
                            models!![position]!!.payment!!.paymentType!!, models[position]!!.totalGross!!,models[position]!!.type!!,models[position]!!.user?.phone!!
                        )
                    }
                }
            }
        }

//            holder.typeOrder.text = currentModel?.type

            val combinedOptions = (dataOption + dataOptionT).joinToString("/")
////            holder.optionListName.text = combinedOptions
//        }
    }


    override fun getItemCount(): Int {
        return models?.filter { it?.chartOrders != null && it.chartOrders.isNotEmpty() }?.size ?: 0
    }
}