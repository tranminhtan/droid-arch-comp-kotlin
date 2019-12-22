package com.test.app.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class RecyclingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun onViewRecycled()
}
