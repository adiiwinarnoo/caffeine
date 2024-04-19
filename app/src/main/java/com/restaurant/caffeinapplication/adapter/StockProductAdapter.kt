package com.restaurant.caffeinapplication.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.restaurant.caffeinapplication.R
import com.restaurant.caffeinapplication.data.model.ResultItemStocks

class StockProductAdapter (val models : List<ResultItemStocks?>?) :
    RecyclerView.Adapter<StockProductAdapter.ViewHolder>() {

    private lateinit var itemClickListener : ItemClickListener
    private lateinit var itemClickListener2 : ItemClickListener2

    interface ItemClickListener{
        fun onClickUpdateStock(view: View, position: Int,idProduct : Int)
    }
    interface ItemClickListener2{
        fun onClickOutStock(view: View, position: Int, idProduct: Int)
    }
    fun setItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }
    fun setItemClickListener(itemClickListener2: ItemClickListener2){
        this.itemClickListener2 = itemClickListener2
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var titleProduct = itemView.findViewById<TextView>(R.id.tv_title_category)
        var imageOutStock = itemView.findViewById<ImageView>(R.id.imageSecondBG)
        var imageThumbnail = itemView.findViewById<ImageView>(R.id.image_stock_category)
        var buttonOutStock = itemView.findViewById<ConstraintLayout>(R.id.layout_stock_out)
        var buttonConfirm = itemView.findViewById<TextView>(R.id.tv_confirm)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product_stocks_non_category, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.itemView.context).load(models?.get(position)!!.thumbnail).into(holder.imageThumbnail)
        holder.titleProduct.text = models[position]!!.name
        if (models[position]!!.stock!! < 1){
            holder.imageOutStock.visibility = View.VISIBLE
            holder.buttonOutStock.visibility = View.VISIBLE
            holder.buttonConfirm.visibility = View.INVISIBLE
            holder.imageOutStock.setOnClickListener {
                itemClickListener.onClickUpdateStock(it,position,models[position]!!.id!!)
            }
        }else if (models[position]!!.stock!! == 1){
            holder.imageOutStock.visibility = View.GONE
            holder.buttonOutStock.visibility = View.GONE
            holder.buttonConfirm.visibility = View.VISIBLE
            holder.buttonConfirm.setOnClickListener {
                Log.d("AVAIL-STOCK", "onBindViewHolder: $position")
                itemClickListener2.onClickOutStock(it,position,models[position]!!.id!!)
            }
        }

    }

    override fun getItemCount(): Int {
        return models!!.size
    }
}