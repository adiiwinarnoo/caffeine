package com.restaurant.caffeinapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.restaurant.caffeinapplication.data.model.IncomeModel
import com.restaurant.caffeinapplication.databinding.ItemIncomeBinding
import java.util.*

class IncomeAdapter (private var incomeList : ArrayList<IncomeModel>): RecyclerView.Adapter<IncomeAdapter.ListViewHolder>() {
    private lateinit var itemClickListener: ItemClickListener

    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemIncomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(incomeList[position])

        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    override fun getItemCount(): Int {
        return incomeList.size
    }

    inner class ListViewHolder(binding : ItemIncomeBinding) : RecyclerView.ViewHolder(binding.root) {
//        private var date = binding.tvDate
        private var name = binding.tvProductName

        fun bind(data: IncomeModel){
//            date.text = data.date
            name.text = data.productName
        }
    }

}