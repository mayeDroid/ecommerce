package com.example.ecommerce.utilities

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

// to add some extra spaces between our recycler view items
class VerticalItemDecoration (private val amount: Int = 30): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
       outRect.bottom = amount
    }
}