package com.restaurant.caffeinapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.restaurant.caffeinapplication.databinding.ItemIncomeDateBinding
import java.util.*

class IncomeDateAdapter (private var incomeDateList : ArrayList<String>): RecyclerView.Adapter<IncomeDateAdapter.ListViewHolder>() {
    private lateinit var itemClickListener: ItemClickListener

    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemIncomeDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(incomeDateList[position])

        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    override fun getItemCount(): Int {
        return incomeDateList.size
    }

    inner class ListViewHolder(binding : ItemIncomeDateBinding) : RecyclerView.ViewHolder(binding.root) {
//        private var date = binding.tvDate

        fun bind(dateValue: String){
//            date.text = dateValue
        }
    }

}