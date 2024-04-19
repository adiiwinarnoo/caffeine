package com.restaurant.caffeinapplication.adapter
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.restaurant.caffeinapplication.R
import com.restaurant.caffeinapplication.data.model.DataOrderItem
import com.restaurant.caffeinapplication.data.model.GroupIncomeOrder

class OrderExpendableAdapter(private val context: Context, private val groups: List<GroupIncomeOrder>) :
    BaseExpandableListAdapter() {

    private lateinit var orderExpendableItemClickListener: OrderExpendableItemClickListener

    fun setOrderExpendableItemClickListener(listener: OrderExpendableItemClickListener) {
        this.orderExpendableItemClickListener = listener
    }

    interface OrderExpendableItemClickListener {
        fun onClickDeliverOrder(view: View, groupPosition: Int, childPosition: Int, customerName: String,
            addressUser: String, productName: String, priceProduct: Int, orderNumber: Int, dateTime: String,
            deliveryCost: Int, totalPayment: Int, models: List<DataOrderItem?>?, totalGross: Int,paymentMethod: String)

        fun onClickPackagingOrder(view: View, groupPosition: Int, childPosition: Int, customerName: String,
                                  productName: String, priceProduct: Int, orderNumber: Int, dateTime: String,
            deliveryCost: Int, totalPayment: Int, paymentMethod: String, models: List<DataOrderItem?>?, totalGross: Int)
    }



    private lateinit var itemClickListener: ItemClickListener

    interface ItemClickListener {
        fun onClick(view: View, position: Int,groupPosition: Int)
    }

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun getGroup(groupPosition: Int): Any = groups[groupPosition]

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true

    override fun hasStableIds(): Boolean = true

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.group_item, parent, false)
        }
        val groupTitleTextView: TextView = view!!.findViewById(R.id.groupTitle)
        val totalAmountText : TextView = view.findViewById(R.id.tv_total_income_year)
        val group = getGroup(groupPosition) as GroupIncomeOrder
        groupTitleTextView.text = group.title
        totalAmountText.text = group.totalIncome.toString()

        // Ubah ikon berdasarkan status ekspansi grup
        val groupIcon = if (isExpanded) R.drawable.ic_up else R.drawable.spinner_ic
        totalAmountText.setCompoundDrawablesWithIntrinsicBounds(0, 0, groupIcon, 0)

        return view
    }
    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildrenCount(groupPosition: Int): Int {
        return if (groups[groupPosition].isExpanded) groups[groupPosition].children.size else 0
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return groups[groupPosition].recyclerViewItem[childPosition]
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val group = getGroup(groupPosition) as GroupIncomeOrder

        val clickedGroupPosition = groupPosition

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_income_date, parent, false)
        }

        val childTitleTextView: TextView = view!!.findViewById(R.id.childTitle)
        val totalIncomeMonth: TextView = view!!.findViewById(R.id.tv_total_income_month)
        childTitleTextView.text = group.children[childPosition]
        totalIncomeMonth.text = group.childrenIncomeDate[childPosition]

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)

        // Check if this child should show the RecyclerView or not
        if (group.recyclerViewItem != null) {
            val childItem = group.recyclerViewItem.find { it.date == group.children[childPosition] }
            if (childItem != null) {
                val adapter = IncomeOrderAdapter(childItem.data!!) // Replace dataForRecyclerView with the appropriate data
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = adapter

                adapter.setItemClickListener(object : IncomeOrderAdapter.IncomeOrderItemClickListener{
                    override fun onClickDeliverOrder(view: View, position: Int, customerName: String,
                        addressUser: String, productName: String, priceProduct: Int, orderNumber: Int,
                        dateTime: String, deliveryCost: Int, totalPayment: Int, models: List<DataOrderItem?>?,
                        totalGross: Int,paymentMethod: String) {
                        orderExpendableItemClickListener.onClickDeliverOrder(view, groupPosition,
                            childPosition, customerName, addressUser, productName, priceProduct,
                            orderNumber, dateTime, deliveryCost, totalPayment, models, totalGross,paymentMethod)
                    }

                    override fun onClickPackagingOrder(view: View, position: Int, customerName: String,
                        productName: String, priceProduct: Int, orderNumber: Int,
                        dateTime: String, deliveryCost: Int, totalPayment: Int, paymentMethod: String,
                        models: List<DataOrderItem?>?, totalGross: Int) {
                        orderExpendableItemClickListener.onClickPackagingOrder(view, position,
                            childPosition, customerName, productName, priceProduct,
                            orderNumber, dateTime, deliveryCost, totalPayment, paymentMethod,
                            models, totalGross)
                    }

                })

            } else {
                recyclerView.visibility = View.GONE
            }
        } else {
            recyclerView.visibility = View.GONE
        }

        totalIncomeMonth.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.spinner_ic, 0)

        view.setOnClickListener {
            Log.d("DATA-PACKAGING", "packagingOrderDetailsIncome-adapter: $clickedGroupPosition")
            itemClickListener.onClick(it, childPosition, clickedGroupPosition)
            // Toggle RecyclerView visibility when clicked
            if (recyclerView.visibility == View.VISIBLE) {
                recyclerView.visibility = View.GONE
                group.isExpanded = false
                totalIncomeMonth.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.spinner_ic, 0)
            } else {
                group.isExpanded = true
                totalIncomeMonth.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_up, 0)
                recyclerView.visibility = View.VISIBLE
            }
        }

        return view
    }





    override fun getGroupCount(): Int = groups.size
}

