package com.restaurant.caffeinapplication.adapter

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
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

class ProductOngoingAdapter(val models : List<ResultItemNewOrder?>?) :
    RecyclerView.Adapter<ProductOngoingAdapter.ViewHolder>() {

    private lateinit var itemClickListener : ItemClickListener
    private lateinit var itemClickFinish : ItemClickListenerFinish
    private lateinit var itemClickDetail : ItemClickDetailProduct

    interface ItemClickListener{
        fun onClick(view: View, position: Int, idProduct : Int,type : String)
    }
    interface ItemClickListenerFinish{
        fun onClick(view: View, position: Int, idProduct : Int,type : String)
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
    fun setItemClickListener(itemClickListenerFinish: ItemClickListenerFinish){
        this.itemClickFinish = itemClickListenerFinish
    }
    fun setItemClickDetail(itemClickDetailProduct: ItemClickDetailProduct){
        this.itemClickDetail = itemClickDetailProduct
    }


    private lateinit var itemClickListener2 : ItemClickListener2

    interface ItemClickListener2{
        fun onClickDelivery(view: View, position: Int, idProduct: Int)
    }

    fun setItemClickListener(itemClickListener2: ItemClickListener2){
        this.itemClickListener2 = itemClickListener2
    }
    private lateinit var itemClickListener3 : ItemClickListener3

    interface ItemClickListener3{
        fun onClickPickup(view: View, position: Int, idProduct: Int)
        fun pickupOnly(view: View, position: Int,idProduct: Int,type : String)
    }

    fun setItemClickListener(itemClickListener3: ItemClickListener3){
        this.itemClickListener3 = itemClickListener3
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
        var cardView = itemView.findViewById<CardView>(R.id.card_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product_progress_order, parent,false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        dataOption.clear()
        dataOptionT.clear()
        val model = models!!.get(position)
        var positionCart = 0
        val currentModel = models.get(position)

        val layoutManager =
            LinearLayoutManager(holder.recyclerProductName.context, LinearLayoutManager.VERTICAL, false)
        holder.recyclerProductName.layoutManager = layoutManager
        holder.invoiceId.text = model?.id?.toString()

        if (currentModel?.status.equals("making", ignoreCase = true) || currentModel?.status.equals("delivery", ignoreCase = true)
            ||currentModel?.status.equals("pickup", ignoreCase = true) || currentModel?.status.equals("waiting_user_pickup") ) {
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
                    val adapter = ProductNameAdapter(models[position]!!) // Pass only the orders list
                    holder.recyclerProductName.adapter = adapter
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

                val readableDuration = when {
                    days > 0 -> "${days} 일전${if (days > 1) "s" else ""}"
                    hours > 0 -> "${hours} 시간전${if (hours > 1) "s" else ""}"
                    else -> "${minutes} 분전${if (minutes > 1) "s" else ""}"
                }

                holder.timeAgo.text = readableDuration

//                for (k in currentModel.chartOrders!![j]!!.cart!!.optionlistCarts!!.indices) {
//                    dataOptionT.add(currentModel.chartOrders[j]!!.cart!!.optionlistCarts!![k]!!.productOptionList!!.name!!)
//                }
//                for (l in currentModel.chartOrders[j]!!.cart!!.cartOptionalProducts!!.indices) {
//                    dataOption.add(currentModel.chartOrders[j]!!.cart!!.cartOptionalProducts!![l]!!.productOptionList!!.name!!)
//                }
            }

            if (currentModel.chartOrders!!.isNotEmpty() && currentModel.chartOrders[0]!!.cart != null) {
                Glide.with(holder.itemView.context)
                    .load(currentModel.chartOrders[0]!!.cart!!.product?.thumbnail)
                    .into(holder.imageThumbnail)
            }
            if(currentModel.status.equals("waiting_user_pickup")){
                holder.button.text = "수령 완료"
                holder.button.setOnClickListener {
                    itemClickFinish.onClick(it,position,models[position]!!.id!!,models[position]!!.type!!)
                }
            } else if (currentModel.status.equals("making",ignoreCase = true) && currentModel.type.equals("dinein") || currentModel.type.equals("pickup")){
                holder.button.text = "수령 요청"
                holder.button.setOnClickListener {
                    Log.d("HIRR", "onBindViewHolder: ${currentModel.status}")
                    itemClickListener.onClick(it,position,models[position]!!.id!!,models[position]!!.type!!)
                }
            }else if (currentModel.status.equals("making", ignoreCase = true)){
                holder.button.text = "주문 접수 완료 (배송기사 매칭 중)"
                holder.button.isClickable = false
                holder.button.alpha = 0.5f
                holder.button.setOnClickListener {
                    Log.d("WIRR", "onBindViewHolder: ")
//                    itemClickListener.onClick(it,position,models[position]!!.id!!,models[position]!!.type!!)
                }
            }else if (currentModel.status.equals("delivery", ignoreCase = true)){
                holder.button.text = "배송 중"
                holder.button.setOnClickListener {
                    itemClickListener2.onClickDelivery(it,position,models[position]!!.id!!)
                }
            }else if (currentModel.status.equals("pickup", ignoreCase = true)){
                if (currentModel.type.equals("pickup", ignoreCase = true) || currentModel.type.equals("dinein", ignoreCase = true)){
                    holder.button.text = "픽업 대기중"
                    holder.button.setOnClickListener {
                        itemClickListener3.pickupOnly(it,position,models[position]!!.id!!,models[position]!!.type!!)
                    }
                }else{
                    holder.button.text = "배송기사 픽업대기"
                    holder.button.setOnClickListener {
                        itemClickListener3.onClickPickup(it,position,models[position]!!.id!!)
                    }
                }

            }

            if (currentModel.type.equals("dinein")){
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
            }else if (currentModel.type.equals("delivery")){
                holder.typeOrder.text = "배달 주문"
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
            }else if (currentModel.type.equals("pickup") || currentModel.type.equals("dinein")){
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
        }
    }


    override fun getItemCount(): Int {
        return models?.size!!
    }
}