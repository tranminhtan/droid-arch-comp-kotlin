package com.task.app.base

import androidx.databinding.ViewDataBinding

/**
 * A Custom RecyclerView.ViewHolder that holds the view and ViewModel.
 */
open class DataBindingViewHolder<T> constructor(val binder: ViewDataBinding) :
    RecyclingViewHolder(binder.root) {

    open fun setData(item: T) {}

    override fun onViewRecycled() {
        // No-op
    }
}