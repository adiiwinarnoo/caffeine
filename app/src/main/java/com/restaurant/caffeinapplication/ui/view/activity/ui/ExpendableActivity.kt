package com.restaurant.caffeinapplication.ui.view.activity.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.restaurant.caffeinapplication.databinding.ActivityExpendableBinding

class ExpendableActivity : AppCompatActivity() {

    lateinit var binding : ActivityExpendableBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpendableBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val groups = mutableListOf(
//            Group("1999, Juli", mutableListOf("10", "20"), mutableListOf(
//                    ChildItem("10", mutableListOf("Data diangka 10", "Data diangka 10 2", "Data diangka 10 3")),
//                    ChildItem("20", mutableListOf("Data diangka 20 A", "Data diangka 20 B", "Data diangka 20 C"))
//                ),
//                false
//            ),
//            // Add other groups as needed
//        )

//        val adapter = ExpendableAdapter(this, groups)
//        binding.expandableListView.setAdapter(adapter)
//
//        binding.expandableListView.setOnGroupExpandListener { groupPosition ->
//            groups[groupPosition].isExpanded = true
//            adapter.notifyDataSetChanged()
//        }
//
//        binding.expandableListView.setOnGroupCollapseListener { groupPosition ->
//            groups[groupPosition].isExpanded = false
//            adapter.notifyDataSetChanged()
//        }
//
//        adapter.setItemClickListener(object : ExpendableAdapter.ItemClickListener {
//            override fun onClick(view: View, position: Int,groupPosition: Int) {
//                // Get the group position and child position from the adapter's flat position
//                val flatPosition = binding.expandableListView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, position))
//                val localPosition = binding.expandableListView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, position))
//                val localVisibleRect = Rect()
//                binding.expandableListView.getLocalVisibleRect(localVisibleRect)
//                // Scroll to the item if it's not fully visible
//                if (!localVisibleRect.contains(localPosition, 1)) {
//                    binding.expandableListView.smoothScrollToPosition(flatPosition)
//                }
//            }
//        })
    }
}