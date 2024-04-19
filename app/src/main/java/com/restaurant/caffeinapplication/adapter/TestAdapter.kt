package com.restaurant.caffeinapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.restaurant.caffeinapplication.R
import com.restaurant.caffeinapplication.data.model.ResultItemCategoryStocks

class TestAdapter (val models : List<ResultItemCategoryStocks?>?) :
    RecyclerView.Adapter<TestAdapter.ViewHolder>() {

    private lateinit var itemClickListener : ItemClickListener

    interface ItemClickListener{
        fun onClickCategory(view: View, position: Int,idCategoryProduct : Int)
    }

    fun setItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }
    private lateinit var itemClickListener2 : ItemClickListener2

    interface ItemClickListener2{
        fun onClickCategoryDefault(view: View, position: Int)
    }

    fun setItemClickListener(itemClickListener2: ItemClickListener2){
        this.itemClickListener2 = itemClickListener2
    }

    private var lastClickedPosition: Int = RecyclerView.NO_POSITION

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var titleProduct = itemView.findViewById<TextView>(R.id.tv_title_category)
        var imageCategory = itemView.findViewById<ImageView>(R.id.image_stock_category)
        var cardCategory = itemView.findViewById<CardView>(R.id.card_category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stocks_category, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.itemView.context).load(models?.get(position)!!.img).into(holder.imageCategory)
        holder.titleProduct.text = models!![position]!!.name

        val item = models!![position]

        holder.cardCategory.setOnClickListener {
            itemClickListener.onClickCategory(it, position, models[position]!!.id!!)

            // Jika item diklik kembali, atur warna latar belakangnya menjadi putih
            if (position == lastClickedPosition) {
                itemClickListener2.onClickCategoryDefault(it,position)
                item?.isSelected = false
                holder.cardCategory.setCardBackgroundColor(holder.itemView.context.getColor(R.color.white))
                lastClickedPosition = RecyclerView.NO_POSITION
            } else {
                // Jika item sebelumnya sudah diklik, atur warna latar belakangnya kembali ke warna default (putih)
                if (lastClickedPosition != RecyclerView.NO_POSITION) {
                    val lastItem = models[lastClickedPosition]
                    lastItem?.isSelected = false
                    notifyItemChanged(lastClickedPosition)
                }

                // Set item yang baru diklik menjadi terpilih
                item?.isSelected = true
                holder.cardCategory.setCardBackgroundColor(holder.itemView.context.getColor(R.color.item_category))
                lastClickedPosition = position
            }

            // Simpan posisi item yang terakhir diklik
            lastClickedPosition = position
        }

        // Atur warna latar belakang sesuai status isSelected
        val backgroundColor = if (item?.isSelected == true) R.color.item_category else R.color.white
        val textColor = if (item?.isSelected == true) R.color.white else R.color.black
        holder.cardCategory.setCardBackgroundColor(holder.itemView.context.getColor(backgroundColor))
        holder.titleProduct.setTextColor(holder.itemView.context.getColor(textColor))
    }



    override fun getItemCount(): Int {
       return models!!.size
    }
}