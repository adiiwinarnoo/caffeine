package com.restaurant.caffeinapplication.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.restaurant.caffeinapplication.R
import com.restaurant.caffeinapplication.data.model.GroupIncomeCoupon
import com.restaurant.caffeinapplication.data.model.GroupIncomeSubscribe

class IncomeCouponAdapter(private val context: Context, private val groups: List<GroupIncomeCoupon>) :
    BaseExpandableListAdapter() {

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
        val group = getGroup(groupPosition) as GroupIncomeCoupon
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
        val group = getGroup(groupPosition) as GroupIncomeCoupon

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.child_item, parent, false)
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
                val adapter = CouponAdapter(childItem.data) // Replace dataForRecyclerView with the appropriate data
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = adapter
            } else {
                recyclerView.visibility = View.GONE
            }
        } else {
            recyclerView.visibility = View.GONE
        }

        totalIncomeMonth.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.spinner_ic, 0)

        view.setOnClickListener {
            itemClickListener.onClick(it, childPosition, groupPosition)
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

class IncomeSubscribeAdapter(private val context: Context, private val groups: List<GroupIncomeSubscribe>) :
    BaseExpandableListAdapter() {

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
        val group = getGroup(groupPosition) as GroupIncomeSubscribe
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
        val group = getGroup(groupPosition) as GroupIncomeSubscribe

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.child_item, parent, false)
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
                val adapter = SubscribeAdapter(childItem.data) // Replace dataForRecyclerView with the appropriate data
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = adapter
            } else {
                recyclerView.visibility = View.GONE
            }
        } else {
            recyclerView.visibility = View.GONE
        }

        totalIncomeMonth.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.spinner_ic, 0)

        view.setOnClickListener {
            itemClickListener.onClick(it, childPosition, groupPosition)
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

