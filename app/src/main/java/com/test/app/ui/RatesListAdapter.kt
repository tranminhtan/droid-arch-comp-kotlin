package com.test.app.ui

import android.view.View
import androidx.annotation.UiThread
import androidx.databinding.ViewDataBinding
import com.test.app.BR
import com.test.app.R
import com.test.app.base.DataBindingRecyclerViewAdapter
import com.test.app.service.CurrencyRateRepository
import com.test.app.ui.list.RatesItem
import com.test.app.ui.list.RatesItemViewModel
import io.reactivex.Single
import java.util.concurrent.Callable

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
    fun moveSelectedItemToTop(item: RatesItem): Single<Boolean> {
        return Single.fromCallable(Callable {
            if (getList().isNotEmpty() && item != CurrencyRateRepository.BASE_RATES_ITEM) {
                val newList = getList().toMutableList()
                val currentPos = newList.indexOf(item)

                if (currentPos > 0) {
                    newList.remove(item).also {
                        newList.add(0, item)
                    }
                    setListWithoutNotifyChanged(newList)
                    notifyItemMoved(currentPos, 0)
                    return@Callable true
                }
            }
            false
        })
    }
}