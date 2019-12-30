package com.task.app.ui.list

import android.view.View
import androidx.annotation.UiThread
import androidx.databinding.ViewDataBinding
import com.task.app.BR
import com.task.app.R
import com.task.app.base.DataBindingRecyclerViewAdapter
import com.task.app.service.CurrencyRateRepository
import io.reactivex.Single

class RatesListAdapter(private val ratesItemViewModel: RatesItemViewModel) : DataBindingRecyclerViewAdapter<RatesItem>() {

    override fun getLayoutIdForViewType(viewType: Int): Int {
        return R.layout.item_currency_rate
    }

    override fun setData(binder: ViewDataBinding, view: View, viewType: Int, data: RatesItem) {
        binder.setVariable(BR.item, data)
        binder.setVariable(BR.itemVm, ratesItemViewModel)
    }

    override fun areItemsTheSame(oldItem: RatesItem, newItem: RatesItem): Boolean {
        return oldItem.code == newItem.code
    }

    override fun getChangePayload(oldItem: RatesItem, newItem: RatesItem): Any? {
        return newItem.rate
    }

    @UiThread
    override fun moveSelectedItemToTop(item: RatesItem): Single<List<RatesItem>> {
        return Single.fromCallable { moveSelectedItemToTop(getList(), item) }
    }

    private fun moveSelectedItemToTop(list: List<RatesItem>, item: RatesItem): List<RatesItem> {
        val newList = list.toMutableList()
        if (newList.isNotEmpty() && item != CurrencyRateRepository.BASE_RATES_ITEM) {
            val currentPos = newList.indexOf(item)

            return if (currentPos > 0) {
                newList.remove(item).also {
                    newList.add(0, item)
                }
                setListWithoutNotifyChanged(newList)
                notifyItemMoved(currentPos, 0)

                // Update editable state
                newList[0] = newList[0].copy(editable = true)
                newList[1] = newList[1].copy(editable = false)
                newList
            } else {
                emptyList()
            }
        }
        return emptyList()
    }
}