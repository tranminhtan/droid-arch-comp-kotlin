package com.task.app.ui.list

import androidx.recyclerview.widget.DiffUtil

class RatesDiffUtils : DiffUtil.ItemCallback<RatesItem>() {
    override fun areItemsTheSame(oldItem: RatesItem, newItem: RatesItem): Boolean {
        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(oldItem: RatesItem, newItem: RatesItem): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: RatesItem, newItem: RatesItem): Any? {
        return newItem.rate
    }
}