package com.restaurant.caffeinapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.restaurant.caffeinapplication.data.model.ResultItemNotice
import com.restaurant.caffeinapplication.databinding.ItemNoticeBinding

class NoticeAdapter(private var noticeList : List<ResultItemNotice?>?): RecyclerView.Adapter<NoticeAdapter.ListViewHolder>() {

    private lateinit var itemClickListener: ItemClickListener

    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(noticeList!![position]!!)

        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    override fun getItemCount(): Int {
        return noticeList!!.size
    }

    inner class ListViewHolder(binding :ItemNoticeBinding) : RecyclerView.ViewHolder(binding.root) {
        private var date = binding.tvDate
        private var title = binding.tvTitle

        fun bind(item : ResultItemNotice){
            date.text = "[${item.dateString}]"
            title.text = "[${item.title}]"
        }
    }

}